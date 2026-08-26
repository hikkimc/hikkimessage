package com.hm.plugin.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class PlaceholderUtil {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String apply(String text) {
        if (text == null || text.isEmpty()) return "";

        text = text.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("{max}", String.valueOf(Bukkit.getMaxPlayers()));
        text = text.replace("{time}", LocalDateTime.now().format(TIME_FMT));
        text = text.replace("{date}", LocalDateTime.now().format(DATE_FMT));
        text = text.replace("{tps}", String.format("%.1f", Bukkit.getTPS()[0]));

        if (text.contains("{player}")) {
            var players = Bukkit.getOnlinePlayers();
            if (!players.isEmpty()) {
                Player p = players.stream()
                    .skip(ThreadLocalRandom.current().nextInt(players.size()))
                    .findFirst().orElse(null);
                text = text.replace("{player}", p != null ? p.getName() : "Игрок");
            } else {
                text = text.replace("{player}", "Никто");
            }
        }

        if (text.contains("{world}")) {
            var worlds = Bukkit.getWorlds();
            if (!worlds.isEmpty()) {
                text = text.replace("{world}", worlds.get(ThreadLocalRandom.current().nextInt(worlds.size())).getName());
            }
        }

        return text;
    }
}
