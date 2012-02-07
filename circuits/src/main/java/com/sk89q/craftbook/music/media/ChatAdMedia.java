package com.sk89q.craftbook.music.media;

import com.sk89q.craftbook.music.IMusicPlayer;

public class ChatAdMedia extends ExternalMedia
{
	private final int delay;
	private long startTime = 0;
	
	public ChatAdMedia(IMusicPlayer musicPlayer, String message, int delay)
	{
		super(musicPlayer, message);
		
		forceMessage = true;
		
		if(delay < 0)
			delay = 0;
		else if(delay > 3600)
			delay = 3600;
		
		this.delay = delay * 1000;
	}
	
	public boolean playNextNote()
	{
		if(startTime == 0)
		{
			musicPlayer.sendMessage(song);
			startTime = System.currentTimeMillis();
		}
		else if(System.currentTimeMillis() - startTime >= delay)
		{
			finished = true;
			return false;
		}
		return true;
	}
	
	public boolean loadSong()
	{
		finished = false;
		return true;
	}
	
	public void reset()
	{
		startTime = 0;
		finished = false;
	}
	
	public String getInfoMessage()
	{
		return getSong();
	}
	
	public String getSong()
	{
		if(song == null || song.isEmpty())
			return "§7<empty ad>";
		
		return song;
	}
}
