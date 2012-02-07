package com.sk89q.craftbook.music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.sk89q.craftbook.music.media.ChatAdMedia;
import com.sk89q.craftbook.music.media.ExternalMedia;
import com.sk89q.craftbook.music.media.Media;

public class Playlist
{
	private static final Logger logger = Logger.getLogger("Minecraft.CraftBook");
	
	protected final IMusicPlayer musicPlayer;
	
	private final String NAME;
	
	private ArrayList<Media> mediaList = new ArrayList<Media>();
	private int position = 0;
	
	public Playlist(IMusicPlayer musicPlayer)
	{
		this.musicPlayer = musicPlayer;
		NAME = "";
	}
	
	public Playlist(IMusicPlayer musicPlayer, String name)
	{
		this.musicPlayer = musicPlayer;
		NAME = name;
		loadPlaylist();
	}
	
	private void loadPlaylist()
	{
		position = 0;
		if(NAME.length() == 0)
			return;
		
		File file = new File("cbmusic" + File.separator
							+ "playlists" + File.separator
							+ NAME + ".txt");
		
		if(!file.exists())
			return;
		
		FileInputStream fs = null;
		BufferedReader br = null;
		
		try
		{
	    	fs = new FileInputStream(file);
	    	br = new BufferedReader(new InputStreamReader(fs));
	    	
	    	for(int i = 0; i < musicPlayer.getConfig().maxPlaylistTracks; i++)
	    	{
	    		String line = br.readLine();
	    		if(line == null || line.isEmpty())
	    			break;
	    		
	    		ExternalMedia media;
	    		
	    		if(line.length() > 4 && line.substring(0, 3).equalsIgnoreCase("ad:"))
	    		{
	    			String[] args = line.split(":", 3);
	    			int delay = 10;
	    			String ad;
	    			if(args.length > 2)
	    			{
	    				try
	    				{
	    					delay = Integer.parseInt(args[1]);
	    				}
	    				catch(NumberFormatException e)
	    				{
	    					
	    				}
	    				ad = args[2];
	    			}
	    			else
	    			{
	    				ad = args[1];
	    			}
	    			
	    			media = new ChatAdMedia(musicPlayer, ad, delay);
	    		}
	    		else
	    		{
	    			media = musicPlayer.parseExternalData(line);
	    		}
		    	
		    	addMedia(media);
	    	}
		}
		catch(FileNotFoundException e)
		{
			logger.warning("CraftBook playlist not found: "+NAME);
			return;
		}
		catch(IOException e)
		{
			logger.warning("Error loading CraftBook playlist: "+NAME);
			return;
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
		
		if(getSize() == 0)
		{
			logger.warning("Error. CraftBook playlist is empty: "+NAME);
		}
	}
	
	public void addMedia(Media media)
	{
		if(media == null)
			return;
		
		mediaList.add(media);
	}
	
	public Media getCurrentMedia()
	{
		return mediaList.get(position);
	}
	
	public Media getNext()
	{
		if(position + 1 >= mediaList.size())
			return null;
		
		position++;
		
		return getCurrentMedia();
	}
	
	public Media getPrevious()
	{
		if(position - 1 < 0)
			return null;
		
		position--;
		
		return getCurrentMedia();
	}
	
	public Media jumpTo(int index)
	{
		if(index < 0 || index >= mediaList.size())
			return null;
		
		position = index;
		
		return getCurrentMedia();
	}
	
	public int getCurrentPosition()
	{
		return position;
	}
	
	public int getSize()
	{
		return mediaList.size();
	}
}
