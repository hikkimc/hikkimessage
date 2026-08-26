package com.hm.plugin;

import com.hm.plugin.config.ConfigManager;
import com.hm.plugin.message.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HMPlugin extends JavaPlugin {

    private static HMPlugin instance;
    private ConfigManager configManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        getCommand("hm").setExecutor(new HMCommand(this));

        getLogger().info("╔═══════════════════════════════════════════╗");
        getLogger().info("║     HM Plugin включён! v" + getDescription().getVersion() + "         ║");
        getLogger().info("║  Автоматические сообщения активны         ║");
        getLogger().info("║  Типы: message, title, subtitle,          ║");
        getLogger().info("║        actionbar, bossbar                 ║");
        getLogger().info("╚═══════════════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        if (messageManager != null) messageManager.shutdown();
        getLogger().info("HM Plugin выключен.");
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
    }

    public static HMPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public MessageManager getMessageManager() { return messageManager; }
}
