package com.hm.plugin.config;

import com.hm.plugin.message.MessageType;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.List;

public class MessageEntry {

    private final String id;
    private final MessageType type;
    private final List<String> text;
    private final String prefix;
    private final String suffix;
    private final Sound sound;
    private final float soundVolume;
    private final float soundPitch;

    // Title
    private final String subtitle;
    private final int duration;
    private final int fadeIn;
    private final int fadeOut;

    // BossBar
    private final BarColor barColor;
    private final BarStyle barStyle;
    private final int barDuration;

    public MessageEntry(String id, MessageType type, List<String> text,
                        String prefix, String suffix,
                        Sound sound, float soundVolume, float soundPitch,
                        String subtitle, int duration, int fadeIn, int fadeOut,
                        BarColor barColor, BarStyle barStyle, int barDuration) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.prefix = prefix;
        this.suffix = suffix;
        this.sound = sound;
        this.soundVolume = soundVolume;
        this.soundPitch = soundPitch;
        this.subtitle = subtitle;
        this.duration = duration;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.barColor = barColor;
        this.barStyle = barStyle;
        this.barDuration = barDuration;
    }

    public String getId() { return id; }
    public MessageType getType() { return type; }
    public List<String> getText() { return text; }
    public String getPrefix() { return prefix; }
    public String getSuffix() { return suffix; }
    public Sound getSound() { return sound; }
    public float getSoundVolume() { return soundVolume; }
    public float getSoundPitch() { return soundPitch; }
    public String getSubtitle() { return subtitle; }
    public int getDuration() { return duration; }
    public int getFadeIn() { return fadeIn; }
    public int getFadeOut() { return fadeOut; }
    public BarColor getBarColor() { return barColor; }
    public BarStyle getBarStyle() { return barStyle; }
    public int getBarDuration() { return barDuration; }
}
