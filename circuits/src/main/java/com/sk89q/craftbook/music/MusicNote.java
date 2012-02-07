package com.sk89q.craftbook.music;

public class MusicNote
{
	public final int type;
	public final byte pitch;
	
	public MusicNote(int type, byte pitch)
	{
		this.type = type;
		this.pitch = pitch;
	}
	
	public int getType()
	{
		return type;
	}
	
	public byte getPitch()
	{
		return pitch;
	}
}
