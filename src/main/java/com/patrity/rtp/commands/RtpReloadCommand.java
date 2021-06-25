package com.patrity.rtp.commands;

import com.patrity.rtp.Rtp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class RtpReloadCommand implements CommandExecutor {

    private final Rtp RandomTeleport;
    public RtpReloadCommand(Rtp RandomTeleport) {
        this.RandomTeleport = RandomTeleport;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            if(sender.hasPermission("rtp.reload")) {
                pluginReload(sender);
            }
            else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', RandomTeleport.configFile.getString("messages.no-permission")));
        }

        else {

            pluginReload(sender);

        }

        return false;
    }

    public void pluginReload(CommandSender sender) {

        CompletableFuture.runAsync(RandomTeleport::generateFiles).whenComplete((success, error) -> {
            if(error != null) {
                sender.sendMessage(ChatColor.RED + "An error occurred, please check the server console.");
                error.printStackTrace();
                return;
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', RandomTeleport.configFile.getString("messages.reloaded-config")));
        });

    }

}
