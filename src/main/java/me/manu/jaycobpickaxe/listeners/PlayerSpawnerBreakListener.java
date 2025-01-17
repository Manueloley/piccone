package me.manu.jaycobpickaxe.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.manu.jaycobpickaxe.JaycobPickaxe;
import me.manu.jaycobpickaxe.utils.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerSpawnerBreakListener implements Listener {

    private final JaycobPickaxe plugin;

    public PlayerSpawnerBreakListener(JaycobPickaxe plugin) {
        this.plugin = plugin;
    }

    public ItemStack getSpawnerItem(Block spawnerBlock) {

        if (spawnerBlock.getType() != Material.SPAWNER) return null;

        CreatureSpawner spawnerBlockState = (CreatureSpawner) spawnerBlock.getState();

        // Проверка на null
        if (spawnerBlockState.getSpawnedType() == null) return null;

        ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
        BlockStateMeta spawnerMeta = (BlockStateMeta) spawnerItem.getItemMeta();
        CreatureSpawner spawnerState = (CreatureSpawner) spawnerMeta.getBlockState();
        spawnerState.setSpawnedType(spawnerBlockState.getSpawnedType());
        spawnerMeta.setBlockState(spawnerState);

        String mobName = plugin.getConfig().getString("mobs." + spawnerBlockState.getSpawnedType().name());

        String displayName = plugin.getConfig().getString("spawner.display-name");
        displayName = displayName.replaceAll("%mobname%", mobName);
        spawnerMeta.setDisplayName(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', displayName)));
        List<String> spawnerLore = plugin.getConfig().getStringList("spawner.lore");
        List<String> coloredLore = new ArrayList<>();
        for (String line : spawnerLore) {
            coloredLore.add(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', line)));
        }
        spawnerMeta.setLore(coloredLore);
        spawnerItem.setItemMeta(spawnerMeta);

        NBTItem nbtItem = new NBTItem(spawnerItem);
        nbtItem.setString("mobType", spawnerBlockState.getSpawnedType().name());
        return nbtItem.getItem();
    }



    @EventHandler
    public void onPlayerSpawnerBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack pickaxe = e.getPlayer().getItemInHand();
        String material = plugin.getConfig().getString("pickaxe.material");

        if (pickaxe != null && pickaxe.getItemMeta() != null) {
            if (e.getBlock().getType() == Material.SPAWNER) {
                if (pickaxe.getType().toString().equals(material) && pickaxe.getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtil.message(ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("pickaxe.display-name"))))) {
                    ItemStack spawnerItem = getSpawnerItem(e.getBlock());
                    if (spawnerItem != null) {
                        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerItem);
                        player.getInventory().removeItem(pickaxe);
                        Sound sound = Sound.valueOf(plugin.getConfig().getString("spawner.sound"));
                        player.playSound(player.getLocation(), sound, 1.0f,1.0f);
                    }
                }
            }
        }
    }



    @EventHandler
    public void onPlayerSpawnerPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();

        if (item.getType() == Material.SPAWNER) {
            NBTItem nbtItem = new NBTItem(item);

            if (nbtItem.hasKey("mobType")) {
                String mobTypeName = nbtItem.getString("mobType");
                EntityType mobType = EntityType.valueOf(mobTypeName);

                Block block = e.getBlock();
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                spawner.setSpawnedType(mobType);
                spawner.update();
            }
        }
    }
}