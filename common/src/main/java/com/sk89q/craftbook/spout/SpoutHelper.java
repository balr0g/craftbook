package com.sk89q.craftbook.spout;


import org.bukkit.entity.Player;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.sk89q.craftbook.bukkit.BaseBukkitPlugin;


public class SpoutHelper {
	private BaseBukkitPlugin plugin;
	
	public SpoutHelper(BaseBukkitPlugin plugin) {
		this.plugin = plugin;
	}
	
	public boolean hasSpoutClient(Player p) {
		if(plugin.usingSpout()) {
			return ((SpoutPlayer)(p)).isSpoutCraftEnabled(); 
		} else return false;
	}
	
	public void setPlayerWeather(Player p, String weather) {
		if(plugin.usingSpout()) {
			if (weather.equalsIgnoreCase("rain"))
				plugin.getBiomeManager().setPlayerWeather((SpoutPlayer)p, SpoutWeather.RAIN);
			else if(weather.equalsIgnoreCase("snow"))
				plugin.getBiomeManager().setPlayerWeather((SpoutPlayer)p, SpoutWeather.SNOW);
			else if(weather.equalsIgnoreCase("clear"))
				plugin.getBiomeManager().setPlayerWeather((SpoutPlayer)p, SpoutWeather.NONE);
			else
				plugin.getBiomeManager().setPlayerWeather((SpoutPlayer)p, SpoutWeather.RESET);
		}
	}
	
}
