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

package com.sk89q.craftbook.gates.world;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import com.sk89q.craftbook.ic.AbstractIC;
import com.sk89q.craftbook.ic.AbstractICFactory;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;
import com.sk89q.craftbook.util.SignUtil;

public class BlockSensor extends AbstractIC {

    protected boolean risingEdge;

    public BlockSensor(Server server, Sign sign, boolean risingEdge) {
        super(server, sign);
        this.risingEdge = risingEdge;
    }

    @Override
    public String getTitle() {
        return "Detect Block";
    }

    @Override
    public String getSignTitle() {
        return "DETECT BLOCK";
    }

    @Override
    public void trigger(ChipState chip) {
    	int id = -1;
    	short data=-1;
    	if (risingEdge && chip.getInput(0) || (!risingEdge && !chip.getInput(0))) {
        	try {
        		String[] st = getSign().getLine(2).split(":");
        		switch(st.length) {
        		case 2: data = (short)Integer.parseInt(st[1]);
        		case 1: id = Integer.parseInt(st[0]); break;
        		case 0: return;
        		}
        	} catch(Exception e) {}
    	}
    	if(id == -1) return;
    	chip.setOutput(0, hasBlock(id, data));
    }

    /**
     * Returns true if the sign has the specified block at the specified location, ignoring damage value.
     * 
     * @return
     */
    protected boolean hasBlock(int id) {
    	return hasBlock(id, (short)-1);
    }
    

    protected boolean hasBlock(int id, short data) {

        Block b = SignUtil.getBackBlock(getSign().getBlock());

        int x = b.getX();
        int yOffset = b.getY();
        int z = b.getZ();
        try {
            String yOffsetLine = getSign().getLine(2);
            if (yOffsetLine.length() > 0) {
                yOffset += Integer.parseInt(yOffsetLine);
            } else {
                yOffset -= 1;
            }
        } catch (NumberFormatException e) {
            yOffset -= 1;
        }
        Block block = getSign().getWorld().getBlockAt(x, yOffset, z);
        return (block.getTypeId() == id && (data == -1 || data == block.getData()));

    }

    public static class Factory extends AbstractICFactory {

        protected boolean risingEdge;

        public Factory(Server server, boolean risingEdge) {
            super(server);
            this.risingEdge = risingEdge;
        }

        @Override
        public IC create(Sign sign) {
            return new BlockSensor(getServer(), sign, risingEdge);
        }
    }

}
