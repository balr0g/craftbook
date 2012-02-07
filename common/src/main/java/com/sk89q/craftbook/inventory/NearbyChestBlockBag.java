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

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.inventory.ItemStack;


import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.bags.BlockBagException;
import com.sk89q.worldedit.bags.OutOfBlocksException;
import com.sk89q.worldedit.bags.OutOfSpaceException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.blocks.ChestBlock;

/**
 *
 * @author sk89q
 */
public class NearbyChestBlockBag extends BlockBag {
    /**
     * List of chests.
     */
    private Set<ComparableInventory> chests;

    /**
     * Construct the object.
     * 
     * @param origin
     */
    public NearbyChestBlockBag(WorldVector origin) {
        DistanceComparator<ComparableInventory> comparator =
                new DistanceComparator<ComparableInventory>(origin);
        chests = new TreeSet<ComparableInventory>(comparator);
    }

    /**
     * Gets a block.
     *
     * @param pos
     * @param id
     * @return
     * @throws OutOfBlocksException
     */
    public void fetchBlock(BlockType type) throws BlockBagException {
    	fetchBlock(type, (short)-1);
    }
    public void fetchBlock(BlockType type, short data) throws BlockBagException {
        try {
            for (ComparableInventory c : chests) {
                ChestBlock chest = c.getInventory();
                BaseItemStack[] items = chest.getItems();
                
                // Find the item
                for (int i = 0; items.length > i; i++) {
                    if (items[i] != null) {
                        // Found an item
                        if (items[i].getType() == type.getID() &&
                        	(data == -1 || items[i].getDamage() == data) &&
                            items[i].getAmount() >= 1) {
                            int newAmount = items[i].getAmount() - 1;
    
                            if (newAmount > 0) {
                                items[i].setAmount(newAmount);
                            } else {
                                items[i] = null;
                            }
                            
                            chest.setItems(items);
                         
                            return;
                        }
                    }
                }
            }
    
            throw new OutOfBlocksException();
        } finally {
            flushChanges();
        }
    }

    /**
     * Stores a block.
     *
     * @param pos
     * @param id
     * @return
     * @throws OutOfSpaceException
     */
    public void storeBlock(BlockType type) throws BlockSourceException, OutOfSpaceException {
    	storeBlock(type, (short)-1, 1);
    }
    public void storeBlock(BlockType type, short data) throws BlockSourceException, OutOfSpaceException {
    	storeBlock(type, data, 1);
    }
    public void storeBlock(BlockType type, short data, int amount) throws BlockSourceException, OutOfSpaceException {
        try {
            for (ComparableInventory c : chests) {
                ChestBlock chest = c.getInventory();
                BaseItemStack[] items = chest.getItems();
                int emptySlot = -1;
    
                // Find an existing slot to put it into
                for (int i = 0; items.length > i; i++) {
                    if (items[i] != null) {
                        // Found an item
                    	int itemMax = (new ItemStack(items[i].getType()).getMaxStackSize());
                        if (items[i].getType() == type.getID() &&
                        	(data == -1 || items[i].getDamage() == data) &&
                            items[i].getAmount() < itemMax) {
                        	
                        	int newAmount;
                        	if(items[i].getAmount() + amount > itemMax)
                        	{
                        		newAmount = itemMax;
                        		amount = items[i].getAmount() + amount - itemMax;
                        	}
                        	else
                        	{
                        		newAmount = items[i].getAmount() + amount;
                        		amount = 0;
                        	}
                            items[i].setAmount(newAmount);
                            
                            chest.setItems(items);
                            
                            if(amount <= 0)
                            	return;
                            continue;
                        }
                    } else {
                        emptySlot = i;
                    }
                }
    
                // Didn't find an existing stack, so let's create a new one
                if (emptySlot != -1) {
                    items[emptySlot] = new BaseItemStack(type.getID(), amount);
                    if(data != -1)
                    	items[emptySlot].setDamage(data);
                    
                    chest.setItems(items);
                    return;
                }
            }
    
            throw new OutOfSpaceException(type.getID());
        } finally {
            flushChanges(); 
        }
    }
    
    /**
     * Checks if the item can be placed some where. Either an empty slot or existing slot.
     *
     * @param pos
     * @param id
     * @return
     * @throws OutOfSpaceException
     */
    public boolean hasAvailableSlotSpace(BlockType type, short data, int amount) {
        for (ComparableInventory c : chests) {
            ChestBlock chest = c.getInventory();
            BaseItemStack[] items = chest.getItems();
            int emptySlot = -1;

            // Find an existing slot item can be put it into
            for (int i = 0; items.length > i; i++) {
                if (items[i] != null) {
                    // Found an item
                	int itemMax = (new ItemStack(items[i].getType()).getMaxStackSize());
                    if (items[i].getType() == type.getID() &&
                    	(data == -1 || items[i].getDamage() == data) &&
                        items[i].getAmount() < itemMax) {
                    	
                    	//checks if the full stack can fit
                    	if(items[i].getAmount() + amount > itemMax)
                    	{
                    		amount = items[i].getAmount() + amount - itemMax;
                    		continue;
                    	}

                        return true;
                    }
                } else {
                    emptySlot = i;
                }
            }

            // Didn't find an existing stack, so return if has empty slot
            if (emptySlot != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stores a block.
     *
     * @param pos
     * @param id
     * @return
     * @throws OutOfSpaceException
     */
    public void storeBlock(int id, int amount) throws BlockSourceException {
        
    }

    /**
     * Adds a position to be used a source.
     *
     * @param pos
     * @return
     */
    public void addSourcePosition(WorldVector pos) {
        //int ox = pos.getBlockX();
        //int oy = pos.getBlockY();
        //int oz = pos.getBlockZ();

        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    WorldVector cur = new WorldVector(pos.getWorld(), pos.add(x, y, z));
                    addSingleSourcePosition(cur);
                }
            }
        }
    }

    /**
     * Adds a position to be used a source.
     *
     * @param pos
     * @return
     */
    public void addSingleSourcePosition(WorldVector pos) {
    	LocalWorld w = pos.getWorld();
    	BaseBlock block = new BaseBlock(w.getBlockType(pos));
    	w.copyFromWorld(pos, block);
    	if (block.getType() == BlockType.CHEST.getID()) {
            if (block instanceof ChestBlock) {
            	ChestBlock chestBlock = (ChestBlock)block;
                chests.add(new ComparableInventory(new BlockWorldVector(pos), chestBlock));
                // Double chests have two chest blocks, so creating a new Vector
                // should theoretically prevent duplication (but it doesn't
                // (yet...)
            }
        }
    }
    
    public void addSingleSourcePositionExtra(WorldVector pos) {
    	LocalWorld w = pos.getWorld();
    	BaseBlock block = new BaseBlock(w.getBlockType(pos));
    	w.copyFromWorld(pos, block);

    	if (block.getType() == BlockType.CHEST.getID()) {
            if (block instanceof ChestBlock) {
            	ChestBlock chestBlock = (ChestBlock)block;

                chests.add(new ComparableInventory(new BlockWorldVector(pos), chestBlock));
                // Double chests have two chest blocks, so creating a new Vector
                // should theoretically prevent duplication (but it doesn't
                // (yet...)
            }
        }
    }
    
    /**
     * Get the number of chest blocks. A double-width chest will count has
     * two chest blocks.
     * 
     * @return
     */
    public int getChestBlockCount() {
        return chests.size();
    }
    
    /**
     * Fetch related chest inventories.
     * 
     * @return
     */
    public ChestBlock[] getInventories() {
    	ChestBlock[] inventories = new ChestBlock[chests.size()];
        
        int i = 0;
        for (ComparableInventory c : chests) {
            inventories[i] = c.getInventory();
            i++;
        }
        
        return inventories;
    }

    /**
     * Flush changes.
     */
    public void flushChanges()
    {}
    
    public boolean hasRealFetch()
    {
    	return true;
    }
    
    public boolean hasRealStore()
    {
    	return true;
    }

    //FIXME: THESE DON'T WORK!
	@Override
	public void addSingleSourcePosition(Vector arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSourcePosition(Vector arg0) {
		// TODO Auto-generated method stub
		
	}
    
/*
	@Override
	public void addSingleSourcePosition(Vector arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSourcePosition(Vector arg0) {
		// TODO Auto-generated method stub
		
	} */
}
