package me.angelique.enchantingAngel.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnchantConfig {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public EnchantConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public boolean isEnabled(String enchantId) {
        return getBoolean("custom-enchants." + enchantId + ".enabled", true);
    }

    public int getMaxLevel(String enchantId, int defaultValue) {
        return config.getInt("custom-enchants." + enchantId + ".max-level", defaultValue);
    }

    public int getWeight(String enchantId, int defaultValue) {
        return config.getInt("custom-enchants." + enchantId + ".weight", defaultValue);
    }

    public int getMinEnchantingLevel(String enchantId, int defaultValue) {
        return config.getInt("custom-enchants." + enchantId + ".min-enchanting-level", defaultValue);
    }

    public double getDouble(String path, double defaultValue) {
        return config.getDouble(path, defaultValue);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}