package com.Keffisor21.EFTB.Tasks;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.Utils.Task;
import com.Keffisor21.EFTB.Utils.WinEffects;

import java.util.ArrayList;
import java.util.stream.Stream;

public class TaskGameFinished extends Task {
    private int timer = 11;
    private Arena arena;
    private boolean beast;

    public TaskGameFinished(Arena arena, boolean beast) {
        super(0L, 20L);

        this.arena = arena;
        this.beast = beast;
    }

    @Override
    protected void toExecute() {
        timer--;

        if(timer == 10) {
            arena.getPlayers().forEach(player -> {
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

                player.getInventory().clear();
                player.getInventory().setArmorContents(null);

                if(!beast && player == arena.getBeast()) return;

                new WinEffects(player).startFireworksAnimation();
            });
        }

        if(timer == 0) {
            new ArrayList<>(arena.getPlayers()).forEach(player -> player.performCommand("leave"));
            arena.getSpectators().forEach(player -> player.performCommand("leave"));

            ArenaManager.removeArena(arena.getMap());
        }
    }

}
