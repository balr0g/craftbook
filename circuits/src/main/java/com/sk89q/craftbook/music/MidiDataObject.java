package com.sk89q.craftbook.music;

public class MidiDataObject
{
	public final String TITLE;
	public final String AUTHOR;
	public final int OCTAVE_START;
	public final int RATE;
	public final int[] INSTRUMENTS;
	public final byte PLAY_MODE;
	
	public MidiDataObject(String title, String author, int octave_start, int rate, int[] instruments, byte playMode)
	{
		TITLE = title;
		AUTHOR = author;
		OCTAVE_START = octave_start;
		RATE = rate;
		PLAY_MODE = playMode;
		
		INSTRUMENTS = instruments;
	}
}
