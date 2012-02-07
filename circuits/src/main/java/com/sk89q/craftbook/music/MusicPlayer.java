package com.sk89q.craftbook.music;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.sk89q.craftbook.util.HistoryHashMap;
import com.sk89q.craftbook.MusicConfiguration;
import com.sk89q.craftbook.bukkit.BaseBukkitPlugin;
import com.sk89q.craftbook.bukkit.CraftBukkitHelper;
import com.sk89q.craftbook.music.IMusicPlayer;
import com.sk89q.craftbook.music.MusicNote;
import com.sk89q.craftbook.music.Playlist;
import com.sk89q.craftbook.music.RadioObject;
import com.sk89q.craftbook.music.media.ExternalMedia;
import com.sk89q.craftbook.music.media.Media;
import com.sk89q.craftbook.music.media.MidiMedia;
import com.sk89q.craftbook.music.media.SimpleTuneMedia;
import com.sk89q.craftbook.music.media.TextSongMedia;
import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bukkit.BukkitUtil;



public class MusicPlayer implements IMusicPlayer
{
	private BaseBukkitPlugin plugin;
	
	private final boolean isLoop;
	
	private MusicConfiguration cfg;
	
	private final WorldVector loc;
	
	private int currentTick = 0;
	private int skipProtection = 0;
	
	public boolean pause = false;
	
	private Media playingMedia;
	private Playlist playlist;
	
	private Map<String,RadioObject> radios;
	
	public MusicPlayer(String data, WorldVector pos, MusicConfiguration cfg, byte type, boolean loop)
	{
		this(data, pos, cfg, type, loop, false);
	}
	
	public MusicPlayer(String data, WorldVector pos, MusicConfiguration cfg, byte type, boolean loop, boolean isStation)
	{
		this.isLoop = loop;
		
		this.loc = pos;
		
		if(isStation)
			radios = new HistoryHashMap<String,RadioObject>(100);
		else
			radios = null;				
		
		if(type == 0)
		{
			//external media
			playingMedia = parseExternalData(data, true);
			if(playingMedia == null && playlist != null)
			{
				playingMedia = playlist.getCurrentMedia();
			}
		}
		else
		{
			playingMedia = new SimpleTuneMedia(this, data);
		}
	}
	
	public ExternalMedia parseExternalData(String data)
	{
		return parseExternalData(data, false);
	}
	
	private ExternalMedia parseExternalData(String data, boolean includePlaylist)
	{
		String[] args = data.split(":", 2);
		String[] song = args[0].split("\\.", 2);
		
		ExternalMedia media = null;
		
		if(song.length > 1)
		{
			if(song[1].equalsIgnoreCase("m"))
				media = new MidiMedia(this, song[0]);
			else if(includePlaylist && song[1].equalsIgnoreCase("p"))
			{
				playlist = new Playlist(this, song[0]);
				if(playlist.getSize() == 0)
				{
					playlist = null;
				}
				media = null;
			}
		}
		else
		{
			media = new TextSongMedia(this, song[0]);
		}
		
		if(media == null)
			return null;
		
		int rate = -1;
		if(args.length > 1)
		{
			rate = getLimitedRate(args[1]);
		}
		
		media.setRate(rate);
		
		currentTick = media.getRate();
		
		return media;
	}
	
	public void tick()
	{
		if(!isPlaying())
			return;
		
		if(currentTick >= playingMedia.getRate())
		{
			currentTick = 0;
			if(!playingMedia.playNextNote())
			{
				if(playlist != null)
				{
					playNext();
				}
				else if(isLoop)
				{
					playingMedia.reset();
					sendInfoToRadios();
				}
				else
				{
					stop();
				}
				return;
			}
		}
		else
		{
			currentTick++;
		}
	}
	
	public void playNotes(ArrayList<MusicNote> notes)
	{
		for(MusicNote note : notes)
		{
			CraftBukkitHelper.playNote(loc, (byte)note.type, note.pitch);
//			etc.getMCServer().f.a(x, y, z, 64.0D, worldType, new OPacket54PlayNoteBlock(x, y, z, note.type, note.pitch));
			
			if(radios != null)
			{
				for(RadioObject radio : radios.values())
				{
					CraftBukkitHelper.playNote(radio.pos, (byte)note.type, note.pitch);
					/*
					etc.getMCServer().f.a(radio.X, radio.Y, radio.Z, 64.0D, worldType,
							new OPacket54PlayNoteBlock(radio.X, radio.Y, radio.Z, note.type, note.pitch));
				*/
				}
			}
		}
	}
	
	public void sendMessage(String message)
	{
		sendMessageTo(message, loc);
	}
	
	private void sendMessageTo(String message, WorldVector v)
	{
		World world = BukkitUtil.toWorld(v);
		for(Player player: plugin.getServer().getOnlinePlayers())
		{
			if(!(player.getWorld().equals(world)))
				continue;
			
			Location pLoc = player.getLocation();
			double diffX = v.getX() - pLoc.getX();
			double diffY = v.getY() - pLoc.getY();
			double diffZ = v.getZ() - pLoc.getZ();
			
			if(diffX * diffX + diffY * diffY + diffZ * diffZ < 4096.0D)
			{
				String[] lines = message.split("<br>", 5);
				for(String line : lines)
				{
					player.sendMessage(line);
				}
			}
		}
	}
	
	private void sendInfoToRadios()
	{
		if(radios == null || playingMedia == null || !(playingMedia instanceof ExternalMedia))
			return;
		
		ExternalMedia media = (ExternalMedia) playingMedia;
		String message = media.getInfoMessage();
		
		for(RadioObject radio : radios.values())
		{
			if(radio.sendMessages || media.isForcedMessage())
				sendMessageTo(message, radio.pos);
		}
	}
	
	public void playNext()
	{
		if(playlist == null)
			return;
		
		if(skipProtection > cfg.maxMissing)
		{
			turnOff();
			return;
		}
		
		if(playingMedia != null)
			playingMedia.reset();
		
		playingMedia = playlist.getNext();
		if(playingMedia == null)
		{
			if(isLoop)
				playingMedia = playlist.jumpTo(0);
			
			if(playingMedia == null)
			{
				turnOff();
				return;
			}
		}
		
		if(playingMedia instanceof ExternalMedia)
		{
			if( !((ExternalMedia)playingMedia).loadSong())
			{
				skipProtection++;
				playNext();
				return;
			}
			skipProtection = 0;
			sendInfoToRadios();
		}
	}
	
	public void playPrevious()
	{
		if(playlist == null)
			return;
		
		if(skipProtection > cfg.maxMissing)
		{
			turnOff();
			return;
		}
		
		if(playingMedia != null)
			playingMedia.reset();
		
		playingMedia = playlist.getPrevious();
		if(playingMedia == null)
		{
			if(isLoop)
				playingMedia = playlist.jumpTo(playlist.getSize()-1);
			
			if(playingMedia == null)
			{
				turnOff();
				return;
			}
		}
		
		if(playingMedia instanceof ExternalMedia)
		{
			if( !((ExternalMedia)playingMedia).loadSong())
			{
				skipProtection++;
				playPrevious();
				return;
			}
			skipProtection = 0;
			sendInfoToRadios();
		}
	}
	
	//this is really "pause"
	public void stop()
	{
		pause = true;
	}
	
	public void turnOff()
	{
		stop();
		
		if(radios != null)
		{
			World world = BukkitUtil.toWorld(loc);
			for(RadioObject radio : radios.values())
			{
				Block block = world.getBlockAt(BukkitUtil.toLocation(radio.signPos));
		    	if(!(block.getState() instanceof Sign))
		    		return;
	//FIXME! how best to handle radio ICs	    	
//		    	Sign sign = (Sign) block.getState();
//		    	String title = MCX702.getOffState(sign.getText(0));
//		    	sign.setText(0, title);
			}
			radios.clear();
		}
		
		playingMedia = null;
		playlist = null;
	}
	
	public void loadSong()
	{
		if(playingMedia == null || !(playingMedia instanceof ExternalMedia))
			return;
		
		ExternalMedia media = (ExternalMedia) playingMedia;
		if(!media.loadSong())
		{
			byte error;
			if(media instanceof MidiMedia && (error = ((MidiMedia) media).getErrorType()) != 0)
			{
				if(error == 1)
					sendMessage(ChatColor.RED+"> Unsupported MIDI");
				else if(error == 2)
					sendMessage(ChatColor.RED+"> Error reading MIDI");
			}
			return;
		}
		
		//sendInfoToRadios();
	}
	
	public boolean isPlaying()
	{
		return !pause && playingMedia != null && !playingMedia.isFinished();
	}
	
	public void addRadio(String key, RadioObject radio)
	{
		if(radios == null || radio == null)
			return;
		
		radios.put(key, radio);
		
		if(isPlaying() && playingMedia instanceof ExternalMedia)
		{
			ExternalMedia media = (ExternalMedia) playingMedia;
			
			if(radio.sendMessages || media.isForcedMessage())
				sendMessageTo(media.getInfoMessage(), radio.pos);
		}
	}
	
	public RadioObject getRadio(String key)
	{
		if(radios == null)
			return null;
		
		return radios.get(key);
	}
	
	public void removeRadio(String key)
	{
		if(radios == null)
			return;
		
		radios.remove(key);
	}
	
	public int getMaxRate()
	{
		return cfg.maxRate;
	}
	
	public int getLimitedRate(String sRate)
	{
		int rate = -1;
		try
		{
			rate = Integer.parseInt(sRate);
			if(rate > cfg.maxRate)
				rate = cfg.maxRate;
			else if(rate < 1)
				rate = -1;
		}
		catch(NumberFormatException e)
		{
			rate = -1;
		}
		
		return rate;
	}

	public boolean loops()
	{
		return isLoop;
	}
	
	public MusicConfiguration getConfig() {
		return this.cfg;
	}
}
