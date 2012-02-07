package com.sk89q.craftbook.music.media;

import java.util.ArrayList;

import com.sk89q.craftbook.music.IMusicPlayer;
import com.sk89q.craftbook.music.MusicNote;
import com.sk89q.craftbook.music.MusicNoteKey;
import com.sk89q.craftbook.music.parser.DefaultMusicParser;

public class SimpleTuneMedia extends Media
{
	private ArrayList<MusicNoteKey> musicKeys;
	
	private int currentTrackPos = 0;
	
	public SimpleTuneMedia(IMusicPlayer musicPlayer, String data)
	{
		super(musicPlayer);
		
		String[] args = data.split(":", 2);
		if(args.length > 1)
		{
			rate = musicPlayer.getLimitedRate(args[0]);
			data = args[1];
		}
		
		musicKeys = DefaultMusicParser.parseTune1(data);
		
		reset();
	}
	
	public boolean playNextNote()
	{
		if(currentTrackPos >= musicKeys.size())
		{
			finished = true;
			return false;
		}
		
		if(currentNote == musicKeys.get(currentTrackPos).getKey())
		{
			ArrayList<MusicNote> notes = musicKeys.get(currentTrackPos).getNotes();
			
			musicPlayer.playNotes(notes);
			
			currentTrackPos++;
		}
		
		
		currentNote++;
		return true;
	}
	
	public void reset()
	{
		currentNote = 0;
		currentTrackPos = 0;
		finished = musicKeys == null;
	}
}
