package com.calebfroese.dockerbungee;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.calebfroese.dockerbungee.commands.BootCommand;
import com.calebfroese.dockerbungee.commands.UnbootCommand;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;

public class DockerBungeePlugin extends Plugin {
    public ConfigurationManager configuration;
    public static DockerBungeePlugin plugin;
    public static DockerClient dockerClient;

    @Override
    public void onEnable() {
        plugin = this;
        configuration = new ConfigurationManager();
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder().dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig()).build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
        getLogger().info(dockerClient.toString());

        List<Container> containers = dockerClient.listContainersCmd().exec();
        for (Container container : containers) {
            getLogger().info(container.toString());
        }

        configuration.createConfiguration();

        // Register Commands
        getProxy().getPluginManager().registerCommand(this, new BootCommand());
        getProxy().getPluginManager().registerCommand(this, new UnbootCommand());
        getProxy().getPluginManager().registerListener(this, new Events());

        // Build the initial list of all currently running Docker minecraft servers
        // this.refreshServers();
    }

    // public void refreshServers() {
    //     for (Container runningContainer : DockerUtil.getRunningContainers()) {
    //         getLogger().info(runningContainer.getId());
    //     }
    // }
}
