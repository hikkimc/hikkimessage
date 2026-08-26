package com.hm.plugin.config;

import com.hm.plugin.HMPlugin;
import com.hm.plugin.message.MessageType;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ConfigManager {

    private final HMPlugin plugin;
    private int interval;
    private String mode;
    private String globalPrefix;
    private String globalSuffix;
    private boolean soundEnabled;
    private Sound globalSound;
    private float globalSoundVolume;
    private float globalSoundPitch;
    private final Map<String, MessageEntry> messages = new LinkedHashMap<>();
    private List<String> messageKeys = new ArrayList<>();

    public ConfigManager(HMPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        messages.clear();
        plugin.reloadConfig();
        var cfg = plugin.getConfig();

        this.interval = cfg.getInt("interval", 300);
        this.mode = cfg.getString("mode", "random").toLowerCase();
        this.globalPrefix = cfg.getString("prefix", "");
        this.globalSuffix = cfg.getString("suffix", "");

        this.soundEnabled = cfg.getBoolean("sound.enabled", true);
        String snd = cfg.getString("sound.type", "ENTITY_EXPERIENCE_ORB_PICKUP");
        try { this.globalSound = Sound.valueOf(snd); }
        catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Неверный звук: " + snd);
            this.globalSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
        this.globalSoundVolume = (float) cfg.getDouble("sound.volume", 0.5);
        this.globalSoundPitch = (float) cfg.getDouble("sound.pitch", 1.0);

        ConfigurationSection msgSection = cfg.getConfigurationSection("messages");
        if (msgSection != null) {
            for (String key : msgSection.getKeys(false)) {
                ConfigurationSection sec = msgSection.getConfigurationSection(key);
                if (sec == null) continue;

                MessageType type;
                try { type = MessageType.valueOf(sec.getString("type", "message").toUpperCase()); }
                catch (IllegalArgumentException e) { type = MessageType.MESSAGE; }

                // text может быть строкой или списком
                List<String> text = new ArrayList<>();
                if (sec.isList("text")) {
                    text.addAll(sec.getStringList("text"));
                } else {
                    String t = sec.getString("text", "");
                    if (!t.isEmpty()) text.add(t);
                }
                if (text.isEmpty()) text.add("");

                String prefix = sec.getString("prefix", globalPrefix);
                String suffix = sec.getString("suffix", globalSuffix);

                // Sound override
                Sound entrySound = globalSound;
                float entryVol = globalSoundVolume;
                float entryPitch = globalSoundPitch;
                if (sec.contains("sound")) {
                    ConfigurationSection sndSec = sec.getConfigurationSection("sound");
                    if (sndSec != null) {
                        String sType = sndSec.getString("type");
                        if (sType != null) {
                            try { entrySound = Sound.valueOf(sType); }
                            catch (IllegalArgumentException ex) { /* ignore */ }
                        }
                        entryVol = (float) sndSec.getDouble("volume", globalSoundVolume);
                        entryPitch = (float) sndSec.getDouble("pitch", globalSoundPitch);
                    }
                }

                // Title params
                String subtitle = sec.getString("subtitle", "");
                int duration = sec.getInt("duration", 3);
                int fadeIn = sec.getInt("fade-in", 10);
                int fadeOut = sec.getInt("fade-out", 10);

                // BossBar params
                BarColor barColor = BarColor.PURPLE;
                BarStyle barStyle = BarStyle.SOLID;
                int barDuration = sec.getInt("duration", 5);
                try { barColor = BarColor.valueOf(sec.getString("color", "PURPLE").toUpperCase()); }
                catch (IllegalArgumentException e) { /* default */ }
                try { barStyle = BarStyle.valueOf(sec.getString("style", "SOLID").toUpperCase()); }
                catch (IllegalArgumentException e) { /* default */ }

                MessageEntry entry = new MessageEntry(
                    key, type, text, prefix, suffix,
                    entrySound, entryVol, entryPitch,
                    subtitle, duration, fadeIn, fadeOut,
                    barColor, barStyle, barDuration
                );
                messages.put(key, entry);
            }
        }

        this.messageKeys = new ArrayList<>(messages.keySet());

        if (messages.isEmpty()) {
            plugin.getLogger().warning("Сообщения не найдены в конфиге!");
        } else {
            plugin.getLogger().info("Загружено " + messages.size() + " сообщений.");
        }
    }

    public int getInterval() { return interval; }
    public String getMode() { return mode; }
    public String getGlobalPrefix() { return globalPrefix; }
    public String getGlobalSuffix() { return globalSuffix; }
    public boolean isSoundEnabled() { return soundEnabled; }
    public Sound getGlobalSound() { return globalSound; }
    public float getGlobalSoundVolume() { return globalSoundVolume; }
    public float getGlobalSoundPitch() { return globalSoundPitch; }
    public Map<String, MessageEntry> getMessages() { return messages; }
    public List<String> getMessageKeys() { return messageKeys; }
}
