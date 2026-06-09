package me.angelique.enchantingAngel.service;

import me.angelique.enchantingAngel.EnchantingAngel;
import me.angelique.enchantingAngel.config.EnchantConfig;
import me.angelique.enchantingAngel.enchant.CustomEnchant;
import me.angelique.enchantingAngel.registry.CustomEnchantRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public final class CustomBookService {
    private final EnchantingAngel plugin;
    private final CustomEnchantRegistry registry;
    private final CustomEnchantService enchantService;
    private final EnchantConfig config;
    private final Random random = new Random();
    private EnchantMaterialPool materialPool;
    private Function<UUID, String> companyResolver; // player UUID -> companyId

    public CustomBookService(EnchantingAngel plugin, CustomEnchantRegistry registry, CustomEnchantService enchantService, EnchantConfig config) {
        this.plugin = plugin;
        this.registry = registry;
        this.enchantService = enchantService;
        this.config = config;
    }

    public void setMaterialPool(EnchantMaterialPool pool, Function<UUID, String> resolver) {
        this.materialPool = pool;
        this.companyResolver = resolver;
    }

    public ItemStack createBook(CustomEnchant enchant, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta == null) {
            return book;
        }
        int safeLevel = Math.max(1, Math.min(level, enchant.getMaxLevel()));
        meta.setDisplayName(enchant.getColor() + enchant.getDisplayName() + " " + safeLevel);
        List<String> lore = new ArrayList<>();
        lore.add("§7Custom Enchant Book");
        lore.add("§7Apply with an anvil.");
        lore.add("§7Targets: §f" + formatMaterials(enchant));
        lore.add(enchant.getLoreLine(safeLevel));
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(registry.getEnchantIdKey(), PersistentDataType.STRING, enchant.getId());
        meta.getPersistentDataContainer().set(registry.getEnchantLevelKey(), PersistentDataType.INTEGER, safeLevel);
        book.setItemMeta(meta);
        return book;
    }

    public boolean isCustomBook(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.ENCHANTED_BOOK || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta.getPersistentDataContainer().has(registry.getEnchantIdKey(), PersistentDataType.STRING)
                && meta.getPersistentDataContainer().has(registry.getEnchantLevelKey(), PersistentDataType.INTEGER);
    }

    public CustomEnchant getEnchantFromBook(ItemStack itemStack) {
        if (!isCustomBook(itemStack)) {
            return null;
        }
        String id = itemStack.getItemMeta().getPersistentDataContainer().get(registry.getEnchantIdKey(), PersistentDataType.STRING);
        return registry.getById(id);
    }

    public int getLevelFromBook(ItemStack itemStack) {
        if (!isCustomBook(itemStack)) {
            return 0;
        }
        Integer level = itemStack.getItemMeta().getPersistentDataContainer().get(registry.getEnchantLevelKey(), PersistentDataType.INTEGER);
        return level == null ? 0 : level;
    }

    public CustomEnchant rollEnchant(int enchantingCost) {
        List<CustomEnchant> candidates = registry.getAll().stream()
                .filter(CustomEnchant::isEnabled)
                .filter(enchant -> enchantingCost >= enchant.getMinEnchantingLevel())
                .toList();
        if (candidates.isEmpty()) {
            return null;
        }
        int totalWeight = candidates.stream().mapToInt(CustomEnchant::getWeight).sum();
        int roll = random.nextInt(totalWeight) + 1;
        int cursor = 0;
        for (CustomEnchant enchant : candidates) {
            cursor += enchant.getWeight();
            if (roll <= cursor) {
                return enchant;
            }
        }
        return candidates.getFirst();
    }

    public int rollLevel(CustomEnchant enchant) {
        return 1 + random.nextInt(enchant.getMaxLevel());
    }

    public boolean shouldGrantBook() {
        double chance = config.getDouble("custom-book-system.enchanting-table-chance", 0.08D);
        return random.nextDouble() <= chance;
    }

    public void giveRolledBook(Player player, int enchantingCost) {
        if (!shouldGrantBook()) {
            return;
        }
        CustomEnchant enchant = rollEnchant(enchantingCost);
        if (enchant == null) {
            return;
        }
        ItemStack book = createBook(enchant, rollLevel(enchant));
        boolean direct = config.getBoolean("custom-book-system.add-book-directly-to-inventory", true);
        if (direct) {
            player.getInventory().addItem(book);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), book);
        }
        player.sendMessage("§dYou discovered a custom enchant book: " + enchant.getDisplayName() + "!");
        if (config.getBoolean("custom-book-system.broadcast-rare-books", false)) {
            Bukkit.broadcastMessage("§d" + player.getName() + " found a custom enchant book: " + enchant.getDisplayName() + "!");
        }
    }

    public ItemStack applyBookToItem(ItemStack target, ItemStack book) {
        return applyBookToItem(target, book, null);
    }

    /**
     * Applies a custom enchant book to an item. If the enchant has a material requirement
     * and a player is provided, the cost is checked against the company's factory pool.
     * Returns null if the enchant cannot be applied (wrong target, missing materials, etc.).
     */
    public ItemStack applyBookToItem(ItemStack target, ItemStack book, Player player) {
        if (target == null || book == null || !isCustomBook(book)) {
            return null;
        }
        CustomEnchant enchant = getEnchantFromBook(book);
        int level = getLevelFromBook(book);
        if (enchant == null || level <= 0) {
            return null;
        }

        // Check and consume material requirement from company factory pool
        if (player != null && materialPool != null && companyResolver != null) {
            CustomEnchant.MaterialRequirement req = enchant.getMaterialRequirement();
            if (req != null) {
                String companyId = companyResolver.apply(player.getUniqueId());
                int needed = req.amountPerLevel() * level;
                int have = materialPool.getAmount(companyId, req.materialName());
                if (have < needed) {
                    player.sendMessage("§8[§denchantingAngel§8] §cNeed §e" + needed + "x "
                            + formatName(req.materialName()) + " §cfrom factory production. Have §e" + have + "§c.");
                    return null;
                }
                materialPool.consume(companyId, req.materialName(), needed);
            }
        }

        ItemStack result = target.clone();
        if (!enchantService.applyEnchant(result, enchant, level)) {
            return null;
        }
        return result;
    }

    private static String formatName(String key) {
        String[] words = key.toLowerCase().replace("_", " ").split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    private String formatMaterials(CustomEnchant enchant) {
        return enchant.getSupportedMaterials().stream()
                .limit(4)
                .map(Enum::name)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Various");
    }
}
