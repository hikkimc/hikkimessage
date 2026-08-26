package com.hm.plugin;

import com.hm.plugin.config.MessageEntry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HMCommand implements CommandExecutor {

    private final HMPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public HMCommand(HMPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("hm.admin")) {
            sender.sendMessage(mm.deserialize("<red>Нет прав!</red>"));
            return true;
        }

        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {
            case "reload", "rl" -> {
                plugin.reload();
                sender.sendMessage(mm.deserialize("<green>✔ Конфигурация HM перезагружена!</green>"));
            }
            case "test", "t" -> {
                if (sender instanceof Player p) {
                    plugin.getMessageManager().sendTestMessage(p);
                } else {
                    sender.sendMessage(mm.deserialize("<red>Только в игре!</red>"));
                }
            }
            case "list", "l" -> {
                sender.sendMessage(mm.deserialize("<yellow>=== Сообщения ===</yellow>"));
                int i = 1;
                for (var entry : plugin.getConfigManager().getMessages().values()) {
                    String type = entry.getType().name().toLowerCase();
                    sender.sendMessage(mm.deserialize(
                        "<gray>[" + i + "]</gray> <aqua>" + entry.getId() + "</aqua> <dark_gray>(" + type + ")</dark_gray>"
                    ));
                    i++;
                }
            }
            case "next", "n" -> {
                plugin.getMessageManager().forceNextMessage();
                sender.sendMessage(mm.deserialize("<green>✔ Сообщение отправлено!</green>"));
            }
            case "send" -> {
                if (args.length < 2) {
                    sender.sendMessage(mm.deserialize("<red>Использование: /hm send <id></red>"));
                    return true;
                }
                MessageEntry entry = plugin.getConfigManager().getMessages().get(args[1]);
                if (entry == null) {
                    sender.sendMessage(mm.deserialize("<red>Сообщение '" + args[1] + "' не найдено!</red>"));
                    return true;
                }
                plugin.getMessageManager().broadcastEntry(entry);
                sender.sendMessage(mm.deserialize("<green>✔ Отправлено: " + args[1] + "</green>"));
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(mm.deserialize("""
            <gradient:#00AAFF:#AA00FF>╔════════════════════════════════════╗</gradient>
            <gradient:#00AAFF:#AA00FF>║        HM Plugin - Помощь          ║</gradient>
            <gradient:#00AAFF:#AA00FF>╠════════════════════════════════════╣</gradient>
            <gradient:#00AAFF:#AA00FF>║</gradient> <yellow>/hm reload</yellow> <gray>- Перезагрузить      <gradient:#00AAFF:#AA00FF>║</gradient>
            <gradient:#00AAFF:#AA00FF>║</gradient> <yellow>/hm test</yellow>   <gray>- Тестовое сообщение <gradient:#00AAFF:#AA00FF>║</gradient>
            <gradient:#00AAFF:#AA00FF>║</gradient> <yellow>/hm list</yellow>   <gray>- Список сообщений   <gradient:#00AAFF:#AA00FF>║</gradient>
            <gradient:#00AAFF:#AA00FF>║</gradient> <yellow>/hm next</yellow>   <gray>- Следующее сообщение<gradient:#00AAFF:#AA00FF>║</gradient>
            <gradient:#00AAFF:#AA00FF>║</gradient> <yellow>/hm send <id></yellow> <gray>- Отправить по ID  <gradient:#00AAFF:#AA00FF>║</gradient>
            <gradient:#00AAFF:#AA00FF>╚════════════════════════════════════╝</gradient>
            """));
    }
}
