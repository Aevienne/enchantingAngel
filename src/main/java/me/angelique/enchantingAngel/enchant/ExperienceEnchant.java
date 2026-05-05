package me.angelique.enchantingAngel.enchant;

import me.angelique.enchantingAngel.config.EnchantConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.EnumSet;
import java.util.Set;

public final class ExperienceEnchant implements CustomEnchant {
    private final EnchantConfig config;

    public ExperienceEnchant(EnchantConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "experience";
    }

    @Override
    public String getDisplayName() {
        return "Experience";
    }

    @Override
    public int getMaxLevel() {
        return config.getMaxLevel(getId(), 2);
    }

    @Override
    public int getWeight() {
        return config.getWeight(getId(), 12);
    }

    @Override
    public int getMinEnchantingLevel() {
        return config.getMinEnchantingLevel(getId(), 8);
    }

    @Override
    public Set<Material> getSupportedMaterials() {
        return EnumSet.of(Material.NETHERITE_SWORD, Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD, Material.NETHERITE_AXE, Material.DIAMOND_AXE, Material.IRON_AXE, Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE);
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GREEN;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled(getId());
    }

    @Override
    public void onKill(Player killer, EntityDeathEvent event, int level) {
        double multiplier = config.getDouble("custom-enchants.experience.bonus-multiplier-per-level", 1.0D);
        int current = event.getDroppedExp();
        int extra = (int) Math.round(current * (multiplier * level));
        event.setDroppedExp(current + extra);
    }
}
