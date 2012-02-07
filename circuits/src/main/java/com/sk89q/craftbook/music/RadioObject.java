package com.sk89q.craftbook.music;

import com.sk89q.worldedit.WorldVector;

public class RadioObject
{

	public WorldVector pos;
	public WorldVector signPos;
	
	public boolean sendMessages;
	
	public RadioObject(WorldVector pos, WorldVector signPos, boolean messages)
	{
		this.pos = pos;
		this.signPos = signPos;
		sendMessages = messages;
	}
}
