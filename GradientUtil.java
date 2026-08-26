package com.hm.plugin.util;

public class GradientUtil {

    public static String convertLegacyToMiniMessage(String text) {
        if (text == null || text.isEmpty()) return text;

        // HEX &#RRGGBB → <#RRGGBB>
        text = text.replaceAll("&#([0-9a-fA-F]{6})", "<#$1>");

        // Legacy colors
        text = text.replace("&0", "<black>")
                   .replace("&1", "<dark_blue>")
                   .replace("&2", "<dark_green>")
                   .replace("&3", "<dark_aqua>")
                   .replace("&4", "<dark_red>")
                   .replace("&5", "<dark_purple>")
                   .replace("&6", "<gold>")
                   .replace("&7", "<gray>")
                   .replace("&8", "<dark_gray>")
                   .replace("&9", "<blue>")
                   .replace("&a", "<green>")
                   .replace("&b", "<aqua>")
                   .replace("&c", "<red>")
                   .replace("&d", "<light_purple>")
                   .replace("&e", "<yellow>")
                   .replace("&f", "<white>")
                   .replace("&k", "<obfuscated>")
                   .replace("&l", "<bold>")
                   .replace("&m", "<strikethrough>")
                   .replace("&n", "<underlined>")
                   .replace("&o", "<italic>")
                   .replace("&r", "<reset>");

        // Short gradient syntax <g:#hex1:#hex2> → <gradient:#hex1:#hex2>
        text = text.replaceAll("<g:(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6})>", "<gradient:$1:$2>");
        text = text.replaceAll("<g:(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6})>", "<gradient:$1:$2:$3>");
        text = text.replaceAll("<g:(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6})>", "<gradient:$1:$2:$3:$4>");
        text = text.replace("</g>", "</gradient>");

        return text;
    }
}
