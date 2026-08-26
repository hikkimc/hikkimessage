package com.hm.plugin.message;

import com.hm.plugin.HMPlugin;
import com.hm.plugin.config.ConfigManager;
import com.hm.plugin.config.MessageEntry;
import com.hm.plugin.util.GradientUtil;
import com.hm.plugin.util.PlaceholderUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class MessageManager {

    private final HMPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Random random = new Random();
    private BukkitTask task;
    private int currentIndex = 0;
    private CycleMode cycleMode;

    public MessageManager(HMPlugin plugin) {
        this.plugin = plugin;
        startScheduler();
    }

    public void reload() {
        shutdown();
        currentIndex = 0;
        startScheduler();
    }

    public void shutdown() {
        if (task != null && !task.isCancelled()) task.cancel();
    }

    private void startScheduler() {
        String modeStr = plugin.getConfigManager().getMode();
        try { this.cycleMode = CycleMode.valueOf(modeStr.toUpperCase()); }
        catch (IllegalArgumentException e) { this.cycleMode = CycleMode.RANDOM; }

        int interval = plugin.getConfigManager().getInterval();
        if (interval < 1) interval = 60;

        task = new BukkitRunnable() {
            @Override
            public void run() { broadcastNextMessage(); }
        }.runTaskTimer(plugin, interval * 20L, interval * 20L);
    }

    public void broadcastNextMessage() {
        ConfigManager cfg = plugin.getConfigManager();
        List<String> keys = cfg.getMessageKeys();
        if (keys.isEmpty()) return;

        String key;
        switch (cycleMode) {
            case SEQUENTIAL -> {
                key = keys.get(currentIndex);
                currentIndex = (currentIndex + 1) % keys.size();
            }
            case SINGLE -> key = keys.get(0);
            default -> key = keys.get(random.nextInt(keys.size()));
        }

        MessageEntry entry = cfg.getMessages().get(key);
        if (entry != null) broadcastEntry(entry);
    }

    public void broadcastEntry(MessageEntry entry) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendToPlayer(player, entry);
        }
    }

    public void sendTestMessage(Player player) {
        ConfigManager cfg = plugin.getConfigManager();
        List<String> keys = cfg.getMessageKeys();
        if (keys.isEmpty()) return;
        String key = keys.get(random.nextInt(keys.size()));
        MessageEntry entry = cfg.getMessages().get(key);
        if (entry != null) sendToPlayer(player, entry);
    }

    public void forceNextMessage() {
        broadcastNextMessage();
    }

    private void sendToPlayer(Player player, MessageEntry entry) {
        switch (entry.getType()) {
            case MESSAGE -> sendMessage(player, entry);
            case TITLE -> sendTitle(player, entry);
            case SUBTITLE -> sendSubtitle(player, entry);
            case ACTIONBAR -> sendActionBar(player, entry);
            case BOSSBAR -> sendBossBar(player, entry);
        }
        playSound(player, entry);
    }

    private void sendMessage(Player player, MessageEntry entry) {
        for (String line : entry.getText()) {
            String processed = PlaceholderUtil.apply(entry.getPrefix() + line + entry.getSuffix());
            String mini = GradientUtil.convertLegacyToMiniMessage(processed);
            player.sendMessage(mm.deserialize(mini));
        }
    }

    private void sendTitle(Player player, MessageEntry entry) {
        String titleText = entry.getText().isEmpty() ? "" : entry.getText().get(0);
        String processedTitle = PlaceholderUtil.apply(entry.getPrefix() + titleText + entry.getSuffix());
        String processedSubtitle = PlaceholderUtil.apply(entry.getSubtitle());

        Component title = mm.deserialize(GradientUtil.convertLegacyToMiniMessage(processedTitle));
        Component subtitle = mm.deserialize(GradientUtil.convertLegacyToMiniMessage(processedSubtitle));

        Title.Times times = Title.Times.times(
            Duration.ofMillis(entry.getFadeIn() * 50L),
            Duration.ofMillis(entry.getDuration() * 1000L),
            Duration.ofMillis(entry.getFadeOut() * 50L)
        );

        player.showTitle(Title.title(title, subtitle, times));
    }

    private void sendSubtitle(Player player, MessageEntry entry) {
        String text = entry.getText().isEmpty() ? "" : entry.getText().get(0);
        String processed = PlaceholderUtil.apply(entry.getPrefix() + text + entry.getSuffix());
        Component subtitle = mm.deserialize(GradientUtil.convertLegacyToMiniMessage(processed));

        Title.Times times = Title.Times.times(
            Duration.ofMillis(entry.getFadeIn() * 50L),
            Duration.ofMillis(entry.getDuration() * 1000L),
            Duration.ofMillis(entry.getFadeOut() * 50L)
        );

        player.showTitle(Title.title(Component.empty(), subtitle, times));
    }

    private void sendActionBar(Player player, MessageEntry entry) {
        String text = entry.getText().isEmpty() ? "" : entry.getText().get(0);
        String processed = PlaceholderUtil.apply(entry.getPrefix() + text + entry.getSuffix());
        Component component = mm.deserialize(GradientUtil.convertLegacyToMiniMessage(processed));
        player.sendActionBar(component);
    }

    private void sendBossBar(Player player, MessageEntry entry) {
        String text = entry.getText().isEmpty() ? "" : entry.getText().get(0);
        String processed = PlaceholderUtil.apply(entry.getPrefix() + text + entry.getSuffix());
        Component component = mm.deserialize(GradientUtil.convertLegacyToMiniMessage(processed));

        BossBar bossBar = BossBar.bossBar(component, 1.0f,
            net.kyori.adventure.bossbar.BossBar.Color.valueOf(entry.getBarColor().name()),
            net.kyori.adventure.bossbar.BossBar.Overlay.valueOf(entry.getBarStyle().name()));

        player.showBossBar(bossBar);

        int durationTicks = entry.getBarDuration() * 20;
        new BukkitRunnable() {
            @Override
            public void run() { player.hideBossBar(bossBar); }
        }.runTaskLater(plugin, durationTicks);
    }

    private void playSound(Player player, MessageEntry entry) {
        if (!plugin.getConfigManager().isSoundEnabled()) return;
        try {
            player.playSound(player.getLocation(), entry.getSound(),
                entry.getSoundVolume(), entry.getSoundPitch());
        } catch (Exception ignored) {}
    }
}
