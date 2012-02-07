// $Id$
/*
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.craftbook;

import java.io.*;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Configuration handler for CraftBook.
 * 
 * @author sk89q
 */
public class MusicConfiguration {
    public MusicConfiguration(FileConfiguration cfg, File dataFolder) {
        this.dataFolder = dataFolder;
        
        maxBeatDuration = cfg.getInt("music-max-beat-duration", 500);
        maxLines    = cfg.getInt("music-max-text-lines", 100);
        maxPlaylistTracks         = cfg.getInt("music-max-playlist-tracks", 100);
        maxMidiTracks   = cfg.getInt("music-max-midi-tracks", 10);
        nowPlaying = cfg.getString("music-text-now-playing", "");
    }
    
    public final File dataFolder;
    
   // public 
    
	public final int maxBeatDuration;
	public final int maxLines;
	public final int maxPlaylistTracks;
	public final int maxMidiTracks;
	public final int maxRate = 10;
	public final int maxMissing = 10;
	public final String nowPlaying;
}
