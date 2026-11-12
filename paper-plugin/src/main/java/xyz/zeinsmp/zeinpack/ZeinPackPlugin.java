package xyz.zeinsmp.zeinpack;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZeinPackPlugin extends JavaPlugin implements Listener {
    private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        if (!isPackConfigValid()) {
            getLogger().severe("Invalid resource-pack URL or SHA-1 in config.yml. Pack requests are disabled.");
        } else {
            getLogger().info("ZEIN SMP resource-pack delivery is enabled.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        int delay = Math.max(0, getConfig().getInt("resource-pack.join-delay-ticks", 20));
        UUID playerId = event.getPlayer().getUniqueId();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                sendPack(player);
            }
        }, delay);
    }

    @EventHandler
    public void onPackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        switch (event.getStatus()) {
            case SUCCESSFULLY_LOADED -> {
                getLogger().info(player.getName() + " successfully loaded the ZEIN SMP resource pack.");
                player.sendMessage(message("messages.loaded"));
            }
            case DECLINED -> {
                getLogger().warning(player.getName() + " declined the ZEIN SMP resource pack.");
                player.sendMessage(message("messages.declined"));
            }
            case FAILED_DOWNLOAD -> {
                getLogger().warning(player.getName() + " failed to download the ZEIN SMP resource pack.");
                player.sendMessage(message("messages.failed"));
            }
            default -> getLogger().info(player.getName() + " resource-pack status: " + event.getStatus());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zeinpack.admin")) {
            sender.sendMessage(message("messages.no-permission"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(message("messages.reloaded"));
            if (!isPackConfigValid()) {
                sender.sendMessage(message("messages.invalid-config"));
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("send")) {
            if (sender instanceof Player player) {
                sendPack(player);
                sender.sendMessage(message("messages.sent"));
            } else {
                sender.sendMessage("This command can only be used by a player.");
            }
            return true;
        }

        sender.sendMessage("Usage: /zeinpack <send|reload>");
        return true;
    }

    private void sendPack(Player player) {
        if (!isPackConfigValid()) {
            player.sendMessage(message("messages.invalid-config"));
            return;
        }

        String url = getConfig().getString("resource-pack.url", "").trim();
        String sha1 = getConfig().getString("resource-pack.sha1", "").trim().toLowerCase(Locale.ROOT);
        boolean required = getConfig().getBoolean("resource-pack.required", true);
        String promptText = getConfig().getString("resource-pack.prompt", "<red>ZEIN SMP resource pack</red>");
        Component prompt = miniMessage.deserialize(promptText);

        player.setResourcePack(UUID.randomUUID(), url, sha1, prompt, required);
    }

    private boolean isPackConfigValid() {
        String url = getConfig().getString("resource-pack.url", "").trim();
        String sha1 = getConfig().getString("resource-pack.sha1", "").trim();
        return (url.startsWith("https://") || url.startsWith("http://"))
                && SHA1_PATTERN.matcher(sha1).matches();
    }

    private Component message(String path) {
        String value = getConfig().getString(path, "");
        return miniMessage.deserialize(value);
    }
}
