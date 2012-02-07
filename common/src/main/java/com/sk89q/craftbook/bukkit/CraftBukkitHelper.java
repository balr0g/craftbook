package com.sk89q.craftbook.bukkit;

import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bukkit.BukkitUtil;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;;

public class CraftBukkitHelper {
		public static void playNote(WorldVector pos, byte instrument, byte note) {
			Location loc = BukkitUtil.toLocation(pos);
			CraftWorld w = (CraftWorld)(loc.getWorld());
			w.getHandle().playNote(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), instrument, note);
		}
}
