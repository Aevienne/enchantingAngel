package me.angelique.enchantingAngel.registry;

import me.angelique.enchantingAngel.config.EnchantConfig;
import me.angelique.enchantingAngel.enchant.CustomEnchant;
import me.angelique.enchantingAngel.enchant.ExperienceEnchant;
import me.angelique.enchantingAngel.enchant.MobAuraEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CustomEnchantRegistry {
    private final Map<String, CustomEnchant> enchantsById = new LinkedHashMap<>();
    private final NamespacedKey enchantIdKey;
    private final NamespacedKey enchantLevelKey;

    public CustomEnchantRegistry(EnchantConfig config, Plugin plugin) {
        this.enchantIdKey = new NamespacedKey(plugin, "ce_id");
        this.enchantLevelKey = new NamespacedKey(plugin, "ce_level");

        register(new MobAuraEnchant(config, plugin));
        register(new ExperienceEnchant(config));
    }

    public void register(CustomEnchant enchant) {
        if (enchant == null) {
            return;
        }
        enchantsById.put(enchant.getId().toLowerCase(), enchant);
    }

    public CustomEnchant getById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return enchantsById.get(id.toLowerCase());
    }

    public Collection<CustomEnchant> getAll() {
        return Collections.unmodifiableCollection(enchantsById.values());
    }

    public NamespacedKey getEnchantIdKey() {
        return enchantIdKey;
    }

    public NamespacedKey getEnchantLevelKey() {
        return enchantLevelKey;
    }
}