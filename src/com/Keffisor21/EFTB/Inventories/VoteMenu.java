package com.Keffisor21.EFTB.Inventories;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.Configs.GlobalConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class VoteMenu implements Listener {
    private final Inventory inv;
    private final Player player;

    public VoteMenu(Player player) {
        this.player = player;
        inv = Bukkit.createInventory(null, 27, GlobalConfig.getConfigString("Menu.PrincipalVoteMenu.Title"));

        if(player != null) initializeItems(player);
    }

    private void initializeItems(Player p) {
        List<String> time_desc = GlobalConfig.getConfigList("Menu.PrincipalVoteMenu.TimeLore");
        List<String> speed_desc = GlobalConfig.getConfigList("Menu.PrincipalVoteMenu.SpeedLore");

        inv.setItem(11, createGuiItem(GlobalConfig.getConfigString("Menu.PrincipalVoteMenu.TimeName"), time_desc, new ItemStack(Material.WATCH)));
        inv.setItem(15, createGuiItem(GlobalConfig.getConfigString("Menu.PrincipalVoteMenu.SpeedName"), speed_desc, new ItemStack(Material.DIAMOND_BOOTS)));
    }

    private ItemStack createGuiItem(String name, List<String> desc, ItemStack i) {
        ItemMeta itemMeta = i.getItemMeta();

        itemMeta.setDisplayName(name.replace("&", "§"));
        itemMeta.setLore(desc.stream().map(s -> s.replace("&", "§")).collect(Collectors.toList()));

        i.setItemMeta(itemMeta);
        return i;
    }

    public void openInventory() {
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String invName = e.getView().getTitle();
        if (!invName.equals(GlobalConfig.getConfigString("Menu.PrincipalVoteMenu.Title"))) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Arena arena = ArenaManager.getArenaOfPlayer(p);

        if(arena == null) return;

        if(e.getRawSlot() == 11) {
            new VoteTimeMenu(p).openInventory();
        }

        if(e.getRawSlot() == 15) {
            new VoteSpeedMenu(p).openInventory();
        }

    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        Arena arena = ArenaManager.getArenaOfPlayer(p);

        if(arena == null) return;

        if(((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) && (p.getItemInHand().getType() == Material.EMPTY_MAP)) {
            ItemStack item = p.getItemInHand();

            if(!item.hasItemMeta()) return;

            ItemMeta meta = item.getItemMeta();

            if(meta.getDisplayName().equalsIgnoreCase(GlobalConfig.getConfigString("Item.PrincipalVoteMenu"))) {
                e.setCancelled(true);
                new VoteMenu(p).openInventory();
            }
        }

    }

}
