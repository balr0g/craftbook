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

package com.sk89q.craftbook.bukkit;

import com.sk89q.craftbook.InsufficientPermissionsException;
import com.sk89q.craftbook.LocalPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BukkitPlayer implements LocalPlayer {

    protected final BaseBukkitPlugin plugin;
    protected final Player player;

    public Player getPlayer() {

        return player;
    }

    public BukkitPlayer(BaseBukkitPlugin plugin, Player player) {

        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void print(String message) {

        player.sendMessage(ChatColor.GOLD + plugin.getLanguageManager().getString(message));
    }

    @Override
    public void printError(String message) {

        player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString(message));
    }

    @Override
    public void printRaw(String message) {

        player.sendMessage(plugin.getLanguageManager().getString(message));
    }

    @Override
    public boolean hasPermission(String perm) {

        return plugin.hasPermission(player, perm);
    }

    @Override
    public void checkPermission(String perm) throws InsufficientPermissionsException {

        if (!hasPermission(perm)) throw new InsufficientPermissionsException();
    }

    @Override
    public String getName() {

        return player.getName();
    }

}
