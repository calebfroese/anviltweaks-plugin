package com.calebfroese.dockerbungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import java.nio.file.Files;
import java.util.List;
import static com.calebfroese.dockerbungee.DockerBungeePlugin.plugin;

public class ConfigurationManager {
    private Configuration getConfiguration() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return new Configuration();
        }
    }

    public void createConfiguration() {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = plugin.getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getServers() {
        return getConfiguration().getStringList("servers");
    }

    public String getServersDir() {
        return getConfiguration().getString("directory");
    }
}
