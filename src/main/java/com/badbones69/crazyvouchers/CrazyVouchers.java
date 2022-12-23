package com.badbones69.crazyvouchers;

import com.badbones69.crazyvouchers.api.enums.Messages;
import com.badbones69.crazyvouchers.controllers.GUI;
import com.badbones69.crazyvouchers.api.FileManager;
import com.badbones69.crazyvouchers.api.FileManager.Files;
import com.badbones69.crazyvouchers.api.CrazyManager;
import com.badbones69.crazyvouchers.commands.VoucherCommands;
import com.badbones69.crazyvouchers.commands.VoucherTab;
import com.badbones69.crazyvouchers.controllers.FireworkDamageAPI;
import com.badbones69.crazyvouchers.controllers.VoucherClick;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CrazyVouchers extends JavaPlugin implements Listener {

    private static CrazyVouchers plugin;

    private FileManager fileManager;

    private CrazyManager crazyManager;

    private Methods methods;

    private FireworkDamageAPI fireworkDamageAPI;

    private GUI gui;

    @Override
    public void onEnable() {
        plugin = this;

        fileManager = new FileManager();

        crazyManager = new CrazyManager();

        methods = new Methods();

        fileManager.logInfo(true).setup();

        if (!Files.DATA.getFile().contains("Players")) {
            Files.DATA.getFile().set("Players.Clear", null);
            Files.DATA.saveFile();
        }

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new VoucherClick(), this);
        pluginManager.registerEvents(gui = new GUI(), this);
        pluginManager.registerEvents(fireworkDamageAPI = new FireworkDamageAPI(), this);

        registerCommand(getCommand("vouchers"), new VoucherTab(), new VoucherCommands());

        Messages.addMissingMessages();

        FileConfiguration config = Files.CONFIG.getFile();

        boolean metricsEnabled = Files.CONFIG.getFile().getBoolean("Settings.Toggle-Metrics");
        String metricsPath = Files.CONFIG.getFile().getString("Settings.Toggle-Metrics");

        if (metricsPath == null) {
            config.set("Settings.Toggle-Metrics", true);

            Files.CONFIG.saveFile();
        }

        if (metricsEnabled) new Metrics(this, 4536);

        crazyManager.load();
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    public static CrazyVouchers getPlugin() {
        return plugin;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Methods getMethods() {
        return methods;
    }

    public FireworkDamageAPI getFireworkDamageAPI() {
        return fireworkDamageAPI;
    }

    public GUI getGui() {
        return gui;
    }
}