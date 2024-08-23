package com.Keffisor21.EFTB.Tasks;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaState;
import com.Keffisor21.EFTB.ArenaControllers.CagesController;
import com.Keffisor21.EFTB.Configs.GlobalConfig;
import com.Keffisor21.EFTB.Nms.Sound;
import com.Keffisor21.EFTB.Utils.Task;
import com.Keffisor21.EFTB.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

                arena.broadcastMessage(GlobalConfig.getConfigString("Messages.GameStarting.ServerFull"));
                arena.setArenaState(ArenaState.STARTING);
                started.put(arena.getMap(), true);
            }

            if(timer < GlobalConfig.getTimingWaitingFull() && !arena.getArenaState().equals(ArenaState.STARTING)) {
                arena.setArenaState(ArenaState.STARTING);
                started.put(arena.getMap(), true);
            }

			for(int i = 3; i != 0; i--) {
                if(i != timer) continue;

				if(i != 1) arena.broadcastMessage(GlobalConfig.getConfigString("Messages.GameStarting.StartingCount"));
				if(i == 1) arena.broadcastMessage(GlobalConfig.getConfigString("Messages.GameStarting.StartingCountLast"));

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

                GlobalConfig.getConfigList("Messages.GameStarted.Started").forEach(arena::broadcastMessage);
                arena.broadcastMessage(GlobalConfig.getConfigString("Messages.GameStarted.TimeSelected_" + highestTimeVote));
                arena.broadcastMessage(GlobalConfig.getConfigString("Messages.GameStarted.SpeedSelected_" + highestSpeedVote));

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

                beast.sendTitle(GlobalConfig.getConfigString("Messages.BeastRelease.PlayerSelected.Title"), "Messages.BeastRelease.PlayerSelected.Subtitle");
                c.teleportBeastCage(beast);
                GlobalConfig.getConfigList("Messages.BeastRelease.PlayerSelected.Messages").forEach(beast::sendMessage);

                this.runnable.cancel();
                started.remove(arena.getMap());
                arena.setArenaState(ArenaState.IN_GAME);

                TaskBeastRelease taskBeastRelease = new TaskBeastRelease(arena, beast);
                taskBeastRelease.run();
            }

			return;
        }

		if(arena.getArenaState() == ArenaState.STARTING) {
			arena.broadcastMessage(GlobalConfig.getConfigString("Messages.GameStarting.NoEnoughPlayers"));
            arena.setArenaState(ArenaState.WAITING);
		}

        timer = GlobalConfig.getTimingWaitingStart() + 1;
        this.runnable.cancel();
        started.remove(arena.getMap());
    }
}
