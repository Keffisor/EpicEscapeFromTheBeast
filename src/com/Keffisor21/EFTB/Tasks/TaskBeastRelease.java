package com.Keffisor21.EFTB.Tasks;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaState;
import com.Keffisor21.EFTB.Configs.GlobalConfig;
import com.Keffisor21.EFTB.Nms.Sound;
import com.Keffisor21.EFTB.Utils.Task;
import com.Keffisor21.EFTB.Utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TaskBeastRelease extends Task {
    private int time = GlobalConfig.getTimingBeastRelease() + 1;

    private final Arena arena;
    private final Player beast;

    public TaskBeastRelease(Arena arena, Player beast) {
        super(0L, 20L);

        this.arena = arena;
        this.beast = beast;
    }

    @Override
    protected void toExecute() {
        if(!arena.getArenaState().equals(ArenaState.IN_GAME)) {
            this.runnable.cancel();
            return;
        }

        time--;

        beast.setLevel(time);

        if(time == GlobalConfig.getTimingBeastRelease()) {

            ItemStack[] armor = {new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_HELMET)};
            beast.getInventory().setArmorContents(armor);
            beast.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));

            Utils.setVariables(GlobalConfig.getConfigList("Messages.BeastRelease.Release"), Lists.newArrayList("%release_time%"), Lists.newArrayList(GlobalConfig.getTimingBeastRelease()))
                    .forEach(arena::broadcastMessage);
            return;
        }

        if(time <= 10 && time > 1) {
            beast.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Messages.BeastRelease.Beast.ReleaseCount"), Lists.newArrayList("%time%"), Lists.newArrayList(time)));
            beast.playSound(beast.getLocation(), Sound.CLICK.getBukkitSound(), 1.0F, 1.0F);
            return;
        }

        if(time == 1) {
            beast.sendMessage(Utils.setVariables(GlobalConfig.getConfigString("Messages.BeastRelease.Beast.ReleaseCountLast"), Lists.newArrayList("%time%"), Lists.newArrayList(time)));
            beast.playSound(beast.getLocation(), Sound.CLICK.getBukkitSound(), 1.0F, 1.0F);
            return;
        }

        if(time != 0) return;

        this.runnable.cancel();

        arena.setBeastReleasedAt(Utils.getActualTimestamp());

        int pos = new Random().nextInt(arena.getMaxPlayers()) + 1;
        arena.getCagesController().forceTeleportCage(beast, pos); // Force tp to a random regular player cage

        arena.getPlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ANVIL_USE.getBukkitSound(), 1.0F, 1.0F));
        Utils.setVariables(GlobalConfig.getConfigList("Messages.BeastRelease.Released"), Lists.newArrayList("%beast%"), Lists.newArrayList(beast.getName()))
                .forEach(arena::broadcastMessage);
    }

}
