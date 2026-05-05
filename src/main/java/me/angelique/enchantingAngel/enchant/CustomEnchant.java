package me.angelique.enchantingAngel.enchant;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public interface CustomEnchant {
    String getId();

    String getDisplayName();

    int getMaxLevel();

    int getWeight();

    int getMinEnchantingLevel();

    Set<Material> getSupportedMaterials();

    ChatColor getColor();

    boolean isEnabled();

    default void onAttack(Player attacker, EntityDamageByEntityEvent event, int level) {
    }

    default void onKill(Player killer, EntityDeathEvent event, int level) {
    }

    default String getLoreLine(int level) {
        return getColor() + getDisplayName() + " " + toRoman(Math.max(level, 1));
    }

    default NamespacedKey getEnchantIdKey() {
        return new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "ce_id");
    }

    default NamespacedKey getEnchantLevelKey() {
        return new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "ce_level");
    }

    private static String toRoman(int number) {
        int value = Math.max(1, number);
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (value >= values[i]) {
                value -= values[i];
                builder.append(numerals[i]);
            }
        }
        return builder.toString();
    }
}