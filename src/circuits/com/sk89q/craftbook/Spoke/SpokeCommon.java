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

package com.sk89q.craftbook.Spoke;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.Spoke.ClassInfo.ClassWithLines;
import com.sk89q.craftbook.Spoke.SpokeExpressions.TokenEnumerator;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;
import com.sk89q.craftbook.Spoke.Tokens.LineToken;

/**
 * Plugin for the Spoke Language.
 * 
 * @author Dested
 */
public class SpokeCommon {
	public static Integer IntegerTryParse(String s) {
		try {
			return Integer.parseInt(s);

		} catch (NumberFormatException nfe) {
			return Integer.MIN_VALUE;
		}
	}

	public static void Assert(boolean b, String bad, TokenEnumerator enumerator)
			throws SpokeException {

		if (!b) {
			throw new SpokeException(enumerator.getCurrent());

		}

	}

	public static <T> T[] CombineArray(T dm, Collection<T> params) {
		T[] newArray = (T[]) Array
				.newInstance(dm.getClass(), params.size() + 1);

		newArray[0] = dm;

		int i = 0;
		for (Iterator<T> iterator = params.iterator(); iterator.hasNext();) {
			T t = (T) iterator.next();
			newArray[i + 1] = t;
			i++;
		}

		return newArray;
	}

	public static void dropSign(World world, int x, int y, int z) {
		world.getBlockAt(x, y, z).setTypeIdAndData(0, (byte) 0, false);
		world.dropItem(new Location(world, x, y, z), new ItemStack(323, 1));
	}
}