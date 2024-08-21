package com.Keffisor21.EFTB.Events;

import com.Keffisor21.EFTB.EFTB;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.Keffisor21.EFTB.LobbyManager.Spawn;

public class Events implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        EFTB.players.add(e.getPlayer());
        Spawn.send(e.getPlayer());

        Bukkit.getScheduler().scheduleSyncDelayedTask(EFTB.instance, new Runnable() {
            public void run() {
                if (e.getPlayer() == null) {
                    return;
                }
                Spawn.send(e.getPlayer());
            }
        }, 8L);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        EFTB.players.remove(e.getPlayer());
    }

}