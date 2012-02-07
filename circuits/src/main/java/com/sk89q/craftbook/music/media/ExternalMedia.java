package com.sk89q.craftbook.music.media;

import java.io.File;

import com.sk89q.craftbook.music.IMusicPlayer;

public abstract class ExternalMedia extends Media
{
	protected final String mediaDir = "cbmusic" + File.separator;
	protected final String song;
	
	protected String title;
	protected String author;
	
	protected boolean forceMessage = false;
	
	public ExternalMedia(IMusicPlayer musicPlayer, String song)
	{
		super(musicPlayer);
		this.song = song;
	}
	
	public boolean loadSong()
	{
		return false;
	}
	
	public String getInfoMessage()
	{
		String title;
		if(getTitle().isEmpty())
			title = getSong();
		else
			title = getTitle();
		
		String message = " §6"+musicPlayer.getConfig().nowPlaying+"<br>§6> §f"+title;
		
		if(!getAuthor().isEmpty())
			message += " §6by §f"+getAuthor();
		
		return message;
	}
	
	public String getSong()
	{
		return song;
	}
	
	public String getTitle()
	{
		if(title == null)
			return "";
		return title;
	}
	
	public String getAuthor()
	{
		if(author == null)
			return "";
		return author;
	}
	
	public boolean isForcedMessage()
	{
		return forceMessage;
	}
}
