package com.Keffisor21.EFTB.Events;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.Arena.ArenaState;
import com.Keffisor21.EFTB.Inventories.LootArenaMenu;
import com.Keffisor21.EFTB.LobbyManager.Spawn;
import com.Keffisor21.EFTB.Nms.Sound;
import com.Keffisor21.EFTB.Utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.List;
import java.util.Random;

public class ArenaEvents implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Arena arena = ArenaManager.getArenaOfPlayer(e.getPlayer());

        if(arena == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Arena arena = ArenaManager.getArenaOfPlayer(e.getPlayer());

        if(arena == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onQuitGame(PlayerQuitEvent e) {
        Arena arena = ArenaManager.getArenaOfPlayer(e.getPlayer());

        if(arena == null) return;

        arena.removePlayer(e.getPlayer(), false);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        Arena arena = ArenaManager.getArenaOfPlayer(p);

        if (arena == null) return;

        p.spigot().respawn();

        e.setKeepInventory(true);

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        if(p.getKiller() != null && p.getKiller() instanceof Player) {
            e.setDeathMessage(null);
            arena.getController().playerKilled(p, p.getKiller());
            return;
        }

        e.setDeathMessage(null);
        arena.getController().playerDied(p);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if(!(e.getEntity() instanceof  Player)) return;

        Arena arena = ArenaManager.getArenaOfPlayer((Player) e.getEntity());

        if(arena == null) return;

        e.setCancelled(true);
    }

    /*@EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Arena arena = ArenaManager.getArenaOfPlayer((Player) event.getEntity());

        if(arena == null) return;

        if(event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }*/

    @EventHandler
    public void onDropItems(PlayerDropItemEvent event) {
        Arena arena = ArenaManager.getArenaOfPlayer(event.getPlayer());

        if(arena == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityShootBowEvent(EntityShootBowEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player)e.getEntity();

            Arena arena = ArenaManager.getArenaOfPlayer(p);
            if(arena == null) return;

            e.getProjectile().setCustomName(p.getName());
        }
    }

    @EventHandler
    public void onDeath(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();

        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if(arena == null) return;

        if((p.getHealth() - e.getDamage()) <= 0.0) { // Instead of death, if the damage will kill the player, just cancel the event and do the kill stuff so it's smoother
            e.setCancelled(true);

            p.setHealth(20);
            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);

            Entity en = e.getDamager();

            if(en instanceof Player) {
                Player killer = (Player)e.getDamager();
                arena.getController().playerKilled(p, killer);
                return;
            }

            if(en instanceof Arrow) {
                Arrow arrow = (Arrow)en;
                arena.getController().playerKilled(p, arrow.getCustomName());
                return;
            }

            arena.getController().playerDied(p);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if(arena == null) return;

        if(damager.getPlayer() != arena.getBeast() && p != arena.getBeast() || arena.getArenaState().equals(ArenaState.RESTARTING))
            e.setCancelled(true);
    }

    @EventHandler
    public void onOpenChest(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null || !e.getClickedBlock().getType().equals(Material.CHEST)) return;

        Player p = e.getPlayer();

        Arena arena = ArenaManager.getArena(e.getClickedBlock().getWorld().getName());
        if(arena == null) return;

        e.setCancelled(true);

        if(!arena.getArenaState().equals(ArenaState.IN_GAME)) {
            return;
        }

        new LootArenaMenu(p).openInventory();
        p.playSound(p.getLocation(), Sound.CHEST_OPEN.getBukkitSound(), 1.0F, 1.0F);
    }

    @EventHandler
    public void onButtonClick(PlayerInteractEvent e) {
        // When a player clicks a button in the map will teleport it to the start of the arena
        List<Material> allowed = Lists.newArrayList(Utils.getMaterial("WOOD_BUTTON", "OAK_BUTTON"), Material.STONE_BUTTON);
        if(e.getClickedBlock() == null || !allowed.contains(e.getClickedBlock().getType())) return;

        Player p = e.getPlayer();

        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if(arena == null || !arena.getArenaState().equals(ArenaState.IN_GAME)) return;

        arena.getCagesController().forceTeleportCage(p, new Random().nextInt(arena.getMaxPlayers()) + 1);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();

        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if(arena == null || arena.getArenaState().equals(ArenaState.IN_GAME)) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onVoidEnter(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if(arena == null || arena.getArenaState().equals(ArenaState.IN_GAME)) return;

        if((e.getTo().getBlockY() < -1) && (!e.getPlayer().isDead())) {
            Spawn.sendWaitingLobby(p, arena.getMap());
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        Arena arena = ArenaManager.getArena(e.getWorld().getName());

        if(arena == null || !e.toWeatherState()) return;

        e.setCancelled(true);
    }

}
