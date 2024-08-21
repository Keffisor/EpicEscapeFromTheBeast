package com.Keffisor21.EFTB;

import java.util.List;

import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.Events.Events;
import com.Keffisor21.EFTB.Inventories.VoteMenu;
import com.Keffisor21.EFTB.Inventories.VoteSpeedMenu;
import com.Keffisor21.EFTB.Inventories.VoteTimeMenu;
import com.Keffisor21.EFTB.Signs.SignsEvents;
import com.Keffisor21.EFTB.Signs.SignsManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.Keffisor21.EFTB.Commands.ArenaLeaveCommand;
import com.Keffisor21.EFTB.Commands.MainCommand;
import com.Keffisor21.EFTB.Events.ArenaEvents;
import com.google.common.collect.Lists;

public class EFTB extends JavaPlugin implements Listener {
    public static List<Player> players = Lists.newArrayList();
    public static EFTB instance;

    @Override
    public void onEnable() {
        instance = this;

        registerEvents();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        getCommand("eftb").setExecutor(new MainCommand());
        getCommand("leave").setExecutor(new ArenaLeaveCommand());

        SignsManager.loadAllSigns();
    }

    @Override
    public void onDisable() {
        ArenaManager.getAllArenasCached().forEach(arena -> arena.getPlayers().forEach(player -> {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.setGameMode(GameMode.SURVIVAL);
        }));
    }

    public void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new Events(), this);
        pm.registerEvents(new ArenaEvents(), this);

        pm.registerEvents(new VoteMenu(null), this);
        pm.registerEvents(new VoteTimeMenu(null), this);
        pm.registerEvents(new VoteSpeedMenu(null), this);

        pm.registerEvents(new SignsEvents(), this);
    }

}
