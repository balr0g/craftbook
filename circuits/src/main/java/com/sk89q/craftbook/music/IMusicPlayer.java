package com.sk89q.craftbook.music;

import java.util.ArrayList;

import com.sk89q.craftbook.MusicConfiguration;
import com.sk89q.craftbook.music.media.ExternalMedia;

public interface IMusicPlayer
{
	void tick();
	void playNotes(ArrayList<MusicNote> notes);
	void sendMessage(String message);
	
	ExternalMedia parseExternalData(String data);
	
	int getLimitedRate(String sRate);
	boolean loops();
	
	MusicConfiguration getConfig();

}