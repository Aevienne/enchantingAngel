package me.angelique.enchantingAngel.listener;

import me.angelique.enchantingAngel.enchant.CustomEnchant;
import me.angelique.enchantingAngel.registry.CustomEnchantRegistry;
import me.angelique.enchantingAngel.service.CustomEnchantService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public final class ExpListener implements Listener {
    private final CustomEnchantRegistry registry;
    private final CustomEnchantService enchantService;

    public ExpListener(CustomEnchantRegistry registry, CustomEnchantService enchantService) {
        this.registry = registry;
        this.enchantService = enchantService;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        for (CustomEnchant enchant : registry.getAll()) {
            int level = enchantService.getLevel(weapon, enchant);
            if (level > 0) {
                enchant.onKill(killer, event, level);
            }
        }
    }
}
