// $Id$

package com.sk89q.craftbook.ic;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.util.BlockWorldVector;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public interface MultiInputIC extends IC,PersistentIC
{
    /**
     * Return true if this IC still is valid.
     * 
     * @return
     */
    public abstract boolean isActive();
    /**
     * Return true if this IC still is valid.
     * 
     * @return
     */
	public abstract BlockWorldVector[] getTriggerPositions(BlockWorldVector position,LocalPlayer player, Sign sign);
}
