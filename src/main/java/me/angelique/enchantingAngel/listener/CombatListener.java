package me.angelique.enchantingAngel.listener;

import me.angelique.enchantingAngel.enchant.CustomEnchant;
import me.angelique.enchantingAngel.registry.CustomEnchantRegistry;
import me.angelique.enchantingAngel.service.CustomEnchantService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public final class CombatListener implements Listener {
    private static final String MOB_AURA_METADATA = "enchantingangel_mobaura";
    private final CustomEnchantRegistry registry;
    private final CustomEnchantService enchantService;
    private final Plugin plugin;

    public CombatListener(CustomEnchantRegistry registry, CustomEnchantService enchantService, Plugin plugin) {
        this.registry = registry;
        this.enchantService = enchantService;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (hasMobAuraMetadata(event.getEntity())) {
            return;
        }

        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        if (attacker.isDead()) {
            return;
        }

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon == null || weapon.getType().isAir()) {
            return;
        }

        for (CustomEnchant enchant : registry.getAll()) {
            int level = enchantService.getLevel(weapon, enchant);
            if (level > 0) {
                enchant.onAttack(attacker, event, level);
            }
        }
    }

    private boolean hasMobAuraMetadata(Entity entity) {
        if (!entity.hasMetadata(MOB_AURA_METADATA)) {
            return false;
        }

        for (MetadataValue value : entity.getMetadata(MOB_AURA_METADATA)) {
            if (value != null && value.getOwningPlugin() == plugin && value.asBoolean()) {
                return true;
            }
        }

        return false;
    }
}
