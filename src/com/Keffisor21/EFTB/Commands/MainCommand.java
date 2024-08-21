package com.Keffisor21.EFTB.Commands;

import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.Arena.ArenaState;
import com.Keffisor21.EFTB.Configs.GlobalConfig;
import com.Keffisor21.EFTB.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.Keffisor21.EFTB.EFTB;
import com.Keffisor21.EFTB.Configs.MapsConfig;
import com.Keffisor21.EFTB.Arena.Arena;

import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.GRAY + "EpicEscapeFromTheBeast");
            return false;
        }

        Player p = (Player)sender;

        if(args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "EpicEFTB by " + ChatColor.YELLOW + " Keffisor21");

            if(p.hasPermission("epiceftb.admin")) {
                sender.sendMessage(ChatColor.GREEN + "/eftb setmainlobby");
                sender.sendMessage(ChatColor.GREEN + "/eftb arena");
            }

            sender.sendMessage(ChatColor.GREEN + "/eftb join");
            sender.sendMessage(ChatColor.GREEN + "/eftb leave");

			return false;
        }

		if(args[0].equals("setmainlobby")) {
            if(!p.hasPermission("epiceftb.admin")) {
                p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                return false;
            }
            sender.sendMessage(ChatColor.GREEN + "You have set the main lobby correctly");

            EFTB.instance.getConfig().set("Lobby.world", p.getWorld().getName());
            EFTB.instance.getConfig().set("Lobby.x", p.getLocation().getX());
            EFTB.instance.getConfig().set("Lobby.y", p.getLocation().getY());
            EFTB.instance.getConfig().set("Lobby.z", p.getLocation().getZ());
            EFTB.instance.getConfig().set("Lobby.Pitch", p.getLocation().getPitch());

            EFTB.instance.saveConfig();
            EFTB.instance.reloadConfig();

			return false;
        }

		if(args[0].equals("debug")) {
            try {
                sender.sendMessage("You're on the map " + ArenaManager.getArenaOfPlayer(p).getMap());
                sender.sendMessage("The players on the map are " + ArenaManager.getArena(p.getWorld().getName()).getPlayers().toString());
            } catch (Exception e) {
            }
			return false;
		}

		if(args[0].equals("leave") || args[0].equals("quit")) {
            p.performCommand("leave");
			return false;
        }

		if(args[0].equals("join")) {
            if(args.length == 1) {
                List<String> maps = MapsConfig.getMapsList().stream().filter(MapsConfig::isValidMap).collect(Collectors.toList());
                if(maps.isEmpty()) {
                    p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.Join.NoMaps"), null, null));
                    return false;
                }

                HashMap<String, Integer> arenas = new HashMap<>();
                ArenaManager.getAllArenasCached().forEach(arena -> {
                    if(arena.isArenaFull() || arena.getPlayers().isEmpty()) return;
                    arenas.put(arena.getMap(), arena.getPlayers().size());
                });

                LinkedHashMap<String, Integer> arenasAvailable = Utils.sortByHighestInteger(arenas);

                if(!arenas.isEmpty()) { // If there is an arena with players, join to that arena instead of a random one
                    p.performCommand("eftb join " + arenasAvailable.keySet().toArray()[0]);
                    return false;
                }

                p.performCommand("eftb join " + maps.get(new Random().nextInt(maps.size())));
                return false;
            }

            if(args.length == 2) {
                String map = args[1];

                Arena arena = ArenaManager.getArena(map);

                if(!MapsConfig.existAsMap(map)) {
					sender.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.Join.MapNotFound"), null, null));
					return false;
				}

				if(arena == null) {
                    sender.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.Join.MapNotFullyConfig"), null, null));
					return false;
				}

                if(!arena.getArenaState().equals(ArenaState.WAITING) && !arena.getArenaState().equals(ArenaState.STARTING)) {
                    sender.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.Join.InGame"), null, null));
                    return false;
                }

                if(arena.getPlayers().size() >= arena.getMaxPlayers()) {
                    sender.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.Join.GameFull"), null, null));
                    return false;
                }

				if(ArenaManager.getArenaOfPlayer(p) != null) {
                    Utils.setVariables(GlobalConfig.getConfigString("Commands.Join.PlayerAlreadyInGame"), null, null);
                    return false;
				}

                arena.addPlayer(p);
				return false;
            }

        }

		if(args[0].equals("arena")) {
            if (args.length == 1) {
                //✔️,✕
                sender.sendMessage(ChatColor.GREEN + "/eftb arena create");
                sender.sendMessage(ChatColor.GREEN + "/eftb arena spawn add");
                sender.sendMessage(ChatColor.GREEN + "/eftb arena spawn remove");
                sender.sendMessage(ChatColor.GREEN + "/eftb arena setspect");
                sender.sendMessage(ChatColor.GREEN + "/eftb arena setmin <amount>");
                sender.sendMessage(ChatColor.GREEN + "/eftb arena setmax <amount>");
                sender.sendMessage(ChatColor.GREEN + "/eftb arena setbeast");
                sender.sendMessage(ChatColor.GREEN + "/eftb arena setwaitinglobby");

				return false;
            }

			if(args[1].equals("create")) {
                if(!p.hasPermission("epiceftb.admin")) {
                    p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                    return false;
                }

                String map = p.getWorld().getName();

                if(MapsConfig.existAsMap(map)) {
                    sender.sendMessage(ChatColor.RED + "This world is already registered");
                    return false;
                }

                try {
                    MapsConfig.load(p.getWorld().getName());
                    sender.sendMessage(ChatColor.GREEN + "Arena created correctly with the name of " + map);
                } catch (Exception e2) {
                    sender.sendMessage(ChatColor.RED + "In the creation of the arena named " + map + " has throw an error");
                    e2.printStackTrace();
                }

				return false;
            }

			if(args[1].equals("spawn")) {
                if (args.length == 2) {
                    sender.sendMessage(ChatColor.GREEN + "/eftb arena spawn add");
                    sender.sendMessage(ChatColor.GREEN + "/eftb arena spawn remove");

                    return false;
                }

                if (args.length == 3) {
                    if (args[2].equals("add")) {
                        if(!p.hasPermission("epiceftb.admin")) {
                            p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                            return false;
                        }

                        if (!MapsConfig.existAsMap(p.getWorld().getName())) {
                            sender.sendMessage(ChatColor.RED + "This world isn't a register map for set spawns");
                            return false;
                        }

                        int mapN = 0;
                        for (int i = 1; true; i++) {
                            FileConfiguration map = MapsConfig.getFileConfigurationOfMap(p.getWorld().getName());
                            if(map.get("Arena.Spawn." + i + ".X") == null) {
                                mapN = i;
                                break;
                            }
                        }

                        FileConfiguration map = MapsConfig.getFileConfigurationOfMap(p.getWorld().getName());
                        Location loc = p.getLocation().add(0.5D, 0.0D, 0.5D);

                        map.set("Arena.Spawn." + mapN + ".X", loc.getX());
                        map.set("Arena.Spawn." + mapN + ".Y", loc.getY());
                        map.set("Arena.Spawn." + mapN + ".Z", loc.getZ());
                        map.set("Arena.Spawn." + mapN + ".Pitch", loc.getPitch());

                        MapsConfig.saveMaps(MapsConfig.getFileOfMap(p.getWorld().getName()), map);
                        sender.sendMessage(ChatColor.GREEN + "You have add the spawn [" + mapN + "]");

                        return false;
                    }

                    if (args[2].equals("remove")) {
                        if(!p.hasPermission("epiceftb.admin")) {
                            p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                            return false;
                        }

                        if(!MapsConfig.existAsMap(p.getWorld().getName())) {
                            sender.sendMessage(ChatColor.RED + "This world isn't a register map");
                            return false;
                        }

                        int mapN = 0;
                        FileConfiguration map = MapsConfig.getFileConfigurationOfMap(p.getWorld().getName());

                        for (int i = 1; true; i++) {
                            if (map.get("Arena.Spawn." + i + ".X") != null) mapN++;
                            if (map.get("Arena.Spawn." + i + ".X") == null) break;
                        }

                        map.set("Arena.Spawn." + mapN + ".X", null);
                        map.set("Arena.Spawn." + mapN + ".Y", null);
                        map.set("Arena.Spawn." + mapN + ".Z", null);
                        map.set("Arena.Spawn." + mapN + ".Pitch", null);
                        map.set("Arena.Spawn." + mapN, null);

                        MapsConfig.saveMaps(MapsConfig.getFileOfMap(p.getWorld().getName()), map);
                        sender.sendMessage(ChatColor.RED + "You have remove the spawn [" + mapN + "]");

                        return false;
                    }
                }
            }

            if(args[1].equals("setmin")) {
                if(!p.hasPermission("epiceftb.admin")) {
                    p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                    return false;
                }

                if(!Utils.isValidInteger(args[2])) {
                    sender.sendMessage(ChatColor.RED + "Please write a valid number on the command");
                    return false;
                }

                if (!MapsConfig.existAsMap(p.getWorld().getName())) {
                    sender.sendMessage(ChatColor.RED + "This world isn't a register map");
                    return false;
                }

                if(args.length == 3) {
                    if(!Utils.isValidInteger(args[2])) {
                        sender.sendMessage(ChatColor.RED + "Please write a valid number on the command");
                        return false;
                    }

                    if(Integer.parseInt(args[2]) < 2) {
                        sender.sendMessage(ChatColor.RED + "You cannot set a maximum of players of a number less than two");
                        return false;
                    }

                    FileConfiguration map = MapsConfig.getFileConfigurationOfMap(p.getWorld().getName());
                    map.set("Arena.Minimum", Integer.parseInt(args[2]));
                    sender.sendMessage(ChatColor.GREEN + "You have set the minimum of players of " + args[2]);
                    MapsConfig.saveMaps(MapsConfig.getFileOfMap(p.getWorld().getName()), map);

                    return false;
                }

                sender.sendMessage(ChatColor.RED + "Usage: /eftb arena setmin <amount>");
                return false;
            }

            if(args[1].equals("setmax")) {
                if(!p.hasPermission("epiceftb.admin")) {
                    p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                    return false;
                }

                if(!Utils.isValidInteger(args[2])) {
                    sender.sendMessage(ChatColor.RED + "Please write a valid number on the command");
                    return false;
                }

                if (!MapsConfig.existAsMap(p.getWorld().getName())) {
                    sender.sendMessage(ChatColor.RED + "This world isn't a register map");
                    return false;
                }

                if(args.length == 3) {
                    if(!Utils.isValidInteger(args[2])) {
                        sender.sendMessage(ChatColor.RED + "Please write a valid number on the command");
                        return false;
                    }

                    if(Integer.parseInt(args[2]) < 2) {
                        sender.sendMessage(ChatColor.RED + "You cannot set a maximum of players of a number less than two");
                        return false;
                    }

                    FileConfiguration map = MapsConfig.getFileConfigurationOfMap(p.getWorld().getName());
                    map.set("Arena.Maximum", Integer.parseInt(args[2]));
                    sender.sendMessage(ChatColor.GREEN + "You have set the maximum of players of " + args[2]);
                    MapsConfig.saveMaps(MapsConfig.getFileOfMap(p.getWorld().getName()), map);

                    return false;
                }

                sender.sendMessage(ChatColor.RED + "Usage: /eftb arena setmax <amount>");
                return false;
            }

            if (args[1].equals("setspect")) {
                if(!p.hasPermission("epiceftb.admin")) {
                    p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                    return false;
                }

                if (!MapsConfig.existAsMap(p.getWorld().getName())) {
                    sender.sendMessage(ChatColor.RED + "This world isn't a register map");
                    return false;
                }

                FileConfiguration map = MapsConfig.getFileConfigurationOfMap(p.getWorld().getName());

                map.set("Arena.Spectator.X", p.getLocation().getX());
                map.set("Arena.Spectator.Y", p.getLocation().getY());
                map.set("Arena.Spectator.Z", p.getLocation().getZ());
                map.set("Arena.Spectator.Pitch", p.getLocation().getPitch());

                sender.sendMessage(ChatColor.GREEN + "You have set the location of the spectator's spawn ");
                MapsConfig.saveMaps(MapsConfig.getFileOfMap(p.getWorld().getName()), map);

                return false;
            }

            if(args[1].equals("setwaitinglobby")) {
                if(!p.hasPermission("epiceftb.admin")) {
                    p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                    return false;
                }

                if (!MapsConfig.existAsMap(p.getWorld().getName())) {
                    sender.sendMessage(ChatColor.RED + "This world isn't a register map");
                    return false;
                }

                FileConfiguration map = MapsConfig.getFileConfigurationOfMap(p.getWorld().getName());

                map.set("Arena.WaitingLobby.X", p.getLocation().getX());
                map.set("Arena.WaitingLobby.Y", p.getLocation().getY());
                map.set("Arena.WaitingLobby.Z", p.getLocation().getZ());
                map.set("Arena.WaitingLobby.Pitch", p.getLocation().getPitch());

                sender.sendMessage(ChatColor.GREEN + "You have set the location of the waiting lobby spawn.");
                MapsConfig.saveMaps(MapsConfig.getFileOfMap(p.getWorld().getName()), map);
                return false;
            }

            if(args[1].equals("setbeast")) {
                if(!p.hasPermission("epiceftb.admin")) {
                    p.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Commands.NoPermission"), null, null));
                    return false;
                }

                FileConfiguration map = MapsConfig.getFileConfigurationOfMap(p.getWorld().getName());

                map.set("Arena.Beast.X", p.getLocation().getX());
                map.set("Arena.Beast.Y", p.getLocation().getY());
                map.set("Arena.Beast.Z", p.getLocation().getZ());
                map.set("Arena.Beast.Pitch", p.getLocation().getPitch());

                sender.sendMessage(ChatColor.GREEN + "You have set the location of the beast's spawn ");
                MapsConfig.saveMaps(MapsConfig.getFileOfMap(p.getWorld().getName()), map);
                return false;
            }

            sender.sendMessage(ChatColor.RED + "That subcommand doesn't exist");
        }

        return false;
    }

}
