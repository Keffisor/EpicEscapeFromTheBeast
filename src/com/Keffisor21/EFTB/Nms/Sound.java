package com.Keffisor21.EFTB.Nms;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Stream;

public enum Sound {
    NOTE_PLING(Lists.newArrayList("NOTE_PLING", "BLOCK_NOTE_PLING", "BLOCK_NOTE_BLOCK_PLING")),
    ENDERDRAGON_GROWL(Lists.newArrayList("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL", "ENTITY_ENDER_DRAGON_GROWL")),
    CLICK(Lists.newArrayList("CLICK", "UI_BUTTON_CLICK")),
    ANVIL_USE(Lists.newArrayList("ANVIL_USE", "BLOCK_ANVIL_USE")),
    CHEST_OPEN(Lists.newArrayList("CHEST_OPEN", "BLOCK_CHEST_OPEN"));

    private List<String> sounds;
    Sound(List<String> list) {
        this.sounds = list;
    }

    public org.bukkit.Sound getBukkitSound() {
        return Stream.of(org.bukkit.Sound.values()).filter(soundE -> sounds.contains(soundE.name())).findFirst().orElse(null);
    }

}
