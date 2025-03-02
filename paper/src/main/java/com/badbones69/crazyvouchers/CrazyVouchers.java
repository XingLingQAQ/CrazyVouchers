package com.badbones69.crazyvouchers;

import com.badbones69.crazyvouchers.api.CrazyManager;
import com.badbones69.crazyvouchers.api.InventoryManager;
import com.badbones69.crazyvouchers.api.builders.types.VoucherMenu;
import com.badbones69.crazyvouchers.commands.features.CommandHandler;
import com.badbones69.crazyvouchers.config.ConfigManager;
import com.badbones69.crazyvouchers.listeners.FireworkDamageListener;
import com.badbones69.crazyvouchers.listeners.VoucherClickListener;
import com.badbones69.crazyvouchers.listeners.VoucherCraftListener;
import com.badbones69.crazyvouchers.listeners.VoucherMiscListener;
import com.badbones69.crazyvouchers.support.MetricsWrapper;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import com.ryderbelserion.fusion.paper.Fusion;
import com.ryderbelserion.fusion.paper.FusionApi;
import com.ryderbelserion.fusion.paper.files.FileManager;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazyvouchers.config.types.ConfigKeys;
import java.util.List;
import java.util.Locale;

public class CrazyVouchers extends JavaPlugin {

    public @NotNull static CrazyVouchers get() {
        return JavaPlugin.getPlugin(CrazyVouchers.class);
    }

    private final long startTime;

    public CrazyVouchers() {
        this.startTime = System.nanoTime();
    }

    private InventoryManager inventoryManager;

    private CrazyManager crazyManager;

    private HeadDatabaseAPI api;

    private final FusionApi fusionApi = FusionApi.get();

    private FileManager fileManager;

    @Override
    public void onEnable() {
        this.fusionApi.enable(this);

        this.api = this.fusionApi.getDatabaseAPI();

        this.fileManager = this.fusionApi.getFileManager();

        ConfigManager.load(getDataFolder());

        boolean loadOldWay = ConfigManager.getConfig().getProperty(ConfigKeys.mono_file);

        this.fileManager.addFile("users.yml", FileType.YAML).addFile("data.yml", FileType.YAML);

        if (loadOldWay) {
            this.fileManager.addFile("voucher-codes.yml", FileType.YAML).addFile("vouchers.yml", FileType.YAML);
        } else {
            this.fileManager.addFolder("codes", FileType.YAML).addFolder("vouchers", FileType.YAML);
        }

        new MetricsWrapper(4536).start();

        this.crazyManager = new CrazyManager();
        this.crazyManager.load();

        this.inventoryManager = new InventoryManager();

        Methods.janitor();

        PluginManager pluginManager = getServer().getPluginManager();

        new CommandHandler();

        List.of(
                new FireworkDamageListener(),
                new VoucherClickListener(),
                new VoucherCraftListener(),
                new VoucherMiscListener(),
                new VoucherMenu()
        ).forEach(event -> pluginManager.registerEvents(event, this));

        if (getFusion().isVerbose()) {
            getComponentLogger().info("Done ({})!", String.format(Locale.ROOT, "%.3fs", (double) (System.nanoTime() - this.startTime) / 1.0E9D));
        }
    }

    public @Nullable final HeadDatabaseAPI getApi() {
        if (this.api == null) {
            return null;
        }

        return this.api;
    }

    public @NotNull final Fusion getFusion() {
        return this.fusionApi.getFusion();
    }

    public @NotNull final FusionApi getFusionApi() {
        return this.fusionApi;
    }

    public @NotNull final InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public @NotNull final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public @NotNull FileManager getFileManager() {
        return this.fileManager;
    }
}