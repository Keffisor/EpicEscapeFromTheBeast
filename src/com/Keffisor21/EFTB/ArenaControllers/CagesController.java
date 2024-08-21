package com.Keffisor21.EFTB.ArenaControllers;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.Keffisor21.EFTB.EFTB;
import com.Keffisor21.EFTB.Configs.MapsConfig;

public class CagesController {
    private final String map;
    private final ConcurrentHashMap<Integer, Boolean> cage = new ConcurrentHashMap<Integer, Boolean>();

    public CagesController(String map) {
        this.map = map;
    }

    public void teleportCage(Player p) {
        if(p == null) return;

        int maxPlayers = MapsConfig.getMaximumPlayers(map);

        for(int i2 = 1; i2 <= maxPlayers; i2++) {
            cage.putIfAbsent(i2, false);
        }

        for(int i = 1; i <= maxPlayers; i++) {
            if(cage.get(i)) continue;

            cage.put(i, true);

            forceTeleportCage(p, i);
            i = maxPlayers;
        }
    }

    public void forceTeleportCage(Player p, int i) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(EFTB.instance, new Runnable() {
            @Override
            public void run() {
                if(p == null) return;

                FileConfiguration config = MapsConfig.getFileConfigurationOfMap(map);
                double x = config.getDouble("Arena.Spawn." + i + ".X");
                double y = config.getDouble("Arena.Spawn." + i + ".Y") + 1;
                double z = config.getDouble("Arena.Spawn." + i + ".Z");
                float pitch = (float) config.getDouble("Arena.Spawn." + i + ".Pitch");
                World w = Bukkit.getWorld(map);

                if (w == null) {
                    Bukkit.getServer().getLogger().info("Invalid world");
                    return;
                }

                Location loc = new Location(w, x, y, z);
                loc.setPitch(pitch);

                p.teleport(loc);
            }
        }, 5L);
    }

    public void teleportBeastCage(Player p) {
        if(p == null) return;

        FileConfiguration config = MapsConfig.getFileConfigurationOfMap(map);

        double x = config.getDouble("Arena.Beast.X");
        double y = config.getDouble("Arena.Beast.Y") + 1;
        double z = config.getDouble("Arena.Beast.Z");
        float pitch = (float) config.getDouble("Arena.Beast.Pitch");

        World w = Bukkit.getWorld(map);

        if (w == null) {
            Bukkit.getServer().getLogger().info("Invalid world");
            return;
        }

        Location loc = new Location(w, x, y, z);
        loc.setPitch(pitch);

        p.teleport(loc);
    }

}
