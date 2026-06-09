package me.angelique.enchantingAngel.listener;

import me.angelique.angelNCore.events.ItemProducedEvent;
import me.angelique.enchantingAngel.service.EnchantMaterialPool;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listens for factory production events (via angelNCore EventBus/Bukkit event system)
 * and deposits produced materials into the enchanting material pool.
 *
 * This is the supply-side of the enchanting economy: factories must run and produce
 * items before high-tier enchants can be applied.
 */
public class FactoryItemListener implements Listener {

    private final EnchantMaterialPool materialPool;

    public FactoryItemListener(EnchantMaterialPool materialPool) {
        this.materialPool = materialPool;
    }

    @EventHandler
    public void onItemProduced(ItemProducedEvent event) {
        String companyId = event.getCompanyId();
        String itemType  = event.getItemType();
        int quantity     = event.getQuantity();

        if (companyId == null || companyId.isBlank()) return;
        materialPool.deposit(companyId, itemType, quantity);
    }
}
