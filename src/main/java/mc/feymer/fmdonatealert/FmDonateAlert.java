package mc.feymer.fmdonatealert;

import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class FmDonateAlert extends JavaPlugin implements CommandExecutor {
    private Random random;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Плагин включен!");
        this.random = new Random();
        this.getCommand("fmda").setExecutor(this);

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fmdonatealert.admin")) {
            sender.sendMessage(getConfig().getString(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.no-perms"))));
            return true;
        } else if (args.length == 1) {
            if (args[0].equals("reload")) {
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.success-reload")));
            } else
                for (String s : getConfig().getStringList("messages.usage"))
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            return true;
        } else if (args.length < 3) {
            for (String s : getConfig().getStringList("messages.usage"))
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            return true;
        }
        if (args[0].equals("send")) {
            String nick = args[1];
            String donate = Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length));
            List<String> texts = new ArrayList(getConfig().getConfigurationSection("texts").getKeys(false));
            List<String> list = getConfig().getStringList("texts." + texts.get(this.random.nextInt(texts.size())));
            list.forEach((a) -> {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', a.replace("%player%", nick).replace("%donate%", donate)));
            });
            if (getConfig().getBoolean("enable-title")) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.sendTitle(getConfig().getString("messages.title").replace("&", "§"), getConfig().getString("messages.subtitle").replace("%donate%", donate).replace("&", "§").replace("%player%", nick));
                }
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.success-send")));
            return true;
        } else
            for (String s : getConfig().getStringList("messages.usage"))
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        return true;
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин выключен!");
    }
}
