package com.patrity.rtp;

import com.patrity.rtp.Utilities.CommentedConfiguration;
import com.patrity.rtp.commands.RtpCommand;
import com.patrity.rtp.commands.RtpReloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class Rtp extends JavaPlugin {

    public static Rtp RandomTeleport;
    public Logger logger = Bukkit.getLogger();

    public CommentedConfiguration configFile;


    @Override
    public void onEnable() {
        Rtp.RandomTeleport = this;
        this.generateFiles();

        Objects.requireNonNull(this.getCommand("rtp")).setExecutor(new RtpCommand(this));
        this.getCommand("rtp-reload").setExecutor(new RtpReloadCommand(this));
        logger.info("[" + this.getName() + "] Finished Loading");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /*
        Generates configuration files
     */
    public void generateFiles() {

        saveDefaultConfig();

        File file = new File(getDataFolder(), "config.yml");
        configFile = CommentedConfiguration.loadConfiguration(file);

        try {
            configFile.syncWithConfig(file, getResource("config.yml"), "ignore");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }

    }

    public void saveFiles() {

        try {
            configFile.save(new File(getDataFolder(), "config.yml"));
        } catch(IOException ex) {
            ex.printStackTrace();
        }

    }

}