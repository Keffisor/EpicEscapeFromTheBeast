package com.Keffisor21.EFTB.Arena;

import java.util.*;
import java.util.stream.Collectors;

import com.Keffisor21.EFTB.ArenaControllers.ArenaController;
import com.Keffisor21.EFTB.ArenaControllers.CagesController;
import com.Keffisor21.EFTB.LobbyManager.Spawn;
import com.Keffisor21.EFTB.Scoreboard.ArenaScoreboardManager;
import com.Keffisor21.EFTB.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.Keffisor21.EFTB.Configs.MapsConfig;

import javax.annotation.Nullable;

public class Arena {
    private final String map;
    private ArenaState state = ArenaState.WAITING;

    private List<Player> players = new ArrayList<>();
    private Player beast = null;

    private final ArenaController controller;
    private final CagesController cages;

    private final List<UUID> playersVotedTime = new ArrayList<UUID>();
    private final List<UUID> playersVotedSpeed = new ArrayList<UUID>();

    private final HashMap<String, Integer> votesTimeCount = new HashMap<String, Integer>();
    private final HashMap<String, Integer> votesSpeedCount = new HashMap<String, Integer>();

    private int beastReleasedAt = 0;

    public Arena(String map) {
        this.map = map;

        this.controller = new ArenaController(this);
        this.cages = new CagesController(map);
    }

    public String getMap() {
        return map;
    }

    public World getWorld() {
        return Bukkit.getWorld(map);
    }

    public int getMinPlayers() {
        return MapsConfig.getMinPlayers(map);
    }

    public int getMaxPlayers() {
        return MapsConfig.getMaximumPlayers(map);
    }

    public boolean isArenaFull() {
        return getPlayers().size() >= getMaxPlayers();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getSpectators() {
        return getWorld().getPlayers().stream().filter(p -> p.getGameMode().equals(GameMode.SPECTATOR)).collect(Collectors.toList());
    }

    @Nullable
    public Player getBeast() {
        return beast;
    }

    public ArenaState getArenaState() {
        return state;
    }

    public ArenaController getController() {
        return controller;
    }

    public CagesController getCagesController() {
        return cages;
    }

    public List<UUID> getPlayersVotedTime() {
        return this.playersVotedTime;
    }

    public List<UUID> getPlayersSpeedVoted() {
        return this.playersVotedSpeed;
    }

    public int getTimeVotesCount(String id) {
        if(votesTimeCount.get(id) == null) return 0;
        return votesTimeCount.get(id);
    }

    public int getSpeedVotesCount(String id) {
        if(votesSpeedCount.get(id) == null) return 0;
        return votesSpeedCount.get(id);
    }

    public String getHighestTimeVote() {
        LinkedHashMap<String, Integer> votes = Utils.sortByHighestInteger(votesTimeCount);

        if(votes.isEmpty()) return "DAY";
        return (String) votes.keySet().toArray()[0];
    }

    public String getHighestSpeedVote() {
        LinkedHashMap<String, Integer> votes = Utils.sortByHighestInteger(votesSpeedCount);

        if(votes.isEmpty()) return "NORMAL";
        return (String) votes.keySet().toArray()[0];
    }

    public int getBeastReleasedAt() {
        return this.beastReleasedAt;
    }

    public int getGameTimeLeft() {
        if(getBeastReleasedAt() == 0) return 300;
        int timeLeft = 300 - (Utils.getActualTimestamp() - getBeastReleasedAt());
        if(timeLeft < 0) timeLeft = 0;
        return timeLeft;
    }

    public void addPlayer(Player player) {
        players.add(player);
        ArenaManager.playerMap.put(player, this.getMap());

        Spawn.sendWaitingLobby(player, map);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setFoodLevel(20);
        player.setHealth(20);

        this.getController().playerJoin(player);
        ArenaScoreboardManager.scoreboard(player);
    }

    public void removePlayer(Player player, boolean killed) {
        players.remove(player);

        if(!killed) this.getController().playerLeave(player);
        ArenaManager.playerMap.remove(player);
    }

    public void setBeast(Player beast) {
        this.beast = beast;
    }

    public void setArenaState(ArenaState state) {
        this.state = state;
    }

    public void addVoteTimeCount(Player player, String id) {
        playersVotedTime.add(player.getUniqueId());

        Integer count = votesTimeCount.get(id);

        if(count == null) {
            votesTimeCount.put(id, 1);
            return;
        }

        votesTimeCount.put(id, (count + 1));
    }

    public void addVoteSpeedCount(Player player, String id) {
        playersVotedSpeed.add(player.getUniqueId());

        Integer count = votesSpeedCount.get(id);

        if(count == null) {
            votesSpeedCount.put(id, 1);
            return;
        }

        votesSpeedCount.put(id, (count + 1));
    }

    public void setBeastReleasedAt(int beastReleasedAt) {
        this.beastReleasedAt = beastReleasedAt;
    }

    public void broadcastMessage(String content) {
        List<Player> players = this.getPlayers();

        if(players.isEmpty()) return;

        players.forEach(p2 -> p2.sendMessage(content));
        getSpectators().forEach(p2 -> p2.sendMessage(content)); // Send also messages to the spectators
    }

}
