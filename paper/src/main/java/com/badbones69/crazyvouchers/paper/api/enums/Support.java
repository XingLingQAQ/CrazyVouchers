package com.badbones69.crazyvouchers.paper.api.enums;

import com.badbones69.crazyvouchers.paper.CrazyVouchers;

public enum Support {
    
    PLACEHOLDERAPI("PlaceholderAPI");
    
    private final String name;

    private final CrazyVouchers plugin = CrazyVouchers.getPlugin();
    
    Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        return plugin.getServer().getPluginManager().getPlugin(name) != null;
    }
}