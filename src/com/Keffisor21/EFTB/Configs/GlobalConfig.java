package com.Keffisor21.EFTB.Configs;

import com.Keffisor21.EFTB.EFTB;
import com.Keffisor21.EFTB.Utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalConfig {

    public static int getTimingBeastRelease() {
        return EFTB.instance.getConfig().getInt("Arena.BeastRelease");
    }

    public static int getTimingWaitingStart() {
        return EFTB.instance.getConfig().getInt("Arena.WaitingStart");
    }

    public static int getTimingWaitingFull() {
        return EFTB.instance.getConfig().getInt("Arena.WaitingFull");
    }

    public static String getConfigString(String path) {
        return Utils.setVariables(EFTB.instance.getConfig().getString(path));
    }

    public static List<String> getConfigList(String path) {
        return EFTB.instance.getConfig().getStringList(path).stream().map(Utils::setVariables).collect(Collectors.toList());
    }

}
