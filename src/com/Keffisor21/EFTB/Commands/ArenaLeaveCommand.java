package com.Keffisor21.EFTB.Commands;

import com.Keffisor21.EFTB.Arena.ArenaManager;
import com.Keffisor21.EFTB.LobbyManager.Spawn;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Keffisor21.EFTB.Configs.MapsConfig;
import com.Keffisor21.EFTB.Arena.Arena;

import net.md_5.bungee.api.ChatColor;

public class ArenaLeaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This only can be executed by a player");
            return false;
        }

		Player p = (Player) sender;

		Arena arena = ArenaManager.getArenaOfPlayer(p);

		if(arena != null) {
            arena.removePlayer(p, false);

            p.setGameMode(GameMode.SURVIVAL);
            p.setLevel(0);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));

            Spawn.send(p);
            return false;
        }

        if(MapsConfig.existAsMap(p.getWorld().getName())) {
            Spawn.send(p);
            p.setGameMode(GameMode.SURVIVAL);
        }

        return false;
    }

}
