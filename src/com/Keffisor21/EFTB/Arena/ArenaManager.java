package com.Keffisor21.EFTB.Arena;

import com.Keffisor21.EFTB.Configs.MapsConfig;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaManager {
    private static ConcurrentHashMap<String, Arena> arenas = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Player, String> playerMap = new ConcurrentHashMap<Player, String>(); // Cache

    @Nullable
    public static Arena getArena(String map) {
        if(!MapsConfig.existAsMap(map) || !MapsConfig.isValidMap(map)) return null;

        if(arenas.get(map) == null) arenas.put(map, new Arena(map));
        return arenas.get(map);
    }

    @Nullable
    public static Arena getArenaOfPlayer(Player player) {
        if(playerMap.get(player) == null) return null;
        return getArena(playerMap.get(player));
    }

    public static List<Arena> getAllArenasCached() {
        List<Arena> results = new ArrayList<Arena>();
        arenas.forEach((k, v) -> results.add(v));
        return results;
    }

    /**
     * Remove the arena only from cache, so all the variables are restarted
     * @param map
     */
    public static void removeArena(String map) {
        arenas.remove(map);
    }

}
