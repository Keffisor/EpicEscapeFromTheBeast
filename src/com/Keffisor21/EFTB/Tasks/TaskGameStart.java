package com.Keffisor21.EFTB.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.Keffisor21.EFTB.Configs.GlobalConfig;
import com.Keffisor21.EFTB.Nms.Sound;
import com.Keffisor21.EFTB.Utils.Task;
import org.bukkit.entity.Player;

import com.Keffisor21.EFTB.Utils.Utils;
import com.Keffisor21.EFTB.ArenaControllers.CagesController;
import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaState;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TaskGameStart extends Task {
    public static ConcurrentHashMap<String, Boolean> started = new ConcurrentHashMap<>();
    private int timer = GlobalConfig.getTimingWaitingStart() + 1;
    private final Arena arena;

    public TaskGameStart(Arena arena) {
        super(0L, 20L);

        this.arena = arena;
    }

    @Override
    protected void toExecute() {
        List<Player> players = arena.getPlayers();
        int size = players.size();

        if(size == 0) {
            this.runnable.cancel();
            arena.setArenaState(ArenaState.WAITING);
            started.remove(arena.getMap());
        }

        if(arena.getMinPlayers() <= size) {
            timer--;

            for(Player p : arena.getPlayers()) {
                p.setLevel(timer);
            }

            if(players.size() >= arena.getMaxPlayers() && !arena.getArenaState().equals(ArenaState.STARTING)) {
                if(timer > GlobalConfig.getTimingWaitingFull()) timer = GlobalConfig.getTimingWaitingFull();

                arena.broadcastMessage("§9Server full, starting game...");
                arena.setArenaState(ArenaState.STARTING);
                started.put(arena.getMap(), true);
            }

            if(timer < GlobalConfig.getTimingWaitingFull() && !arena.getArenaState().equals(ArenaState.STARTING)) {
                arena.setArenaState(ArenaState.STARTING);
                started.put(arena.getMap(), true);
            }

			for(int i = 3; i != 0; i--) {
                if(i != timer) continue;

				arena.broadcastMessage("§9The game will start in " + i + " seconds");

                for(Player p : arena.getPlayers()) {
                    p.playSound(p.getLocation(), Sound.CLICK.getBukkitSound(), 1.0F, 1.0F);
				}
			}

            if(timer == 0) {
                String highestTimeVote = arena.getHighestTimeVote();
                String highestSpeedVote = arena.getHighestSpeedVote();

                switch(highestTimeVote) {
                    case "NIGHT": {
                        arena.getWorld().setTime(17000);
                        break;
                    }
                    case "SUNRISE": {
                        arena.getWorld().setTime(13500);
                        break;
                    }
                    default: arena.getWorld().setTime(1000);
                }
                
                switch(highestSpeedVote) {
                    case "II": {
                        arena.getPlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20000000, 1)));
                        break;
                    }
                    case "III": {
                        arena.getPlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20000000, 2)));
                    }
                }

                arena.getWorld().setGameRuleValue("doDaylightCycle", "false");
                arena.getWorld().setGameRuleValue("doMobSpawning", "false");

                arena.broadcastMessage("§9Gooooo!!!");
                arena.broadcastMessage("§6The time of §a" + highestTimeVote + " §6has been selected for this game.");
                arena.broadcastMessage("§6The speed §a" + highestSpeedVote + " §6has been selected for this game.");

                CagesController c = arena.getCagesController();

                arena.getPlayers().forEach(p -> p.getInventory().clear());

                List<Player> playersWB = new ArrayList<Player>(players);
                Player beast = Utils.getRandomPlayer(players);
                playersWB.remove(beast);

                arena.setBeast(beast);

                for(Player p : playersWB) {
                    c.teleportCage(p);
                    p.playSound(p.getLocation(), Sound.NOTE_PLING.getBukkitSound(), 1.0F, 1.0F);
                }

                beast.playSound(beast.getLocation(), Sound.ENDERDRAGON_GROWL.getBukkitSound(), 1.0F, 1.0F);

                beast.sendTitle("", "§eYou're the §cBEAST");
                c.teleportBeastCage(beast);
                beast.sendMessage("§eYou're the §cBEAST");

                this.runnable.cancel();
                started.remove(arena.getMap());
                arena.setArenaState(ArenaState.IN_GAME);

                TaskBeastRelease taskBeastRelease = new TaskBeastRelease(arena, beast);
                taskBeastRelease.run();
            }

			return;
        }

		if(arena.getArenaState() == ArenaState.STARTING) {
			arena.broadcastMessage("§cFaltan jugadores para iniciar, esperando jugadores");
            arena.setArenaState(ArenaState.WAITING);
		}

        timer = GlobalConfig.getTimingWaitingStart() + 1;
        this.runnable.cancel();
        started.remove(arena.getMap());
    }
}
