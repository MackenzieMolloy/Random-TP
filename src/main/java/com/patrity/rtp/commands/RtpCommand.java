package com.patrity.rtp.commands;

import com.patrity.rtp.Rtp;
import jdk.nashorn.internal.ir.BlockStatement;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.*;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RtpCommand implements CommandExecutor {

    private final Rtp RandomTeleport;

    public RtpCommand(final Rtp RandomTeleport) {this.RandomTeleport = RandomTeleport;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            getLoc(player, args);
            //else if(args[0].equalsIgnoreCase("-d") && player.hasPermission("rtp.admin")) getLoc(player, true);
        }
        return true;
    }

    private void getLoc(Player player, String args[]) {

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
        int y = worldTp.getHighestBlockYAt(x, z);

        Location location = new Location(worldTp, x, y, z);

        int loopRepeats = 0;
        if(location.getBlock().isLiquid() || location.getBlock().getBlockData() instanceof Waterlogged || location.getBlock().isPassable()) {
            do {

                if (loopRepeats >= RandomTeleport.configFile.getInt("options.re-check-locations")) {

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RandomTeleport.configFile.getString("messages.location-not-found")));
                    return;

                }

                loopRepeats++;

                x = random(minX, maxX);
                z = random(minZ, maxZ);
                y = worldTp.getHighestBlockYAt(x, z);
                location = new Location(worldTp, x, y, z);


            } while (location.getBlock().isLiquid() || location.getBlock().getBlockData() instanceof Waterlogged || location.getBlock().isPassable());
        }

        if(args.length != 0) {

            List<String> stringList = new ArrayList<String>(Arrays.asList(args));

            if(stringList.contains("-c")) {
                player.sendMessage(x + "/" + y + "/" + z);
            }

            if(stringList.contains("-d") && player.hasPermission("rtp.admin")) {
                location.getBlock().setType(Material.GOLD_BLOCK);
            }
            if(stringList.contains("-a") && player.hasPermission("rtp.admin")) {

                Location playerLoc = player.getLocation();

                player.sendMessage("Passable: " + playerLoc.getBlock().isPassable() + "\nBlock Waterlogged: " + (playerLoc.getBlock().getBlockData() instanceof Waterlogged) + "\nLiquid: " + playerLoc.getBlock().isLiquid() + "\nLocation: "
                        + playerLoc.getX() + "/" + playerLoc.getY() + "/" + playerLoc.getZ());
                return;
            }

        }

        if(RandomTeleport.configFile.getBoolean("options.fall.enabled")) {
            player.teleport(new Location(worldTp, x + 0.5, y + RandomTeleport.configFile.getDouble("options.fall.hight"), z + 0.5));

            PotionEffect slowFall = new PotionEffect(PotionEffectType.SLOW_FALLING, RandomTeleport.configFile.getInt("options.fall.effect-time")*20,1,true,false,false);
            player.addPotionEffect(slowFall);
        }
        else {
            player.teleport(new Location(worldTp, x + 0.5, y + 2, z + 0.5));
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RandomTeleport.configFile.getString("messages.teleported")));

    }

    private int random(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
