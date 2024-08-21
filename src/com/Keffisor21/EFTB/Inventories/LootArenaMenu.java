package com.Keffisor21.EFTB.Inventories;

import com.Keffisor21.EFTB.Utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class LootArenaMenu {
    private final Inventory inv;
    private final Player player;

    public LootArenaMenu(Player player) {
        this.player = player;
        inv = Bukkit.createInventory(null, 54, "§7§lEFTB");

        if(player != null) initializeItems(player);
    }

    private void initializeItems(Player p) {
        inv.setItem(getRandomSlot(), createGuiItem(new ItemStack(Material.DIAMOND_SWORD), Lists.newArrayList(
                Utils.getEnchantment("DAMAGE_ALL", "SHARPNESS"),
                Utils.getEnchantment("DURABILITY", "UNBREAKING"),
                Enchantment.FIRE_ASPECT
        ), Lists.newArrayList(5, 3, 2)));

        inv.setItem(getRandomSlot(), createGuiItem(new ItemStack(Material.BOW), Lists.newArrayList(
                Utils.getEnchantment("ARROW_DAMAGE", "POWER"),
                Utils.getEnchantment("ARROW_INFINITE", "INFINITY"),
                Utils.getEnchantment("ARROW_FIRE", "FLAME")
        ), Lists.newArrayList(5, 1, 1)));

        Lists.newArrayList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS).forEach(material -> {
            inv.setItem(getRandomSlot(), createGuiItem(new ItemStack(material), Lists.newArrayList(
                    Utils.getEnchantment("PROTECTION_ENVIRONMENTAL", "PROTECTION"),
                    Utils.getEnchantment("PROTECTION_FIRE", "FIRE_PROTECTION"),
                    Utils.getEnchantment("PROTECTION_FALL", "FEATHER_FALLING")
            ), Lists.newArrayList(4, 4, 4, 2)));
        });

        inv.setItem(getRandomSlot(), new ItemStack(Material.ARROW, 64));
        inv.setItem(getRandomSlot(), new ItemStack(Material.GOLDEN_APPLE, 3));

        inv.setItem(getRandomSlot(), Utils.getPotionEffect((byte) 8226, PotionEffectType.SPEED, 90 * 20, 1));
        inv.setItem(getRandomSlot(), Utils.getPotionEffect((byte) 8227, PotionEffectType.FIRE_RESISTANCE, 180 * 20, 0));
        inv.setItem(getRandomSlot(), Utils.getPotionEffect((byte) 8229, PotionEffectType.HEAL, 1, 1));
    }

    private ItemStack createGuiItem(ItemStack item, List<Enchantment> enchantments, List<Integer> levels) {
        ItemMeta itemSwordMeta = item.getItemMeta();

        for(int i = 0; i < enchantments.size(); i++)
            itemSwordMeta.addEnchant(enchantments.get(i), levels.get(i) != null ? levels.get(i): 1, true);

        item.setItemMeta(itemSwordMeta);

        return item;
    }

    public void openInventory() {
        player.openInventory(inv);
    }

    private List<Integer> slots = Lists.newArrayList();
    private int getRandomSlot() {
        if(slots.isEmpty()) {
            for(int i = 0; i < 53; i++) slots.add(i);
            Collections.shuffle(slots);
        }

        int i = slots.get(0);
        slots.remove(0);

        return i;
    }

}
