package me.manu.jaycobpickaxe;

import me.manu.jaycobpickaxe.commands.GiveCommand;
import me.manu.jaycobpickaxe.listeners.PlayerSpawnerBreakListener;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class JaycobPickaxe extends JavaPlugin {

    private File messagesFile;
    private FileConfiguration messagesConfigFile;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createCustomConfig();
        getCommand("jp").setExecutor(new GiveCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerSpawnerBreakListener(this),this);
        getLogger().info("Лучшие бесплатные плагины тут 👇");
        getLogger().info("https://discord.gg/JXFbN5NMDX                   ");
    }

    private void createCustomConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfigFile = new YamlConfiguration();
        try {
            messagesConfigFile.load(messagesFile);
            YamlConfiguration.loadConfiguration(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    public FileConfiguration getMessagesConfig() {
        return this.messagesConfigFile;
    }

    public void reloadMessagesConfig() {
        messagesConfigFile = YamlConfiguration.loadConfiguration(messagesFile);
        try {
            messagesConfigFile.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
