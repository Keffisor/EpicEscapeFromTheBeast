package com.Keffisor21.EFTB.ArenaControllers;

import com.Keffisor21.EFTB.Arena.Arena;
import com.Keffisor21.EFTB.Arena.ArenaState;
import com.Keffisor21.EFTB.Configs.GlobalConfig;
import com.Keffisor21.EFTB.LobbyManager.Spawn;
import com.Keffisor21.EFTB.Tasks.TaskGameFinished;
import com.Keffisor21.EFTB.Tasks.TaskGameStart;
import com.Keffisor21.EFTB.Utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ArenaController {
    private final Arena arena;

    public ArenaController(Arena arena) {
        this.arena = arena;
    }

    public void playerJoin(Player p) {
        List<Player> players = arena.getPlayers();

        if(players.isEmpty()) return;

        arena.getWorld().setTime(1000);
        arena.getWorld().setGameRuleValue("doDaylightCycle", "false");
        arena.getWorld().setGameRuleValue("doMobSpawning", "false");

        arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Messages.PlayerJoin"),
                Lists.newArrayList("%player%", "%players_count%", "%max_players_count%"),
                Lists.newArrayList(p.getName(), players.size(), arena.getMaxPlayers()))
        );

        ItemStack item = new ItemStack(Material.EMPTY_MAP);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.setVariables(GlobalConfig.getConfigString("Item.PrincipalVoteMenu"), null, null));

        item.setItemMeta(itemMeta);
        p.getInventory().setItem(4, item);

		if(arena.getArenaState().equals(ArenaState.WAITING) && TaskGameStart.started.get(arena.getMap()) == null) {
            TaskGameStart task = new TaskGameStart(arena);
            task.run();
            TaskGameStart.started.put(arena.getMap(), true);
        }

    }

    public void playerLeave(Player p) {
        if(arena.getArenaState().equals(ArenaState.RESTARTING)) {
            Spawn.send(p);
            return;
        }

        p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setLevel(0);

        Spawn.send(p);

        List<Player> players = arena.getPlayers();

        if(players.isEmpty()) return;

        int max = arena.getMaxPlayers();
        int size = players.size();

        arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Messages.PlayerLeave"),
                Lists.newArrayList("%player%", "%players_count%", "%max_players_count%"),
                Lists.newArrayList(p.getName(), size, max))
        );

        checkWinConditions();
    }

    public void playerKilled(Player p, Object killer) {
        p.setGameMode(GameMode.SPECTATOR);
        arena.removePlayer(p, true);

        if(killer instanceof Player)
            arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Messages.PlayerKilledByPlayer"),
                    Lists.newArrayList("%player%", "%killer%"), Lists.newArrayList(p.getName(), ((Player)killer).getName()))
            );

        if(killer instanceof String)
            arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Messages.PlayerKilledByPlayer"),
                    Lists.newArrayList("%player%", "%killer%"), Lists.newArrayList(p.getName(), killer))
            );

        checkWinConditions();
    }

    public void playerDied(Player p) {
        p.setGameMode(GameMode.SPECTATOR);
        arena.removePlayer(p, true);

        arena.broadcastMessage(Utils.setVariables(GlobalConfig.getConfigString("Messages.PlayerDied"), Lists.newArrayList("%player%"), Lists.newArrayList(p.getName())));

        checkWinConditions();
    }

    public void playerWin(boolean beast) {
        arena.setArenaState(ArenaState.RESTARTING);

        if(beast) GlobalConfig.getConfigList("Messages.BeastWin").forEach(content -> {
            arena.broadcastMessage(Utils.setVariables(content, null, null));
        });

        if(!beast) GlobalConfig.getConfigList("Messages.PlayersWin").forEach(content -> {
            arena.broadcastMessage(Utils.setVariables(content, null, null));
        });

        GlobalConfig.getConfigList("Messages.GameFinished").forEach(content -> {
            arena.broadcastMessage(Utils.setVariables(content, null, null));
        });

        arena.getPlayers().forEach(player -> {
            String[] a = {null, null, null, null};

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            player.setFoodLevel(20);
            player.setHealth(20);
        });

        TaskGameFinished taskGameFinished = new TaskGameFinished(arena, beast);
        taskGameFinished.run();
    }

    public void checkWinConditions() {
        if(!arena.getArenaState().equals(ArenaState.IN_GAME)) return;

        Player beast = arena.getBeast();

        if(!arena.getPlayers().contains(beast) || arena.getGameTimeLeft() == 0) { // Players win
            playerWin(false);
            return;
        }

        if(arena.getPlayers().size() == 1 && arena.getPlayers().contains(beast)) { // Beast win!
            playerWin(true);
            return;
        }

    }

}
