package com.Keffisor21.EFTB.LobbyManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.Keffisor21.EFTB.EFTB;
import com.Keffisor21.EFTB.Configs.MapsConfig;

public class Spawn {

    public static void send(Player p) {
        FileConfiguration config = EFTB.instance.getConfig();

        String world = config.getString("Lobby.world");
        double x = config.getDouble("Lobby.x");
        double y = config.getDouble("Lobby.y");
        double z = config.getDouble("Lobby.z");
        float pitch = (float) config.getDouble("Lobby.Pitch");

        World w = Bukkit.getWorld(world);

        if (w == null) {
            Bukkit.getServer().getLogger().info("Please set the spawn lobby");
            return;
        }

        Location loc = new Location(w, x, y, z);
        loc.setPitch(pitch);
        p.teleport(loc);
    }

    public static void sendWaitingLobby(Player p, String map) {
        if(p == null) return;

        FileConfiguration config = MapsConfig.getFileConfigurationOfMap(map);

        double x = config.getDouble("Arena.WaitingLobby.X");
        double y = config.getDouble("Arena.WaitingLobby.Y");
        double z = config.getDouble("Arena.WaitingLobby.Z");
        float pitch = (float) config.getDouble("Arena.WaitingLobby.Pitch");

        World w = Bukkit.getWorld(map);

        if(w == null) {
            Bukkit.getServer().getLogger().info("Invalid world");
            return;
        }

        Location loc = new Location(w, x, y, z);
        loc.setPitch(pitch);
        p.teleport(loc);
    }
}
