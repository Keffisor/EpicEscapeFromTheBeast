package com.Keffisor21.EFTB.Signs;

import com.Keffisor21.EFTB.Configs.MapsConfig;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.stream.Stream;

public class SignsEvents implements Listener {

    @EventHandler
    public void onSignWrite(SignChangeEvent e) {
        Player p = e.getPlayer();

        if (e.getLine(0).equalsIgnoreCase("[EFTB]") && p.hasPermission("epiceftb.admin")) {
            String map =  e.getLine(1);

            if(!MapsConfig.existAsMap(map) || !MapsConfig.isValidMap(map)) {
                p.sendMessage(ChatColor.RED + "The map " + map + " was not found or it's not configurated correctly.");
                return;
            }

            Location loc = e.getBlock().getLocation();

            if(!SignsManager.registerSign(map, loc)) {
                p.sendMessage(ChatColor.RED  + "The sign registration has failed, check your signs.yml file");
                return;
            }

            p.sendMessage(ChatColor.GREEN + "The sign was added to " + map);

            SignsManager.updateSign(loc, map);

            Stream.of(
                    loc.clone().add(1, 0, 0),
                    loc.clone().add( -1, 0, 0),
                    loc.clone().add(0, 0, 1),
                    loc.clone().add(0, 0, -1)
            ).forEach(locCheck -> {
                Block block = locCheck.getWorld().getBlockAt(locCheck);
                if(block.getType().equals(Material.AIR) || block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.BARRIER)) return;

                block.setType(Material.STAINED_GLASS);
                block.setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte) 5, true);
            });

        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null || !e.getClickedBlock().getType().equals(Material.WALL_SIGN)) return;

        Player p = e.getPlayer();

        Sign sign = (Sign) e.getClickedBlock().getState();

        String map = SignsManager.getMapOfSign(e.getClickedBlock().getLocation());
        if(map == null) return;

        if(e.getAction().equals(Action.LEFT_CLICK_BLOCK) && p.hasPermission("epiceftb.admin") && p.getGameMode().equals(GameMode.CREATIVE)) {
            p.sendMessage(ChatColor.RED + "The sign was removed");
            return;
        }

        e.setCancelled(true);

        p.performCommand("eftb join " + map);
    }

}
