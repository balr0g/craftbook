// $Id$
/*
 * CraftBook
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
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

package com.sk89q.craftbook.inventory;


import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.blocks.ChestBlock;

/**
 * Sorts inventories based on position.
 *
 * @author sk89q
 */
public class ComparableInventory implements PointBasedEntity {
    /**
     * Chest location.
     */
    private BlockWorldVector pos;
    

    /**
     * Chest.
     */
    private ChestBlock inventory;

    /**
     * Construct the object.
     * 
     * @param pos
     * @param block
     */
    public ComparableInventory(BlockWorldVector pos, ChestBlock inventory) {
        this.pos = pos;
        this.inventory = inventory;
    }

    /**
     * @return
     */
    public BlockWorldVector getPosition() {
        return pos;
    }
    
    /**
     * @return
     */
    public ChestBlock getInventory() {
        return inventory;
    }

    /**
     * Equals check.
     * 
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof ComparableInventory) {
            return ((ComparableInventory)other).pos.equals(pos);
        } else {
            return false;
        }
    }

    /**
     * Get the hash code.
     * 
     * @return
     */
    @Override
    public int hashCode() {
        return pos.hashCode();
    }
}
