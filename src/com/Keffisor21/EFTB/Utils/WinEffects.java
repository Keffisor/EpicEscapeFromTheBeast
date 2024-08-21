package com.Keffisor21.EFTB.Utils;

import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.EFTB;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinEffects {
    private final Player player;
    private int times = 0;

    public WinEffects(Player player) {
        this.player = player;
    }

    public void startFireworksAnimation() {
        new BukkitRunnable() {
            @Override
            public void run() {
                times++;

                if(times == 12 || player == null || !player.isOnline() || ArenaManager.getArenaOfPlayer(player) == null) {
                    cancel();
                    return;
                }

                int power = (int) (Math.random() * 3.0D) + 1;

                Firework fireworks = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                FireworkMeta fireworkmeta = fireworks.getFireworkMeta();

                List<Color> colors = Stream.of(Color.AQUA, Color.GREEN, Color.RED, Color.ORANGE, Color.PURPLE, Color.YELLOW).collect(Collectors.toList());

                FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(colors).withFade(colors).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();

                fireworkmeta.addEffect(e);
                fireworkmeta.setPower(power);
                fireworks.setFireworkMeta(fireworkmeta);
            }
        }.runTaskTimer(EFTB.instance, 0L, 10L);
    }

}
