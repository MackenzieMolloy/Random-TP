package com.patrity.rtp.commands;

import com.patrity.rtp.Rtp;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RtpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = getLoc(player);
            player.teleport(location);
            player.sendMessage("Whoosh!");
            Rtp.SINGLETON.logger.info(player.getLocation().toString());
        }
        return true;
    }
    private Location getLoc(Player player) {
        ConfigurationSection bounds = Rtp.SINGLETON.config.getConfigurationSection("boundaries");
        assert bounds != null;
        int maxX = bounds.getInt("max-x");
        int minX = bounds.getInt("min-x");
        int maxZ = bounds.getInt("max-z");
        int minZ = bounds.getInt("min-z");

        String world = bounds.getString("world");
        World worldTp = player.getWorld();

        assert world != null;
        if (!world.equalsIgnoreCase("none")) {
            if (!(Rtp.SINGLETON.getServer().getWorld(world) == null)) {
                worldTp = Rtp.SINGLETON.getServer().getWorld(world);
            } else {
                Rtp.SINGLETON.logger.warning("[RTP] World " + world + " not found! Please Verify config!");
            }
        }

        int x = random(minX, maxX);
        int z = random(minZ, maxZ);
        int y = player.getWorld().getHighestBlockYAt(x, z) + 2;
        return new Location(worldTp, x, y ,z);
    }

    private int random(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
