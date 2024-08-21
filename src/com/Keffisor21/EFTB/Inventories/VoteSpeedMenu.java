package com.Keffisor21.EFTB.Inventories;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.Configs.GlobalConfig;
import com.Keffisor21.EFTB.Nms.Sound;
import com.Keffisor21.EFTB.Utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class VoteSpeedMenu implements Listener {
    private final Inventory inv;
    private final Player player;

    public VoteSpeedMenu(Player player) {
        this.player = player;
        inv = Bukkit.createInventory(null, 27, GlobalConfig.getConfigString("Menu.SpeedVoteMenu.Title"));

        if(player != null) initializeItems(player);
    }

    private void initializeItems(Player p) {
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if(arena == null) return;

        int normal_count = arena.getSpeedVotesCount("NORMAL");
        int speed_2_count = arena.getSpeedVotesCount("II");
        int speed_3_count = arena.getSpeedVotesCount("III");

        List<String> speed1_desc = Utils.setVariables(GlobalConfig.getConfigList("Menu.SpeedVoteMenu.ItemSpeedNormalLore"), Lists.newArrayList("%votes%"), Lists.newArrayList(normal_count));
        List<String> speed2_desc = Utils.setVariables(GlobalConfig.getConfigList("Menu.SpeedVoteMenu.ItemSpeedIILore"), Lists.newArrayList("%votes%"), Lists.newArrayList(speed_2_count));
        List<String> speed3_desc = Utils.setVariables(GlobalConfig.getConfigList("Menu.SpeedVoteMenu.ItemSpeedIIILore"), Lists.newArrayList("%votes%"), Lists.newArrayList(speed_3_count));

        inv.setItem(10, createGuiItem(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.ItemSpeedNormalName"), speed1_desc, new ItemStack(Material.BARRIER, normal_count == 0 ? 1: normal_count)));
        inv.setItem(13, createGuiItem(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.ItemSpeedIIName"), speed2_desc, new ItemStack(Material.IRON_BOOTS, speed_2_count == 0 ? 1: speed_2_count)));
        inv.setItem(16, createGuiItem(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.ItemSpeedIIIName"), speed3_desc, new ItemStack(Material.DIAMOND_BOOTS, speed_3_count == 0 ? 1: speed_3_count)));
    }

    private ItemStack createGuiItem(String name, List<String> desc, ItemStack i) {
        ItemMeta itemMeta = i.getItemMeta();

        itemMeta.setDisplayName(name.replace("&", "§"));
        itemMeta.setLore(desc.stream().map(s -> s.replace("&", "§")).collect(Collectors.toList()));

        i.setItemMeta(itemMeta);
        return i;
    }

    public void openInventory() {
        player.playSound(player.getLocation(), Sound.CHEST_OPEN.getBukkitSound(), 1.0F, 1.0F);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String invName = e.getView().getTitle();
        if(!invName.equals(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.Title"))) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Arena arena = ArenaManager.getArenaOfPlayer(p);

        if(arena == null) return;

        if(e.getRawSlot() == 10 || e.getRawSlot() == 13 || e.getRawSlot() == 16) {
            p.closeInventory();

            if(arena.getPlayersSpeedVoted().contains(p.getUniqueId())) {
                p.sendMessage(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.Messages.AlreadyVoted"));
                return;
            }

            if(!p.hasPermission("epiceftb.vote.speed")) {
                p.sendMessage(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.Messages.MissingRank"));
                return;
            }

            arena.getPlayers().forEach(player1 -> player1.playSound(player1.getLocation(), Sound.NOTE_PLING.getBukkitSound(), 1.0F, 1.0F));
        }

        if(e.getRawSlot() == 10) {
            arena.addVoteSpeedCount(p, "NORMAL");
            arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.Messages.VotedForNormalSpeed"), Lists.newArrayList("%player%", "%votes%"), Lists.newArrayList(p.getName(), arena.getSpeedVotesCount("NORMAL"))));
        }

        if(e.getRawSlot() == 13) {
            arena.addVoteSpeedCount(p, "II");
            arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.Messages.VotedForIISpeed"), Lists.newArrayList("%player%", "%votes%"), Lists.newArrayList(p.getName(), arena.getSpeedVotesCount("II"))));
        }

        if(e.getRawSlot() == 16) {
            arena.addVoteSpeedCount(p, "III");
            arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Menu.SpeedVoteMenu.Messages.VotedForIIISpeed"), Lists.newArrayList("%player%", "%votes%"), Lists.newArrayList(p.getName(), arena.getSpeedVotesCount("III"))));
        }

    }

}
