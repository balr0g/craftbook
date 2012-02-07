package com.sk89q.craftbook.music.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import com.sk89q.craftbook.music.MusicNoteKey;

public class DefaultMusicParser
{
	public static ArrayList<MusicNoteKey> parse(BufferedReader br, int maxDuration, int maxLines) throws IOException
	{
		MusicNoteKey[] keys = new MusicNoteKey[maxDuration];
		
		int type = -1;
		int maxKey = 0;
		for(int i = 0; i < maxLines; i++)
		{
			String line = br.readLine();
			
			if(line == null || line.length() == 0)
			{
				if(type < 0)
				{
					//eof (or bad format)
					break;
				}
				
				type = -1;
				continue;
			}
			
			if(line.length() < 3)
				break;
			
			if(type < 0)
			{
				type = getType(line);
				
				if(type < 0)
					break;
				
				//next line
				continue;
			}
			
			int basePitch = getPitchType(line.substring(0, 2));
			
			if(basePitch < -1)
				break;
			
			int curKey = 0;
			for(int j = 2; j < line.length(); j++)
			{
				int pitch = 0;
				switch(line.charAt(j))
				{
					case '|':
						continue;
					case ' ':
					case '-':
						curKey++;
						continue;
					case '9':
					case '8':
					case '7':
					case '6':
					case '5':
						if(basePitch == 0)
							pitch = 5;
						else
							pitch = 4;
						curKey++;
						break;
					case '4':
						pitch = 4;
						curKey++;
						break;
					case '3':
					case '2':
					case '1':
					case '0':
						pitch = 3;
						curKey++;
						break;
					default:
						continue;
				}
				
				if(pitch < 3)
					break;
				
				if(keys[curKey] == null)
				{
					keys[curKey] = new MusicNoteKey(curKey);
				}
				
				keys[curKey].addNote(type, (byte) (basePitch + (pitch - 3) * 12) );
				
				if(curKey > maxKey)
					maxKey = curKey;
			}
		}
		
		ArrayList<MusicNoteKey> musicKeys = new ArrayList<MusicNoteKey>();
		
		for(int i = 0; i < maxKey; i++)
		{
			if(keys[i] == null)
				continue;
			
			musicKeys.add(keys[i]);
		}
		
		if(musicKeys.size() == 0)
			return null;
		
		return musicKeys;
	}
	
	public static ArrayList<MusicNoteKey> parse2(BufferedReader br, int maxDuration, int maxLines) throws IOException
	{
		MusicNoteKey[] keys = new MusicNoteKey[maxDuration];
		
		int type = -1;
		int maxKey = 0;
		for(int i = 0; i < maxLines; i++)
		{
			String line = br.readLine();
			
			if(line == null || line.length() == 0)
			{
				if(type < 0)
				{
					//eof (or bad format)
					break;
				}
				
				type = -1;
				continue;
			}
			
			if(line.length() < 3)
				break;
			
			if(type < 0)
			{
				type = getType(line);
				
				if(type < 0)
					break;
				
				//next line
				continue;
			}
			
			if(line.charAt(0) == '#')
			{
				//comment line
				continue;
			}
			
			int octave = 2;
			
			try
			{
				octave = Integer.parseInt(""+line.charAt(0));
			}
			catch(NumberFormatException e)
			{
				break;
			}
			
			
			int curKey = 0;
			for(int j = 1; j < line.length(); j++)
			{
				int pitch = 0;
				switch(line.charAt(j))
				{
					case '|':
						continue;
					case ' ':
					case '-':
						curKey++;
						continue;
					case 'f':
						pitch++;
					case 'e':
						pitch++;
					case 'D':
						pitch++;
					case 'd':
						pitch++;
					case 'C':
						pitch++;
					case 'c':
						pitch++;
					case 'b':
						pitch++;
					case 'A':
						pitch++;
					case 'a':
						pitch++;
					case 'G':
						pitch++;
					case 'g':
						pitch++;
					case 'F':
						curKey++;
						break;
					default:
						continue;
				}
				
				if(keys[curKey] == null)
				{
					keys[curKey] = new MusicNoteKey(curKey);
				}
				
				pitch += (octave - 2) * 12;
				
				if(pitch < 0)
				{
					//not sure what to do when the octave is too low
					//for now I will guess setting it as F#2 is ok
					//I know nothing about music! XD
					pitch = 0;
				}
				else if(pitch > 24)
				{
					//same as above....
					pitch = 24;
				}
				
				keys[curKey].addNote(type, (byte) pitch );
				
				if(curKey > maxKey)
					maxKey = curKey;
			}
		}
		
		ArrayList<MusicNoteKey> musicKeys = new ArrayList<MusicNoteKey>();
		
		for(int i = 0; i < maxKey; i++)
		{
			if(keys[i] == null)
				continue;
			
			musicKeys.add(keys[i]);
		}
		
		if(musicKeys.size() == 0)
			return null;
		
		return musicKeys;
	}
	
	public static ArrayList<MusicNoteKey> parseTune1(String tune)
	{
		if(tune == null)
			return null;
		
		ArrayList<MusicNoteKey> musicKeys = new ArrayList<MusicNoteKey>();
		
		int instrument = -1;
		int position = 0;
		for(int i = 0; i < tune.length(); i++)
		{
			char first = tune.charAt(i);
			if(first >= '0' && first <= '9')
			{
				//instrument?
				instrument = getTypeFromChar(first);
			}
			else if(i+1 < tune.length())
			{
				//note?
				if(instrument == -1)
					return null;
				
				int pitch = getPitchFromChar(first);
				boolean skip = false;
				if(pitch == -1)
				{
					switch(first)
					{
						case '-':
						case ' ':
							skip = true;
							break;
						default:
							return null;
					}
				}
				
				int octave;
				try
				{
					octave = Integer.parseInt(Character.toString(tune.charAt(i+1)));
				}
				catch(NumberFormatException e)
				{
					octave = 2;
				}
				
				if(skip)
				{
					if(octave == 0)
						octave = 10;
					
					position += octave;
				}
				else
				{
					MusicNoteKey key = new MusicNoteKey(position);
					position++;
					
					if(octave < 2)
						octave = 2;
					
					pitch += (octave - 2) * 12;
					
					if(pitch < 0)
						pitch = 0;
					else if(pitch > 24)
						pitch = 24;
					
					key.addNote(instrument, (byte)pitch);
					musicKeys.add(key);
				}
				
				i++;
			}
		}
		
		if(musicKeys.size() == 0)
			return null;
		
		return musicKeys;
	}
	
	public static int getType(String stype)
	{
		int type = -1;
		
		//names must be 3 or more chars
		if(stype.equalsIgnoreCase("harp"))
		{
			type = 0;
		}
		else if(stype.equalsIgnoreCase("bdrum"))
		{
			type = 1;
		}
		else if(stype.equalsIgnoreCase("snare"))
		{
			type = 2;
		}
		else if(stype.equalsIgnoreCase("hat"))
		{
			type = 3;
		}
		else if(stype.equalsIgnoreCase("batk"))
		{
			type = 4;
		}
		
		return type;
	}
	
	public static int getPitchType(String stype)
	{
		int type = -1;
		
		if(stype.equalsIgnoreCase("f#"))
		{
			type = 0;
		}
		else if(stype.equalsIgnoreCase("g."))
		{
			type = 1;
		}
		else if(stype.equalsIgnoreCase("g#"))
		{
			type = 2;
		}
		else if(stype.equalsIgnoreCase("a."))
		{
			type = 3;
		}
		else if(stype.equalsIgnoreCase("a#"))
		{
			type = 4;
		}
		else if(stype.equalsIgnoreCase("b."))
		{
			type = 5;
		}
		else if(stype.equalsIgnoreCase("c."))
		{
			type = 6;
		}
		else if(stype.equalsIgnoreCase("c#"))
		{
			type = 7;
		}
		else if(stype.equalsIgnoreCase("d."))
		{
			type = 8;
		}
		else if(stype.equalsIgnoreCase("d#"))
		{
			type = 9;
		}
		else if(stype.equalsIgnoreCase("e."))
		{
			type = 10;
		}
		else if(stype.equalsIgnoreCase("f."))
		{
			type = 11;
		}
		
		return type;
	}
	
	public static int getTypeFromChar(char type)
	{
		int instrument = -1;
		switch(type)
		{
			case '9':
			case '8':
			case '7':
			case '6':
			case '5':
			case '0':
				instrument = 0;
				break;
			case '1':
				instrument = 1;
				break;
			case '2':
				instrument = 2;
				break;
			case '3':
				instrument = 3;
				break;
			case '4':
				instrument = 4;
				break;
		}
		
		return instrument;
	}
	
	public static int getPitchFromChar(char charPitch)
	{
		int pitch = 0;
		switch(charPitch)
		{
			case 'f':
				pitch++;
			case 'e':
				pitch++;
			case 'D':
				pitch++;
			case 'd':
				pitch++;
			case 'C':
				pitch++;
			case 'c':
				pitch++;
			case 'b':
				pitch++;
			case 'A':
				pitch++;
			case 'a':
				pitch++;
			case 'G':
				pitch++;
			case 'g':
				pitch++;
			case 'F':
				break;
			default:
				pitch = -1;
				break;
		}
		
		return pitch;
	}
}
