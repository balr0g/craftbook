package com.sk89q.craftbook.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MusicMidiTrack
{
	public final Track TRACK;
	
	public static boolean enableChannel10 = false; //percussion channel
	
	private boolean finished = false;
	private int octave = 2; //lowest octave
	
	public int instrument = -1;
	public int position = 0;
	
	public byte playMode = 0;
	
	private final Map<Integer, Integer> programChanges = new HashMap<Integer, Integer>();
	
	private static int[] instruments = {
        0, 0, 0, 0,  0, 0, 0, 0,
        0, 0, 0, 0,  0, 0, 0, 0,
        0, 0, 0, 0,  0, 0, 0, 0,
        0, 0, 0, 0,  0, 0, 0, 0,
        
        1, 0, 1, 1,  0, 2, 0, 2,
        1, 2, 1, 2,  1, 2, 1, 1,
        2, 1, 2, 2,  0, 2, 2, 0,
        2, 0, 2, 1,  1, 1, 1, 1,
        
        1, 1, 0, 0,  2, 2, 0, 0,
        0, 2, 0, 0,  0, 0, 0, 0,
        0, 0, 0, 0,  0, 0, 0, 0,
        0, 0, 0, 0,  0, 0, 0, 0,
        
        0, 0, 0, 0,  0, 0, 0, 0,
        0, 0, 0, 0,  0, 0, 0, 0,
        1, 1, 1, 1,  1, 1, 1, 1,
        1, 1, 1, 1,  1, 2, 4, 3,
    };
	
	private static int[] percussion = {
        1, 1, 1, 2,  3, 2, 1, 3,
        1, 3, 1, 3,  1, 1, 3, 1,
        3, 3, 3, 3,  3, 0, 3, 3,
        3, 1, 1, 1,  1, 1, 1, 1,
        
        3, 3, 3, 3,  4, 4, 3, 3,
        3, 3, 3, 1,  1, 3, 3, 2,
        4, 4, 3, 1,  1, 4, 4, 4,
        4, 4, 4, 4,  4, 4, 4, 4,
        
        4, 4, 4, 4,  4, 4, 4, 4,
        4, 4, 4, 4,  4, 4, 4, 4,
        4, 4, 4, 4,  4, 4, 4, 4,
        4, 4, 4, 4,  4, 4, 4, 4,
        
        4, 4, 4, 4,  4, 4, 4, 4,
        4, 4, 4, 4,  4, 4, 4, 4,
        4, 4, 4, 4,  4, 4, 4, 4,
        4, 4, 4, 4,  4, 4, 4, 4,
	};
	
	public MusicMidiTrack(Track track)
	{
		TRACK = track;
	}
	
	public ArrayList<MusicNote> nextTick(double tick)
	{
		if(tick >= TRACK.ticks())
		{
			finished = true;
			return null;
		}
		
		ArrayList<MusicNote> notes = new ArrayList<MusicNote>();
		
		while(position < TRACK.size() - 1)
		{
			position++;
			
			MidiEvent event = TRACK.get(position);
			if(event == null)
				continue;
			
			if(TRACK.get(position).getTick() > tick)
			{
				position--;
				break;
			}
			
			if(event.getMessage() instanceof MetaMessage)
			{
				if(event.getMessage().getStatus() == 0x2F)
				{
					finished = true;
					break;
				}
				continue;
			}
			
			if(!(event.getMessage() instanceof ShortMessage))
				continue;
			
			ShortMessage message = (ShortMessage) event.getMessage();
			
			if(message.getCommand() != ShortMessage.NOTE_ON)
			{
				if(message.getCommand() == ShortMessage.PROGRAM_CHANGE)
				{
					programChanges.put(message.getChannel(), message.getData1());
				}
				continue;
			}
			
			int note = message.getData1();
			
			switch(playMode)
			{
				case 1:
					note = getShiftedOctaveNote(note);
					break;
				case 2:
					note = getCappedOctaveNote(note);
					break;
				default:
					note = getCycledOctaveNote(note);
			}
			
			if(note < 0)
				continue;
			
			int instrum;
			if(instrument == -1)
				instrum = getInstrument(message.getChannel());
			else
				instrum = instrument;
			
			if(instrum < 0)
				continue;
			
			notes.add(new MusicNote(instrum, (byte)note));
		}
		
		return notes;
	}
	
	public void reset()
	{
		finished = false;
		position = 0;
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public int getOctave()
	{
		return octave;
	}
	
	public void setOctave(int octave)
	{
		this.octave = octave;
	}
	
	/*
	 * Any note lower than the lowest note become the lowest note.
	 * Any note higher than the highest note become the highest note.
	 */
	private int getCappedOctaveNote(int note)
	{
		int min = 18 + 12 * octave;
		int pitch = note - min;
		
		if(pitch < 0)
			pitch = 0;
		else if(pitch > 24)
			pitch = 24;
		
		return pitch;
	}
	
	/*
	 * Shifts octaves lower than the min octave to the min octave and
	 * octaves higher than the max octave to the max octave. Other octaves
	 * inbetween are left alone.
	 */
	private int getShiftedOctaveNote(int note)
	{
		int min = 18 + 12 * octave;
		int pitch = note - min;
		
		while(pitch < 0)
		{
			pitch += 12;
		}
		while(pitch > 24)
		{
			pitch -= 12;
		}
		
		return pitch;
	}
	
	/*
	 * Cycles through the octaves. Sets pairs of octaves as the same high-low
	 * octaves.
	 * The code is easier to understand than me attempting to explain it if
	 * you know what % does.
	 */
	private int getCycledOctaveNote(int note)
	{
		return (note - 6) % 24;
	}
	
	/*
	 * Converts instrument into a valid Minecraft instrument
	 */
	private int getInstrument(int channel)
	{
		Integer programChange = programChanges.get(channel);
		
		if(programChange == null || programChange < 0 || programChange >= instruments.length)
			return 0;
		
		if(channel == 9)
		{
			if(!enableChannel10)
				return -1;
			
			if(programChange < percussion.length)
				return percussion[programChange];
		}
		
		return instruments[programChange];
	}
	
	public static boolean setPercussion(String[] values)
	{
		return setInstrumentList(values, false);
	}
	
	public static boolean setInstruments(String[] values)
	{
		return setInstrumentList(values, true);
	}
	
	private static boolean setInstrumentList(String[] values, boolean isInstrument)
	{
		if(values == null || values.length != 128)
			return false;
		
		int[] inst = new int[128];
		try
		{
			for(int i = 0; i < 128; i++)
			{
				inst[i] = Integer.parseInt(values[i]);
				if(inst[i] < 0 || inst[i] > 4)
					return false;
			}
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		
		if(isInstrument)
			instruments = inst;
		else
			percussion = inst;
		
		return true;
	}
}
