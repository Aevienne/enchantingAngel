package me.angelique.enchantingAngel.command;

import me.angelique.enchantingAngel.enchant.CustomEnchant;
import me.angelique.enchantingAngel.registry.CustomEnchantRegistry;
import me.angelique.enchantingAngel.service.CustomBookService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CustomEnchantCommand implements CommandExecutor {
    private final CustomEnchantRegistry registry;
    private final CustomBookService bookService;

    public CustomEnchantCommand(CustomEnchantRegistry registry, CustomBookService bookService) {
        this.registry = registry;
        this.bookService = bookService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("enchantingangel.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 4 || !args[0].equalsIgnoreCase("givebook")) {
            sender.sendMessage("§eUsage: /ce givebook <player> <enchant> <level>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }
        CustomEnchant enchant = registry.getById(args[2]);
        if (enchant == null) {
            sender.sendMessage("§cUnknown enchant.");
            return true;
        }
        int level;
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException exception) {
            sender.sendMessage("§cInvalid level.");
            return true;
        }
        ItemStack book = bookService.createBook(enchant, level);
        target.getInventory().addItem(book);
        sender.sendMessage("§aGave " + target.getName() + " a " + enchant.getDisplayName() + " book.");
        target.sendMessage("§aYou received a custom enchant book: " + enchant.getDisplayName() + ".");
        return true;
    }
}
