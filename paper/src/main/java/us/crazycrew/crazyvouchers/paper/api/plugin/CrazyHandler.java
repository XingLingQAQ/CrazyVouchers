package us.crazycrew.crazyvouchers.paper.api.plugin;

import com.badbones69.crazyvouchers.paper.CrazyVouchers;
import com.badbones69.crazyvouchers.paper.api.FileManager;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import us.crazycrew.crazyenvoys.common.config.ConfigManager;
import us.crazycrew.crazyenvoys.common.config.types.Config;
import us.crazycrew.crazyvouchers.paper.api.MetricsHandler;
import com.ryderbelserion.cluster.bukkit.BukkitPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.CrazyVouchersPlugin;
import us.crazycrew.crazyvouchers.paper.api.plugin.migration.MigrationService;
import java.io.File;

public class CrazyHandler extends CrazyVouchersPlugin {

    private final @NotNull CrazyVouchers plugin = JavaPlugin.getPlugin(CrazyVouchers.class);

    private BukkitPlugin bukkitPlugin;
    private MetricsHandler metrics;
    private FileManager fileManager;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void install() {
        this.bukkitPlugin = new BukkitPlugin(this.plugin);
        this.bukkitPlugin.enable();

        MigrationService migrationService = new MigrationService();
        migrationService.migrate();

        super.enable();

        LegacyLogger.setName(getConfigManager().getConfig().getProperty(Config.console_prefix));

        this.fileManager = new FileManager();
        this.fileManager
                .registerDefaultGenerateFiles("Example.yml", "/vouchers", "/vouchers")
                .registerDefaultGenerateFiles("Example-Arg.yml", "/vouchers", "/vouchers")
                .registerDefaultGenerateFiles("PlayerHead.yml", "/vouchers", "/vouchers")
                .registerDefaultGenerateFiles("Starter-Money.yml", "/codes", "/codes")
                .registerCustomFilesFolder("/vouchers")
                .registerCustomFilesFolder("/codes")
                .setup();

        boolean metrics = getConfigManager().getConfig().getProperty(Config.toggle_metrics);

        this.metrics = new MetricsHandler();
        if (metrics) this.metrics.start();
    }

    public void uninstall() {
        // Disable crazyenvoys api.
        super.disable();

        // Disable cluster bukkit api.
        this.bukkitPlugin.disable();
    }

    /**
     * Inherited methods.
     */
    @Override
    public @NotNull ConfigManager getConfigManager() {
        return super.getConfigManager();
    }

    public @NotNull FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull MetricsHandler getMetrics() {
        return this.metrics;
    }
}