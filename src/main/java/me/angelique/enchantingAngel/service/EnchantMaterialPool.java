package me.angelique.enchantingAngel.service;

import me.angelique.enchantingAngel.EnchantingAngel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Tracks factory-produced materials available for enchanting, keyed by company ID.
 * High-tier factory runs deposit items here; custom enchants can require materials
 * from the pool before they can be applied or granted.
 */
public class EnchantMaterialPool {

    private final EnchantingAngel plugin;
    // companyId -> (materialName -> qty)
    private final Map<String, Map<String, Integer>> pool = new ConcurrentHashMap<>();
    private File dataFile;

    public EnchantMaterialPool(EnchantingAngel plugin) {
        this.plugin = plugin;
        load();
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    /** Called when a factory produces items — adds to the company's material pool. */
    public void deposit(String companyId, String materialName, int qty) {
        if (companyId == null || companyId.isBlank()) return;
        pool.computeIfAbsent(companyId, k -> new ConcurrentHashMap<>())
            .merge(materialName.toUpperCase(), qty, Integer::sum);
        save();
    }

    /**
     * Attempts to consume {@code qty} of {@code materialName} from the company pool.
     * Returns true and deducts if sufficient; returns false without touching the pool if not.
     */
    public boolean consume(String companyId, String materialName, int qty) {
        if (companyId == null || companyId.isBlank()) return false;
        Map<String, Integer> companyPool = pool.get(companyId);
        if (companyPool == null) return false;
        String key = materialName.toUpperCase();
        int have = companyPool.getOrDefault(key, 0);
        if (have < qty) return false;
        int remaining = have - qty;
        if (remaining == 0) companyPool.remove(key);
        else companyPool.put(key, remaining);
        save();
        return true;
    }

    public int getAmount(String companyId, String materialName) {
        if (companyId == null || companyId.isBlank()) return 0;
        Map<String, Integer> companyPool = pool.get(companyId);
        if (companyPool == null) return 0;
        return companyPool.getOrDefault(materialName.toUpperCase(), 0);
    }

    public Map<String, Integer> getCompanyPool(String companyId) {
        return pool.getOrDefault(companyId, new HashMap<>());
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "data/enchant-materials.yml");
        if (!dataFile.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        pool.clear();
        ConfigurationSection companies = yaml.getConfigurationSection("pool");
        if (companies == null) return;
        for (String company : companies.getKeys(false)) {
            ConfigurationSection mats = yaml.getConfigurationSection("pool." + company);
            if (mats == null) continue;
            Map<String, Integer> companyPool = new ConcurrentHashMap<>();
            for (String mat : mats.getKeys(false)) {
                companyPool.put(mat, mats.getInt(mat));
            }
            pool.put(company, companyPool);
        }
    }

    public void save() {
        if (dataFile == null) dataFile = new File(plugin.getDataFolder(), "data/enchant-materials.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        pool.forEach((company, mats) ->
            mats.forEach((mat, qty) -> yaml.set("pool." + company + "." + mat, qty)));
        try { yaml.save(dataFile); }
        catch (IOException e) { plugin.getLogger().log(Level.SEVERE, "Failed to save enchant-materials.yml", e); }
    }
}
