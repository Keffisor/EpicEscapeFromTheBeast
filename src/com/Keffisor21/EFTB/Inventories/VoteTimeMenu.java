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

public class VoteTimeMenu implements Listener {
    private final Inventory inv;
    private final Player player;

    public VoteTimeMenu(Player player) {
        this.player = player;
        inv = Bukkit.createInventory(null, 27, GlobalConfig.getConfigString("Menu.TimeVoteMenu.Title"));

        if(player != null) initializeItems(player);
    }

    private void initializeItems(Player p) {
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if(arena == null) return;

        int day_count = arena.getTimeVotesCount("DAY");
        int midnight_count = arena.getTimeVotesCount("SUNRISE");
        int night_count = arena.getTimeVotesCount("NIGHT");

        List<String> day_desc = Utils.setVariables(GlobalConfig.getConfigList("Menu.TimeVoteMenu.ItemTimeDayLore"), Lists.newArrayList("%votes%"), Lists.newArrayList(day_count));
        List<String> midnight_desc = Utils.setVariables(GlobalConfig.getConfigList("Menu.TimeVoteMenu.ItemTimeSunriseLore"), Lists.newArrayList("%votes%"), Lists.newArrayList(midnight_count));
        List<String> night_desc = Utils.setVariables(GlobalConfig.getConfigList("Menu.TimeVoteMenu.ItemTimeNightLore"), Lists.newArrayList("%votes%"), Lists.newArrayList(night_count));

        inv.setItem(10, createGuiItem(GlobalConfig.getConfigString("Menu.TimeVoteMenu.ItemTimeDayName"), day_desc, new ItemStack(Material.STAINED_CLAY, day_count == 0 ? 1: day_count, (byte)4)));
        inv.setItem(13, createGuiItem(GlobalConfig.getConfigString("Menu.TimeVoteMenu.ItemTimeSunriseName"), midnight_desc, new ItemStack(Material.STAINED_CLAY, midnight_count == 0 ? 1: midnight_count, (byte)14)));
        inv.setItem(16, createGuiItem(GlobalConfig.getConfigString("Menu.TimeVoteMenu.ItemTimeNightName"), night_desc, new ItemStack(Material.STAINED_CLAY, night_count == 0 ? 1: night_count, (byte)15)));
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
        if (!invName.equals(GlobalConfig.getConfigString("Menu.TimeVoteMenu.Title"))) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Arena arena = ArenaManager.getArenaOfPlayer(p);

        if(arena == null) return;

        if(e.getRawSlot() == 10 || e.getRawSlot() == 13 || e.getRawSlot() == 16) {
            p.closeInventory();

            if(arena.getPlayersVotedTime().contains(p.getUniqueId())) {
                p.sendMessage(GlobalConfig.getConfigString("Menu.TimeVoteMenu.Messages.AlreadyVoted"));
                return;
            }

            if(!p.hasPermission("epiceftb.vote.time")) {
                p.sendMessage(GlobalConfig.getConfigString("Menu.TimeVoteMenu.Messages.MissingRank"));
                return;
            }

            arena.getPlayers().forEach(player1 -> player1.playSound(player1.getLocation(), Sound.NOTE_PLING.getBukkitSound(), 1.0F, 1.0F));
        }

        if(e.getRawSlot() == 10) {
            arena.addVoteTimeCount(p, "DAY");
            arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Menu.TimeVoteMenu.Messages.VotedForDay"), Lists.newArrayList("%player%", "%votes%"), Lists.newArrayList(p.getName(), arena.getTimeVotesCount("DAY"))));
        }

        if(e.getRawSlot() == 13) {
            arena.addVoteTimeCount(p, "SUNRISE");
            arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Menu.TimeVoteMenu.Messages.VotedForSunrise"), Lists.newArrayList("%player%", "%votes%"), Lists.newArrayList(p.getName(), arena.getTimeVotesCount("SUNRISE"))));
        }

        if(e.getRawSlot() == 16) {
            arena.addVoteTimeCount(p, "NIGHT");
            arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Menu.TimeVoteMenu.Messages.VotedForNight"), Lists.newArrayList("%player%", "%votes%"), Lists.newArrayList(p.getName(), arena.getTimeVotesCount("NIGHT"))));
        }

    }

}
