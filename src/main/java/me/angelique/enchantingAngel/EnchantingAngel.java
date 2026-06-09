package me.angelique.enchantingAngel;

import me.angelique.angelNCore.services.CompanyService;
import me.angelique.angelNCore.services.ServiceRegistry;
import me.angelique.enchantingAngel.command.CustomEnchantCommand;
import me.angelique.enchantingAngel.config.EnchantConfig;
import me.angelique.enchantingAngel.listener.AnvilListener;
import me.angelique.enchantingAngel.listener.CombatListener;
import me.angelique.enchantingAngel.listener.EnchantTableListener;
import me.angelique.enchantingAngel.listener.ExpListener;
import me.angelique.enchantingAngel.listener.FactoryItemListener;
import me.angelique.enchantingAngel.registry.CustomEnchantRegistry;
import me.angelique.enchantingAngel.service.CustomBookService;
import me.angelique.enchantingAngel.service.CustomEnchantService;
import me.angelique.enchantingAngel.service.EnchantMaterialPool;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class EnchantingAngel extends JavaPlugin {
    private EnchantConfig enchantConfig;
    private CustomEnchantRegistry registry;
    private CustomEnchantService enchantService;
    private CustomBookService bookService;
    private EnchantMaterialPool materialPool;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.enchantConfig = new EnchantConfig(this);
        this.registry = new CustomEnchantRegistry(enchantConfig, this);
        this.enchantService = new CustomEnchantService(registry);
        this.bookService = new CustomBookService(this, registry, enchantService, enchantConfig);

        // Ensure data folder exists for material pool persistence
        new File(getDataFolder(), "data").mkdirs();

        // Wire enchanting material pool (factory supply → enchanting demand)
        materialPool = new EnchantMaterialPool(this);
        CompanyService companyService = ServiceRegistry.getCompanyService();
        if (companyService != null) {
            bookService.setMaterialPool(materialPool, uuid -> {
                String id = companyService.getCompanyForPlayer(uuid);
                return id != null ? id : "";
            });
            Bukkit.getPluginManager().registerEvents(new FactoryItemListener(materialPool), this);
            getLogger().info("enchantingAngel: factory material pool active.");
        } else {
            getLogger().warning("enchantingAngel: AngelNCore CompanyService not found — material requirements disabled.");
        }

        Objects.requireNonNull(getCommand("ce"), "ce command missing")
                .setExecutor(new CustomEnchantCommand(registry, bookService));

        Bukkit.getPluginManager().registerEvents(new CombatListener(registry, enchantService, this), this);
        Bukkit.getPluginManager().registerEvents(new ExpListener(registry, enchantService), this);
        Bukkit.getPluginManager().registerEvents(new EnchantTableListener(bookService), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(bookService, enchantService), this);

        getLogger().info("enchantingAngel enabled with " + registry.getAll().size() + " custom enchants.");
    }

    @Override
    public void onDisable() {
        if (materialPool != null) materialPool.save();
        getLogger().info("enchantingAngel disabled.");
    }

    public EnchantMaterialPool getMaterialPool() { return materialPool; }
}