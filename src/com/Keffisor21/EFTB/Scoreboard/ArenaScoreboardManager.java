package com.Keffisor21.EFTB.Scoreboard;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.EFTB;
import com.Keffisor21.EFTB.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArenaScoreboardManager {
    private static HashMap<UUID, ArenaScoreboard> scoreboards = new HashMap<UUID, ArenaScoreboard>();

    public static void scoreboard(final Player p) {
        if(!EFTB.instance.getConfig().getBoolean("Scoreboard.enabled") || ArenaManager.playerMap.get(p) == null || scoreboards.containsKey(p.getUniqueId())) return;

        final ArenaScoreboard scoreboard = new ArenaScoreboard(p);

        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = ArenaManager.getArenaOfPlayer(p);

                if(arena == null) {
                    scoreboard.toggleScoreboard();
                    scoreboards.remove(p.getUniqueId());
                    cancel();
                    return;
                }

                int timeLeft = arena.getGameTimeLeft();

                scoreboard.setTitle(0, EFTB.instance.getConfig().getString("Scoreboard.title").replaceAll("&", "§"));

                List<String> h = EFTB.instance.getConfig().getStringList("Scoreboard.lines");

                int line = EFTB.instance.getConfig().getStringList("Scoreboard.lines").size();

                for(String s : h) {
                    int minutes = timeLeft / 60;
                    int seconds = timeLeft % 60;

                    String disMin = (minutes < 10 ? "0" : "") + minutes;
                    String disSec = (seconds < 10 ? "0" : "") + seconds;
                    String formattedTime = disMin + ":" + disSec;

                    scoreboard.setLine(0, line, s.
                            replaceAll("&", "§").
                            replaceAll("%players_count%", "" + arena.getPlayers().size()).
                            replaceAll("%max_players_count%", "" + arena.getMaxPlayers()).
                            replaceAll("%player%", p.getName()).
                            replaceAll("%map%", arena.getMap()).
                            replaceAll("%time_left%", formattedTime)
                    );

                    line--;
                }

                arena.getController().checkWinConditions();
            }
        }.runTaskTimer(EFTB.instance, 0L, 20L);

        scoreboards.put(p.getUniqueId(), scoreboard);
        scoreboard.toggleScoreboard();
    }


}
