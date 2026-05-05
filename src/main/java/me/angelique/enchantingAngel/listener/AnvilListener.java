package me.angelique.enchantingAngel.listener;

import me.angelique.enchantingAngel.service.CustomBookService;
import me.angelique.enchantingAngel.service.CustomEnchantService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class AnvilListener implements Listener {
    private final CustomBookService bookService;
    private final CustomEnchantService enchantService;

    public AnvilListener(CustomBookService bookService, CustomEnchantService enchantService) {
        this.bookService = bookService;
        this.enchantService = enchantService;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack left = inventory.getFirstItem();
        ItemStack right = inventory.getSecondItem();
        if (left == null || right == null) {
            return;
        }
        ItemStack result = bookService.applyBookToItem(left, right);
        if (result != null) {
            event.setResult(result);
            inventory.setRepairCost(10);
        }
    }

    @EventHandler
    public void onAnvilTake(InventoryClickEvent event) {
        InventoryView view = event.getView();
        if (!(view.getTopInventory() instanceof AnvilInventory anvilInventory)) {
            return;
        }
        if (event.getRawSlot() != 2 || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack left = anvilInventory.getFirstItem();
        ItemStack right = anvilInventory.getSecondItem();
        if (left == null || right == null || !bookService.isCustomBook(right)) {
            return;
        }
        ItemStack currentResult = event.getCurrentItem();
        if (currentResult == null) {
            return;
        }
        if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.SHIFT_LEFT) {
            return;
        }
        player.getServer().getScheduler().runTaskLater(player.getServer().getPluginManager().getPlugin("enchantingAngel"), () -> {
            anvilInventory.setFirstItem(null);
            ItemStack updatedBook = right.clone();
            updatedBook.setAmount(updatedBook.getAmount() - 1);
            anvilInventory.setSecondItem(updatedBook.getAmount() > 0 ? updatedBook : null);
        }, 1L);
    }
}
