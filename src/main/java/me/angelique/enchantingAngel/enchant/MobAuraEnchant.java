package me.angelique.enchantingAngel.enchant;

import me.angelique.enchantingAngel.config.EnchantConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.EnumSet;
import java.util.Set;

public final class MobAuraEnchant implements CustomEnchant {
    private static final String MOB_AURA_METADATA = "enchantingangel_mobaura";

    private final EnchantConfig config;
    private final Plugin plugin;

    public MobAuraEnchant(EnchantConfig config, Plugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public String getId() {
        return "mobaura";
    }

    @Override
    public String getDisplayName() {
        return "MobAura CE";
    }

    @Override
    public int getMaxLevel() {
        return config.getMaxLevel(getId(), 3);
    }

    @Override
    public int getWeight() {
        return config.getWeight(getId(), 10);
    }

    @Override
    public int getMinEnchantingLevel() {
        return config.getMinEnchantingLevel(getId(), 15);
    }

    @Override
    public Set<Material> getSupportedMaterials() {
        return EnumSet.of(
                Material.NETHERITE_SWORD,
                Material.DIAMOND_SWORD,
                Material.IRON_SWORD,
                Material.GOLDEN_SWORD,
                Material.STONE_SWORD,
                Material.WOODEN_SWORD,
                Material.NETHERITE_AXE,
                Material.DIAMOND_AXE,
                Material.IRON_AXE
        );
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled(getId());
    }

    @Override
    public void onAttack(Player attacker, EntityDamageByEntityEvent event, int level) {
        if (attacker == null || !attacker.isOnline() || attacker.isDead()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity primaryTarget)) {
            return;
        }

        double radius = config.getDouble("custom-enchants.mobaura.radius-per-level", 2.5D) * level;
        double bonusDamage = config.getDouble("custom-enchants.mobaura.damage-per-level", 1.5D) * level;

        if (radius <= 0.0D || bonusDamage <= 0.0D) {
            return;
        }

        Location center = primaryTarget.getLocation().clone();
        double radiusSquared = radius * radius;

        center.getWorld().spawnParticle(
                Particle.SWEEP_ATTACK,
                center,
                8 + (level * 2),
                0.8,
                0.3,
                0.8,
                0.01
        );
        center.getWorld().spawnParticle(
                Particle.CRIT,
                center.clone().add(0.0, 0.8, 0.0),
                12 + (level * 4),
                0.9,
                0.5,
                0.9,
                0.05
        );
        center.getWorld().playSound(
                center,
                Sound.ENTITY_PLAYER_ATTACK_SWEEP,
                0.8F,
                1.0F + (level * 0.05F)
        );

        primaryTarget.getNearbyEntities(radius, radius, radius).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .filter(entity -> entity != attacker)
                .filter(entity -> entity != primaryTarget)
                .filter(entity -> !entity.isDead())
                .filter(entity -> entity.getWorld().equals(center.getWorld()))
                .filter(entity -> entity.getLocation().distanceSquared(center) <= radiusSquared)
                .forEach(entity -> applyAuraDamage(entity, attacker, bonusDamage));
    }

    private void applyAuraDamage(LivingEntity entity, Player attacker, double damage) {
        entity.setMetadata(MOB_AURA_METADATA, new FixedMetadataValue(plugin, true));
        try {
            entity.damage(damage, attacker);
            entity.getWorld().spawnParticle(
                    Particle.DAMAGE_INDICATOR,
                    entity.getLocation().clone().add(0.0, 1.0, 0.0),
                    6,
                    0.3,
                    0.4,
                    0.3,
                    0.05
            );
        } finally {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (entity.isValid()) {
                    entity.removeMetadata(MOB_AURA_METADATA, plugin);
                }
            });
        }
    }
}
