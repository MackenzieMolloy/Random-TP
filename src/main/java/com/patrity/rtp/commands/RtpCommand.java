package com.patrity.rtp.commands;

import com.patrity.rtp.Rtp;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Random;

public class RtpCommand implements CommandExecutor {

    private final Rtp RandomTeleport;

    public RtpCommand(final Rtp RandomTeleport) {this.RandomTeleport = RandomTeleport;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            getLoc(player);
        }
        return true;
    }

    private void getLoc(Player player) {

        // Check if player does not have permission to use the command
        if (!player.hasPermission("rtp.teleport")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RandomTeleport.configFile.getString("messages.no-permission")));
            return;
        }

        // Checks if the player is in a world that supports random telelportation
        else if(RandomTeleport.configFile.getConfigurationSection("boundaries." + player.getWorld().getName()) == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RandomTeleport.configFile.getString("messages.world-not-allowed")));
            return;
        }

        // Gets the configuration section containing the random teleport information
        ConfigurationSection bounds = RandomTeleport.configFile.getConfigurationSection("boundaries." + player.getWorld().getName());
        assert bounds != null;

        int maxX = bounds.getInt("max.x");
        int minX = bounds.getInt("min.x");
        int maxZ = bounds.getInt("max.z");
        int minZ = bounds.getInt("min.z");

        World worldTp = Bukkit.getServer().getWorld(bounds.getString("world"));

        if(worldTp == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe world you were meant to be teleported to is invalid. Contact an admin. ERR: INVALID_CONFIG"));
        }

        int x = random(minX, maxX);
        int z = random(minZ, maxZ);
        int y = player.getWorld().getHighestBlockYAt(x, z);

        Location location = new Location(worldTp, x, y, z);

        int loopRepeats = 0;
        if(location.getBlock().isLiquid()) {
            do {

                if (loopRepeats >= RandomTeleport.configFile.getInt("options.re-check-locations")) {

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RandomTeleport.configFile.getString("messages.location-not-found")));
                    return;

                }

                loopRepeats++;

                x = random(minX, maxX);
                z = random(minZ, maxZ);
                y = player.getWorld().getHighestBlockYAt(x, z);
                location = new Location(worldTp, x, y, z);


            } while (location.getBlock().isLiquid());
        }

        player.teleport(new Location(worldTp, x+0.5, y + 2, z+0.5));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RandomTeleport.configFile.getString("messages.teleported")));

    }

    private int random(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
