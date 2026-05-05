package me.angelique.enchantingAngel.service;

import me.angelique.enchantingAngel.enchant.CustomEnchant;
import me.angelique.enchantingAngel.registry.CustomEnchantRegistry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class CustomEnchantService {
    private final CustomEnchantRegistry registry;

    public CustomEnchantService(CustomEnchantRegistry registry) {
        this.registry = registry;
    }

    public int getLevel(ItemStack item, CustomEnchant enchant) {
        if (item == null || enchant == null || !item.hasItemMeta()) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        String enchantId = container.get(registry.getEnchantIdKey(), PersistentDataType.STRING);
        Integer level = container.get(registry.getEnchantLevelKey(), PersistentDataType.INTEGER);

        if (enchantId == null || level == null) {
            return 0;
        }

        if (!enchant.getId().equalsIgnoreCase(enchantId)) {
            return 0;
        }

        return level;
    }

    public boolean applyEnchant(ItemStack item, CustomEnchant enchant, int level) {
        if (item == null || enchant == null || level <= 0) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(registry.getEnchantIdKey(), PersistentDataType.STRING, enchant.getId());
        container.set(registry.getEnchantLevelKey(), PersistentDataType.INTEGER, level);

        List<String> lore = meta.hasLore() && meta.getLore() != null
                ? new ArrayList<>(meta.getLore())
                : new ArrayList<>();

        lore.removeIf(line -> line != null && line.contains(enchant.getDisplayName()));
        lore.add(enchant.getLoreLine(level));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return true;
    }
}