package com.sk89q.craftbook.music.parser;

import java.io.BufferedReader;
import java.io.IOException;

import com.sk89q.craftbook.music.MidiDataObject;

public class MidiDataParser1
{
	public static MidiDataObject parse(BufferedReader br, int maxTracks) throws IOException
	{
		final int maxLines = 10 + maxTracks;
		
		String title = "";
		String author = "";
		int octave_start = 2;
		int rate = -1;
		byte playMode = 0;
		
		boolean trackInfo = false;
		int[] instruments = null;
		int[] playTracks = null;
		
		for(int i = 0; i < maxLines; i++)
		{
			String fileLine = br.readLine();
			if(fileLine == null)
				break;
			
			String[] line = fileLine.split(":", 2);
			if(line.length < 2 && !line[0].equalsIgnoreCase("tracks"))
				return null;
			
			if(trackInfo)
			{
				if(line[0].charAt(0) != ' ')
					trackInfo = false;
				else
				{
					int trackNum;
					try
					{
						trackNum = Integer.parseInt(line[0].substring(1)) - 1;
					}
					catch(NumberFormatException e)
					{
						return null;
					}
					
					int type = DefaultMusicParser.getType(line[1]);
					if(trackNum >= 0 && trackNum < instruments.length && type >= 0)
					{
						instruments[trackNum] = type;
					}
					
					continue;
				}
			}
			
			if(line[0].equalsIgnoreCase("title"))
				title = line[1];
			else if(line[0].equalsIgnoreCase("author"))
				author = line[1];
			else if(line[0].equalsIgnoreCase("ticks-per-beat"))
			{
				try
				{
					rate = Integer.parseInt(line[1]);
				}
				catch(NumberFormatException e)
				{
					return null;
				}
			}
			else if(line[0].equalsIgnoreCase("octave-start"))
			{
				try
				{
					octave_start = Integer.parseInt(line[1]);
				}
				catch(NumberFormatException e)
				{
					return null;
				}
			}
			else if(line[0].equalsIgnoreCase("tracks") && instruments == null)
			{
				instruments = new int[maxTracks];
				trackInfo = true;
			}
			else if(line[0].equalsIgnoreCase("play-tracks"))
			{
				String[] tracks = line[1].split(",");
				playTracks = new int[tracks.length];
				for(int j = 0; j < tracks.length; j++)
				{
					try
					{
						playTracks[j] = Integer.parseInt(tracks[j]) - 1;
					}
					catch(NumberFormatException e)
					{
						return null;
					}
				}
			}
			else if(line[0].equalsIgnoreCase("play-mode"))
			{
				try
				{
					playMode = Byte.parseByte(line[1]);
				}
				catch(NumberFormatException e)
				{
					return null;
				}
			}
		}
		
		if(playTracks != null)
		{
			if(instruments == null)
				instruments = new int[maxTracks];
			
			int cur = 0;
			for(int i = 0; i < playTracks.length; i++)
			{
				if(playTracks[i] < 0 || playTracks[i] >= instruments.length || playTracks[i] < cur)
					continue;
				
				for(; cur < playTracks[i] && cur < instruments.length; cur++)
				{
					instruments[cur] = -1;
				}
				
				cur++;
			}
		}
		
		if(octave_start <= 2)
			octave_start = 2;
		else if(octave_start >= 3)
			octave_start = 3;
		
		MidiDataObject midiData = new MidiDataObject(title, author, octave_start, rate, instruments, playMode);
		
		return midiData;
	}
}
