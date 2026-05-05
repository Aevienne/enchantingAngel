package me.angelique.enchantingAngel;

import me.angelique.enchantingAngel.command.CustomEnchantCommand;
import me.angelique.enchantingAngel.config.EnchantConfig;
import me.angelique.enchantingAngel.listener.AnvilListener;
import me.angelique.enchantingAngel.listener.CombatListener;
import me.angelique.enchantingAngel.listener.EnchantTableListener;
import me.angelique.enchantingAngel.listener.ExpListener;
import me.angelique.enchantingAngel.registry.CustomEnchantRegistry;
import me.angelique.enchantingAngel.service.CustomBookService;
import me.angelique.enchantingAngel.service.CustomEnchantService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class EnchantingAngel extends JavaPlugin {
    private EnchantConfig enchantConfig;
    private CustomEnchantRegistry registry;
    private CustomEnchantService enchantService;
    private CustomBookService bookService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.enchantConfig = new EnchantConfig(this);
        this.registry = new CustomEnchantRegistry(enchantConfig, this);
        this.enchantService = new CustomEnchantService(registry);
        this.bookService = new CustomBookService(this, registry, enchantService, enchantConfig);

        Objects.requireNonNull(getCommand("ce"), "ce command missing")
                .setExecutor(new CustomEnchantCommand(registry, bookService));

        Bukkit.getPluginManager().registerEvents(new CombatListener(registry, enchantService, this), this);
        Bukkit.getPluginManager().registerEvents(new ExpListener(registry, enchantService), this);
        Bukkit.getPluginManager().registerEvents(new EnchantTableListener(bookService), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(bookService, enchantService), this);

        getLogger().info("enchantingAngel enabled with " + registry.getAll().size() + " custom enchants.");
    }
}