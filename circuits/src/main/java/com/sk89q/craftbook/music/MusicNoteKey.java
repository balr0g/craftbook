package com.sk89q.craftbook.music;


import com.sk89q.craftbook.music.MusicNote;

import java.util.ArrayList;


public class MusicNoteKey
{
	private ArrayList<MusicNote> notes = new ArrayList<MusicNote>();
	private final int key;
	
	public MusicNoteKey(int key)
	{
		this.key = key;
	}
	
	public void addNote(int type, byte pitch)
	{
		addNote(new MusicNote(type, pitch));
	}
	
	public void addNote(MusicNote note)
	{
		notes.add(note);
	}
	
	public ArrayList<MusicNote> getNotes()
	{
		return notes;
	}
	
	public boolean hasPitch(int pitch)
	{
		for(MusicNote note : notes)
		{
			if(note.getPitch() == pitch)
				return true;
		}
		
		return false;
	}
	
	public int getKey()
	{
		return key;
	}
}
