package com.Keffisor21.EFTB.Configs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.Keffisor21.EFTB.EFTB;

import net.md_5.bungee.api.ChatColor;

public class MapsConfig {

    public static void load(String name) throws IOException {
        System.out.println(EFTB.instance.getDataFolder().toString());

        if (!new File(EFTB.instance.getDataFolder(), "maps").exists()) {
            new File(EFTB.instance.getDataFolder(), "maps").mkdir();
        }

        File MapsFile = new File(EFTB.instance.getDataFolder(), "/maps/" + name + ".yml");
        FileConfiguration Maps = YamlConfiguration.loadConfiguration(MapsFile);

        if (!new File(EFTB.instance.getDataFolder(), "/maps/" + name + ".yml").exists()) {
            //EFTB.instance.saveResource("/plugins/EpicEscapeFromTheBeast/maps/"+name+".yml", true);
        }

        YamlConfiguration.loadConfiguration(MapsFile);
        saveMaps(MapsFile, Maps);

        if (!MapsFile.exists()) {
            new File(EFTB.instance.getDataFolder(), "/maps/" + name + ".yml").createNewFile();
        }
    }

    public static void saveMaps(File MapsFile, FileConfiguration Maps) {
        try {
            Maps.save(MapsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getFileConfigurationOfMap(String map) {
        File MapsFile = new File(EFTB.instance.getDataFolder(), "/maps/" + map + ".yml");
        FileConfiguration Maps = YamlConfiguration.loadConfiguration(MapsFile);
        return Maps;
    }

    public static File getFileOfMap(String map) {
        File MapsFile = new File(EFTB.instance.getDataFolder(), "/maps/" + map + ".yml");
        return MapsFile;
    }

    public static boolean existAsMap(String map) {
        File MapsFile = new File(EFTB.instance.getDataFolder(), "/maps/" + map + ".yml");
        return MapsFile.exists();
    }

    public static boolean isValidMap(String map) {
        FileConfiguration config = MapsConfig.getFileConfigurationOfMap(map);
        return (config.get("Arena.Spawn.1.X") != null
                && config.get("Arena.Spawn.2.X") != null
                && config.get("Arena.Minimum") != null
                && config.get("Arena.Maximum") != null
                && config.get("Arena.Spectator.X") != null
                && config.get("Arena.Beast.X") != null
                && config.get("Arena.WaitingLobby.X") != null
        );
    }

    public static List<String> getMapsList() {
        File file = new File(EFTB.instance.getDataFolder(), "/maps/");
        return Stream.of(file.listFiles()).map(s -> s.getName().replace(".yml", "")).collect(Collectors.toList());
    }

    public static int getMaximumPlayers(String map) {
        FileConfiguration config = MapsConfig.getFileConfigurationOfMap(map);
        return config.getInt("Arena.Maximum");
    }

    public static int getMinPlayers(String map) {
        FileConfiguration config = MapsConfig.getFileConfigurationOfMap(map);
        return config.getInt("Arena.Minimum");
    }
}