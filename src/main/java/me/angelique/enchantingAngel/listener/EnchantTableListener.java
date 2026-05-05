package me.angelique.enchantingAngel.listener;

import me.angelique.enchantingAngel.service.CustomBookService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public final class EnchantTableListener implements Listener {
    private final CustomBookService bookService;

    public EnchantTableListener(CustomBookService bookService) {
        this.bookService = bookService;
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        int cost = event.getExpLevelCost();
        bookService.giveRolledBook(player, cost);
    }
}
