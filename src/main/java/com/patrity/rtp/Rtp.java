package com.patrity.rtp;

import com.patrity.rtp.commands.RtpCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public final class Rtp extends JavaPlugin {

    public static Rtp SINGLETON;
    public Logger logger = Bukkit.getLogger();
    public Configuration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Rtp.SINGLETON = this;
        loadConfig();
        Objects.requireNonNull(this.getCommand("rtp")).setExecutor(new RtpCommand());
        logger.info("[" + this.getName() + "] Finished Loading");
        logger.info("Max-x: " + config.getConfigurationSection("boundaries").getInt("max-x"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadConfig() {
        logger.info("[" + this.getName() + "]Loading Configuration...");
        File dir = getDataFolder();
        if (!dir.exists()) {
            if (dir.mkdir()) {
                getLogger().info("[" + this.getName() + "] First run detected, creating data folder");
                this.saveDefaultConfig();
                getConfig().options().copyDefaults(true);
                saveConfig();
            } else {
                getLogger().warning("[" + this.getName() + "] Data folder creation failed");
            }
        }
        config = getConfig();
        logger.info("[" + this.getName() + "]Configuration Loaded!");
    }
}