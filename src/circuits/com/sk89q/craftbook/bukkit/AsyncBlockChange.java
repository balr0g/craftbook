package com.sk89q.craftbook.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class AsyncBlockChange {
	public Location Location;
	public Integer TypeID;
	public Byte Data1;
	public Byte Data2;
	public String[] Lines;
	public Material Material;
	public BlockFace Face;

	public AsyncBlockChange(Location loc) {
		Location = loc; 
	}
	public AsyncBlockChange(Location loc, Integer typeid) {
		this(loc);
		TypeID = typeid;
	}

	public AsyncBlockChange(Location loc, Integer typeid, Byte d) {
		this(loc, typeid);
		Data1 = d;
	}

	public AsyncBlockChange(Location loc, Integer typeid, Byte data,
			Byte direction, String[] lines) {
		this(loc, typeid, direction);

		Data2 = data;
		Lines = lines;
	}

	public AsyncBlockChange(Location location2, Material mat,
			BlockFace f) {
		this(location2);
		Material=mat;
		Face=f;
		// TODO Auto-generated constructor stub
	}
}