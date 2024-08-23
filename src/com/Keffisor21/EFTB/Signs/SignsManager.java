package com.Keffisor21.EFTB.Signs;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.Configs.GlobalConfig;
import com.Keffisor21.EFTB.EFTB;
import com.Keffisor21.EFTB.Utils.Task;
import com.Keffisor21.EFTB.Utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SignsManager {

    public static boolean registerSign(String map, Location loc) {
        generateConfigFile();

        FileConfiguration signsConfig = getConfigFile();

        int signIdxDuplicate = getIdxOfSign(loc);
        if(signIdxDuplicate != -1) return true;

        int signIdx;
        for(signIdx = 1; true; signIdx++) {
            if(signsConfig.get("Signs." + signIdx + ".X") != null) continue;
            if(signIdx > 1000) {
                Bukkit.getLogger().info("[EFTB] Error registering the sign, please check the signs.yml file");
                return false;
            }
            break;
        }

        signsConfig.set("Signs." + signIdx + ".World", loc.getWorld().getName());
        signsConfig.set("Signs." + signIdx + ".X", loc.getX());
        signsConfig.set("Signs." + signIdx + ".Y", loc.getY());
        signsConfig.set("Signs." + signIdx + ".Z", loc.getZ());
        signsConfig.set("Signs." + signIdx + ".Map", map);

        saveSignsConfig(signsConfig);
        return true;
    }

    public static void removeSign(Location loc) {
        FileConfiguration signsConfig = getConfigFile();

       int signIdx = getIdxOfSign(loc);
       if(signIdx == -1) return;

        signsConfig.set("Signs." + signIdx, null);
        saveSignsConfig(signsConfig);
    }

    public static void loadAllSigns() {
        if(!getFile().exists()) return;
        FileConfiguration signsConfig = getConfigFile();
        if(signsConfig.get("Signs") == null) return;

        Map<String, Object> signs = ((MemorySection) signsConfig.get("Signs")).getValues(false);

        getSignsIndexes().forEach(signIdx -> {
            World world = Bukkit.getWorld(signsConfig.getString("Signs." + signIdx + ".World"));
            if(world == null) return;

            double x = signsConfig.getDouble("Signs." + signIdx + ".X");
            double y = signsConfig.getDouble("Signs." + signIdx + ".Y");
            double z = signsConfig.getDouble("Signs." + signIdx + ".Z");

            Location loc = new Location(world, x, y , z);
            String map = signsConfig.getString("Signs." + signIdx + ".Map");

            updateSign(loc, map);
        });
    }

    public static String getMapOfSign(Location loc) {
        int signIdx = getIdxOfSign(loc);
        if(signIdx == -1) return null;
        return getConfigFile().getString("Signs." + signIdx + ".Map");
    }

    public static int getIdxOfSign(Location loc) {
        FileConfiguration signsConfig = getConfigFile();

        int signIdx = -1;
        for(String signIndex : getSignsIndexes()) {
            if(signsConfig.getDouble("Signs." + signIndex + ".X") != loc.getX() || signsConfig.getDouble("Signs." + signIndex + ".Y") != loc.getY() || signsConfig.getDouble("Signs." + signIndex + ".Z") != loc.getZ()) continue;
            signIdx = Integer.parseInt(signIndex);
            break;
        }

        return signIdx;
    }

    public static void updateSign(Location loc, String map) {
        new Task(20L, 20L) {
            @Override
            public void toExecute() {
                Arena arena = ArenaManager.getArena(map);

                if(arena == null) return; // Maybe it's changing sm rq? Freeze until it's fixed

                if(!loc.getBlock().getType().equals(Material.WALL_SIGN)) {
                    removeSign(loc);
                    this.runnable.cancel();
                    return;
                }

                Sign sign = (Sign) loc.getBlock().getState();
                Block glassBlock = Stream.of(
                        loc.clone().add(1, 0, 0),
                        loc.clone().add( -1, 0, 0),
                        loc.clone().add(0, 0, 1),
                        loc.clone().add(0, 0, -1)
                ).filter(locCheck -> {
                    Block block = locCheck.getWorld().getBlockAt(locCheck);
                    return block.getType().equals(Material.STAINED_GLASS);
                }).map(location -> location.getWorld().getBlockAt(location)).findFirst().orElse(null);

                sign.update();

                sign.setLine(0, GlobalConfig.getConfigString("Signs.Lines.First"));

                switch(arena.getArenaState()) {
                    case WAITING: {
                        sign.setLine(1, Utils.setVariables(GlobalConfig.getConfigString("Signs.Lines.Second"), Lists.newArrayList("%status%"), Lists.newArrayList(GlobalConfig.getConfigString("Signs.Status.Waiting"))));
                        if(glassBlock != null) glassBlock.setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte) 5, true);
                        break;
                    }
                    case STARTING: {
                        sign.setLine(1, Utils.setVariables(GlobalConfig.getConfigString("Signs.Lines.Second"), Lists.newArrayList("%status%"), Lists.newArrayList(GlobalConfig.getConfigString("Signs.Status.Starting"))));
                        if(glassBlock != null) glassBlock.setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte) 4, true);
                        break;
                    }
                    case IN_GAME: {
                        sign.setLine(1, Utils.setVariables(GlobalConfig.getConfigString("Signs.Lines.Second"), Lists.newArrayList("%status%"), Lists.newArrayList(GlobalConfig.getConfigString("Signs.Status.InGame"))));
                        if(glassBlock != null) glassBlock.setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte) 14, true);
                        break;
                    }
                    case RESTARTING: {
                        sign.setLine(1, Utils.setVariables(GlobalConfig.getConfigString("Signs.Lines.Second"), Lists.newArrayList("%status%"), Lists.newArrayList(GlobalConfig.getConfigString("Signs.Status.Restarting"))));
                        if(glassBlock != null) glassBlock.setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte) 3, true);
                        break;
                    }
                }

                if(glassBlock != null) glassBlock.getState().update(true);

                sign.setLine(2, Utils.setVariables(GlobalConfig.getConfigString("Signs.Lines.Third"), Lists.newArrayList("%map%"), Lists.newArrayList(map)));
                sign.setLine(3, Utils.setVariables(GlobalConfig.getConfigString("Signs.Lines.Fourth"), Lists.newArrayList("%players_count%", "%players_max_count%"), Lists.newArrayList(arena.getPlayers().size(), arena.getMaxPlayers())));

                sign.update(true);
            }
        }.run();
    }

    private static void generateConfigFile() {
        File signsFile = getFile();

        if(signsFile.exists()) return;

        FileConfiguration signsConfig = YamlConfiguration.loadConfiguration(signsFile);
        YamlConfiguration.loadConfiguration(signsFile);

        saveSignsConfig(signsConfig);

        try {
            if(!signsFile.exists()) {
                new File(EFTB.instance.getDataFolder(), "/signs.yml").createNewFile();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File getFile() {
        return new File(EFTB.instance.getDataFolder(), "/signs.yml");
    }

    private static FileConfiguration getConfigFile() {
        return YamlConfiguration.loadConfiguration(getFile());
    }

    private static List<String> getSignsIndexes() {
        if(!getFile().exists()) return Lists.newArrayList();
        FileConfiguration signsConfig = getConfigFile();
        if(signsConfig.get("Signs") == null) return Lists.newArrayList();

        Map<String, Object> signs = ((MemorySection) signsConfig.get("Signs")).getValues(false);
        return Arrays.stream(signs.keySet().toArray()).map(Object::toString).collect(Collectors.toList());
    }

    private static void saveSignsConfig(FileConfiguration signsConfig) {
        try {
            signsConfig.save(getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
