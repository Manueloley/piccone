package me.manu.jaycobpickaxe.commands;

import me.manu.jaycobpickaxe.JaycobPickaxe;
import me.manu.jaycobpickaxe.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand implements CommandExecutor, TabCompleter {

    private final JaycobPickaxe plugin;

    public GiveCommand(JaycobPickaxe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (sender.hasPermission("jaycobpickaxe.give")){
                if (args[0].equalsIgnoreCase("give")) {
                    handleGiveCommand(sender, args);
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("jaycobpickaxe.reload")){
                        plugin.reloadConfig();
                        plugin.reloadMessagesConfig();
                        sender.sendMessage(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("config-reload"))));
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    if (sender.hasPermission("jaycobpickaxe.info")){
                        List<String> list = plugin.getMessagesConfig().getStringList("info");
                        for (String line : list) {
                            sender.sendMessage(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', line)));
                        }
                    }
                } else {
                    sender.sendMessage(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("unknown-command"))));
                }
            }
            }
        return true;
    }

    private void handleGiveCommand(CommandSender sender, String[] args) {
        String playerName = args[1];
        Player target = Bukkit.getServer().getPlayerExact(playerName);

        if (target == null) {
            sender.sendMessage(ColorUtil.message(ChatColor.translateAlternateColorCodes('&',plugin.getMessagesConfig().getString("player-not-found"))));
            return;
        }

        int amount;
        if (args.length > 2) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("error-int"))));
                return;
            }

            for (int i = 0; i < amount; i++) {
                ItemStack pickaxe = new ItemStack(Material.valueOf(plugin.getConfig().getString("pickaxe.material")));
                ItemMeta pickaxeMeta = pickaxe.getItemMeta();
                pickaxeMeta.setDisplayName(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("pickaxe.display-name"))));
                List<String> lore = plugin.getConfig().getStringList("pickaxe.lore");
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', line)));
                }
                pickaxeMeta.setLore(coloredLore);
                pickaxeMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, false);
                pickaxeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                pickaxe.setItemMeta(pickaxeMeta);
                target.getInventory().addItem(pickaxe);
            }
            String giveMessage = plugin.getMessagesConfig().getString("give-message");
            giveMessage = giveMessage.replaceAll("%amount%", String.valueOf(amount));
            giveMessage = giveMessage.replaceAll("%target%", target.getName());
            sender.sendMessage(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', giveMessage)));
        } else {
            sender.sendMessage(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("empty-number-argument"))));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "reload", "info");
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 3) {
            return Arrays.asList("1", "2", "3", "5", "10");
        }
        return null;
    }
}
