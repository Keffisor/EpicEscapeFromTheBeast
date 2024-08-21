package com.Keffisor21.EFTB.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
	public static String getMinecraftVersion() {
	    Matcher matcher = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))").matcher(Bukkit.getVersion());
	    if (matcher.find())
	      return matcher.group(2); 
	    return null;
	}

	public static Player getRandomPlayer(List<Player> players) {
		return ((Player)players.get(new Random().nextInt(players.size())));
	}

	public static Enchantment getEnchantment(String enchantmentValueLegacy, String enchantmentValue) {
		Enchantment enchantment = Stream.of(Enchantment.values()).filter(
				enchantmentE -> enchantmentE.getName().equalsIgnoreCase(enchantmentValueLegacy) || enchantmentE.getName().equalsIgnoreCase(enchantmentValue)
		).findFirst().orElse(null);

		if(enchantment == null) {
			System.out.println(String.format("Enchantment with values %s and %s was not found", enchantmentValueLegacy, enchantmentValue));
			return null;
		}

		return enchantment;
	}

	public static Material getMaterial(String materialValueLegacy, String materialValue) {
		Material material = Stream.of(Material.values()).filter(
				materialE -> materialE.name().equalsIgnoreCase(materialValueLegacy) || materialE.name().equalsIgnoreCase(materialValue)
		).findFirst().orElse(null);

		if(material == null) {
			System.out.println(String.format("Material with values %s and %s was not found", materialValueLegacy, materialValue));
			return null;
		}

		return material;
	}

	public static boolean isValidInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e2) {
			return false;
		}
	}

	public static int getActualTimestamp() {
		return (int)(System.currentTimeMillis() / 1000);
	}

	public static LinkedHashMap<String, Integer>  sortByHighestInteger(HashMap<String, Integer> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}

	public static ItemStack getPotionEffect(byte durability, PotionEffectType potionEffectType, int duration, int amplifier) {
		ItemStack itemStack = new ItemStack(Material.POTION, 1, durability);

		if(itemStack.getType().equals(Material.AIR)) { // 1.21 compatibility
			itemStack = new ItemStack(Material.POTION);

			PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
			itemMeta.addCustomEffect(new PotionEffect(potionEffectType, duration, amplifier), true);
			itemStack.setItemMeta(itemMeta);
		}

		return itemStack;
	}

	public static String setVariables(String content, @Nullable List<Object> from, @Nullable List<Object> to) {
		content = content.replace("&", "§");

		if(from !=  null && to != null)
			for(int i = 0; i < from.size(); i++)
				content = content.replace(String.valueOf(from.get(i)), String.valueOf(to.get(i)));

		return content;
	}

	public static List<String> setVariables(List<String> content, @Nullable List<Object> from, @Nullable List<Object> to) {
		return content.stream().map(s -> setVariables(s, from, to)).collect(Collectors.toList());
	}

	public static String setVariables(String content) {
		return content.replace("&", "§");
	}

	public static List<String> setVariables(List<String> content) {
		return content.stream().map(Utils::setVariables).collect(Collectors.toList());
	}

}
