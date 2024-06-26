package com.badbones69.crazyvouchers;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyvouchers.api.enums.PersistentKeys;
import com.badbones69.crazyvouchers.api.enums.Messages;
import com.badbones69.crazyvouchers.utils.MsgUtils;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyvouchers.common.config.ConfigManager;
import us.crazycrew.crazyvouchers.common.config.types.ConfigKeys;
import com.badbones69.crazyvouchers.api.plugin.CrazyHandler;
import java.util.HashMap;
import java.util.List;

public class Methods {

    @NotNull
    private final CrazyVouchers plugin = CrazyVouchers.get();
    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    @NotNull
    private final ConfigManager configManager = this.crazyHandler.getConfigManager();
    @NotNull
    private final SettingsManager config = this.configManager.getConfig();
    
    public void removeItem(ItemStack item, Player player) {
        if (item.getAmount() <= 1) {
            player.getInventory().removeItem(item);
        } else if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        }
    }
    
    public String getPrefix(String message) {
        return MsgUtils.color(this.config.getProperty(ConfigKeys.command_prefix) + message);
    }
    
    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }
    
    public boolean isInt(CommandSender sender, String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("{arg}", s);
            Messages.not_a_number.sendMessage(sender, placeholders);
            return false;
        }

        return true;
    }

    public String replacePlaceholders(HashMap<String, String> placeholders, String message, boolean isCommand) {
        for (String placeholder : placeholders.keySet()) {
            message = message.replace(placeholder, placeholders.get(placeholder)).replace(placeholder.toLowerCase(), placeholders.get(placeholder));
        }

        if (isCommand) return message; else return MsgUtils.color(message);
    }
    
    public boolean isOnline(CommandSender sender, String name) {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return true;
        }

        Messages.not_online.sendMessage(sender);
        return false;
    }
    
    public boolean hasPermission(Player player, String perm) {
        if (!player.hasPermission("voucher." + perm)) {
            Messages.no_permission.sendMessage(player);
            return false;
        }

        return true;
    }
    
    public boolean hasPermission(CommandSender sender, String perm) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("voucher." + perm)) {
                Messages.no_permission.sendMessage(player);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    
    public boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
    
    public void firework(Location loc, List<Color> list) {
        if (loc.getWorld() == null) return;

        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(list).trail(false).flicker(false).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);

        PersistentDataContainer container = firework.getPersistentDataContainer();

        container.set(PersistentKeys.no_firework_damage.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        new FoliaRunnable(this.plugin.getServer().getRegionScheduler(), firework.getLocation()) {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runDelayed(this.plugin, 2);
    }
}