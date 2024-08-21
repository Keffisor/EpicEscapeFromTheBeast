package com.Keffisor21.EFTB.Inventories;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaManager;
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
        inv = Bukkit.createInventory(null, 27, "§7§lVote the time");

        if(player != null) initializeItems(player);
    }

    private void initializeItems(Player p) {
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if(arena == null) return;

        int day_count = arena.getTimeVotesCount("DAY");
        int midnight_count = arena.getTimeVotesCount("SUNRISE");
        int night_count = arena.getTimeVotesCount("NIGHT");

        List<String> day_desc = Lists.newArrayList("&7Vote per day", "&4" + day_count +" &4Votes");
        List<String> midnight_desc = Lists.newArrayList("&7Vote per sunrise", "&4" + midnight_count +" &4Votes");
        List<String> night_desc = Lists.newArrayList("&7Vote per night", "&4" + night_count +" &4Votes");

        inv.setItem(10, createGuiItem("&e&lDay", day_desc, new ItemStack(Material.STAINED_CLAY, day_count == 0 ? 1: day_count, (byte)4)));
        inv.setItem(13, createGuiItem("&4&lSunrise", midnight_desc, new ItemStack(Material.STAINED_CLAY, midnight_count == 0 ? 1: midnight_count, (byte)14)));
        inv.setItem(16, createGuiItem("&8&lNight", night_desc, new ItemStack(Material.STAINED_CLAY, night_count == 0 ? 1: night_count, (byte)15)));
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
        if (!invName.equals("§7§lVote the time")) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Arena arena = ArenaManager.getArenaOfPlayer(p);

        if(arena == null) return;

        if(e.getRawSlot() == 10 || e.getRawSlot() == 13 || e.getRawSlot() == 16) {
            p.closeInventory();

            if(arena.getPlayersVotedTime().contains(p.getUniqueId())) {
                p.sendMessage("§cYou have already voted!");
                return;
            }

            if(!p.hasPermission("epiceftb.vote.time")) {
                p.sendMessage("§cYou need a higher rank for do this!");
                return;
            }

            arena.getPlayers().forEach(player1 -> player1.playSound(player1.getLocation(), Sound.NOTE_PLING.getBukkitSound(), 1.0F, 1.0F));
        }

        if(e.getRawSlot() == 10) {
            arena.addVoteTimeCount(p, "DAY");
            arena.broadcastMessage("§6" + p.getName() + "§e has voted for §aDay! §e" + arena.getTimeVotesCount("DAY") + " §eVotes!");
        }

        if(e.getRawSlot() == 13) {
            arena.addVoteTimeCount(p, "SUNRISE");
            arena.broadcastMessage("§6" + p.getName() + "§e has voted for §aSunrise! §e" + arena.getTimeVotesCount("SUNRISE") + " §eVotes!");
        }

        if(e.getRawSlot() == 16) {
            arena.addVoteTimeCount(p, "NIGHT");
            arena.broadcastMessage("§6" + p.getName() + "§e has voted for §aNight! §e" + arena.getTimeVotesCount("NIGHT") + " §eVotes!");
        }

    }

}
