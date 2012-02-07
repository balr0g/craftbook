package com.sk89q.craftbook.music.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import com.sk89q.craftbook.music.IMusicPlayer;
import com.sk89q.craftbook.music.MidiDataObject;
import com.sk89q.craftbook.music.MusicMidiTrack;
import com.sk89q.craftbook.music.MusicNote;
import com.sk89q.craftbook.music.parser.MidiDataParser1;

public class MidiMedia extends ExternalMedia
{
	private MusicMidiTrack[] midiTracks;
	private int midiResolution;
	
	private boolean midiStarted = false;
	private byte errorType = 0;
	
	public MidiMedia(IMusicPlayer musicPlayer, String song)
	{
		super(musicPlayer, song);
	}
	
	public boolean loadSong()
	{
		finished = true;
		currentNote = 0;
		
		File file = new File(mediaDir + song + ".mid");
		
		if (!file.exists())
			return false;
		
		midiStarted = true;
		
		Sequence sequence = null;
		try
		{
			sequence = MidiSystem.getSequence(file);
		}
		catch(InvalidMidiDataException e)
		{
			errorType = 1;
			return false;
		}
		catch(IOException e)
		{
			errorType = 2;
			return false;
		}
		
		errorType = 0;
		
		if(sequence == null)
			return false;
		
		Track[] tracks = sequence.getTracks();
		
		if(tracks == null)
			return false;
		
		midiResolution = sequence.getResolution() / 24;
		
		int trackSize = tracks.length;
		if(trackSize > musicPlayer.getConfig().maxMidiTracks)
			trackSize = musicPlayer.getConfig().maxMidiTracks;
		
		MidiDataObject dataObject = loadMidiData(trackSize);
		if(dataObject != null)
		{
			title = dataObject.TITLE;
			author = dataObject.AUTHOR;
		}
		
		if(rate < 1)
		{
			if(dataObject == null || dataObject.RATE < 0)
				rate = 4;
			else if(dataObject != null)
				rate = dataObject.RATE;
		}
		
		if(rate > musicPlayer.getConfig().maxRate)
			rate = musicPlayer.getConfig().maxRate;
		
		midiTracks = new MusicMidiTrack[trackSize];
		
		boolean allNull = true;
		for(int i = 0; i < trackSize; i++)
		{
			Track track = tracks[i];
			
			if(track.size() < 4 || (dataObject != null && dataObject.INSTRUMENTS != null && dataObject.INSTRUMENTS[i] < 0) )
    			continue;
			
			midiTracks[i] = new MusicMidiTrack(track);
			if(dataObject != null)
			{
				if(dataObject.INSTRUMENTS != null)
					midiTracks[i].instrument = dataObject.INSTRUMENTS[i];
			}
			
			allNull = false;
		}
		
		if(allNull)
		{
			midiTracks = null;
			return false;
		}
		
		finished = false;
		
		return true;
	}
	
	private MidiDataObject loadMidiData(int maxTracks)
	{
		File file = new File(mediaDir + song + ".mdata");
		
		if(!file.exists())
			return null;
		
		FileInputStream fs = null;
		BufferedReader br = null;
		
		MidiDataObject midiData = null;
		
		try
		{
			fs = new FileInputStream(file);
	    	br = new BufferedReader(new InputStreamReader(fs));
	    	
	    	//type of music file
	    	String fileFormat = br.readLine();
	    	if(fileFormat.equalsIgnoreCase("fmt1"))
	    	{
	    		midiData = MidiDataParser1.parse(br, maxTracks);
	    	}
		}
		catch(FileNotFoundException e)
		{
			return null;
		}
		catch(IOException e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(br != null)
					br.close();
			}
			catch(IOException e)
			{
				
			}
		}
		
		return midiData;
	}
	
	public boolean playNextNote()
	{
		boolean allFinished = true;
		
		double tick = currentNote * midiResolution;
		
		for(int i = 0; i < midiTracks.length; i++)
		{
			MusicMidiTrack track = midiTracks[i];
			if(track == null || track.isFinished())
				continue;
			
			allFinished = false;
			
			ArrayList<MusicNote> notes = track.nextTick(tick);
			
			if(notes == null || notes.size() == 0)
				continue;
			
			midiStarted = false;
			
			musicPlayer.playNotes(notes);
		}
		
		currentNote += rate;
		
		if(allFinished)
		{
			finished = true;
			return false;
		}
		else if(midiStarted)
		{
			musicPlayer.tick();
		}
		
		return true;
	}
	
	public void reset()
	{
		midiStarted = true;
		
		currentNote = 0;
		
		if(midiTracks != null)
		{
			for(int i = 0; i < midiTracks.length; i++)
			{
				if(midiTracks[i] != null)
					midiTracks[i].reset();
			}
			finished = false;
		}
		else
		{
			finished = true;
		}
	}
	
	public int getRate()
	{
		return 0;
	}
	
	public byte getErrorType()
	{
		return errorType;
	}
}
