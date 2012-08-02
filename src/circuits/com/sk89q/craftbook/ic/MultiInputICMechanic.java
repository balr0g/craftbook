package com.sk89q.craftbook.ic;

import java.util.regex.Matcher;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.sk89q.craftbook.SourcedBlockRedstoneEvent;
import com.sk89q.craftbook.bukkit.BukkitUtil;
import com.sk89q.craftbook.bukkit.CircuitsPlugin;
import com.sk89q.craftbook.util.BlockWorldVector;
import com.sk89q.worldedit.blocks.BlockID;

public class MultiInputICMechanic extends ICMechanic {
	public MultiInputICMechanic(CircuitsPlugin plugin, String id,
			MultiInputIC ic, ICFamily family, BlockWorldVector... pos) {
		super(plugin, id, ic, family, pos);
	}

	@Override
	public void onBlockRedstoneChange(final SourcedBlockRedstoneEvent event) {
		Block block;
  
		BlockWorldVector pos = getTriggerPositions().get(0);// the reader looks
															// for the first
															// input as the head
															// sign

		  block = pos.getWorld().getBlockAt(BukkitUtil.toLocation(pos));

		if (block.getTypeId() == BlockID.WALL_SIGN) {
			final BlockState state = block.getState();

			ChipState chipState = family.detect(
					BukkitUtil.toWorldVector(event.getSource()), (Sign) state);

			ms.push(ic, chipState, (Sign) state);
		}

	}

	@Override
	public void unload() {
		ic.unload();
		// FIXME: cancel any scheduled updates. (do them nao?)
	}

	@Override
	public boolean isActive() {
		BlockWorldVector pt = getTriggerPositions().get(0);
		Block block = pt.getWorld().getBlockAt(BukkitUtil.toLocation(pt));
		for (BlockWorldVector pos : getTriggerPositions()) {
			if (block.getTypeId() == BlockID.WALL_SIGN) {
				BlockState state = block.getState();

				if (state instanceof Sign) {
					Sign sign = (Sign) state;

					Matcher matcher = ICMechanicFactory.codePattern
							.matcher(sign.getLine(1));

					if (!matcher.matches()) {
						return false;
					} else if (!matcher.group(1).equalsIgnoreCase(id)) {
						return false;
					} else if (ic instanceof PersistentIC) {
						return ((PersistentIC) ic).isActive();
					} else {
						return false;
					}
				}
			}
		}
		return false;
	}

}
