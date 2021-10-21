package com.calebfroese.dockerbungee;

import static java.util.stream.Collectors.toList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.calebfroese.dockerbungee.DockerBungeePlugin.dockerClient;
import static com.calebfroese.dockerbungee.DockerBungeePlugin.plugin;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.calebfroese.dockerbungee.DockerUtil;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import static com.calebfroese.dockerbungee.DockerBungeePlugin.plugin;
import com.github.dockerjava.api.model.Container;

public class DockerUtil {
    public static List<Container> getRunningContainers() {
        return DockerBungeePlugin.dockerClient.listContainersCmd().exec().stream().filter(
                container -> Arrays.stream(container.getNames()).anyMatch(name -> name.startsWith("/minecraft-")))
                .collect(toList());
    }

    public static List<String> getRunningServers() {
        List<String> servers = new ArrayList<String>();
        for (Container container : getRunningContainers()) {
            for (String name : container.getNames()) {
                if (name.startsWith("/minecraft-")) {
                    servers.add(name.replace("/minecraft-", ""));
                    break;
                } else {
                    plugin.getLogger().info("Not adding" + name);
                }
            }
        }
        return servers;
    }

    public static List<Container> getAllContainers() {
        return DockerBungeePlugin.dockerClient.listContainersCmd().withShowAll(true).exec().stream().filter(
                container -> Arrays.stream(container.getNames()).anyMatch(name -> name.startsWith("/minecraft-")))
                .collect(toList());
    }

    public static Container findContainerByName(String name) {
        for (Container container : DockerUtil.getAllContainers()) {
            if (Arrays.asList(container.getNames()).contains("/" + name)) {
                return container;
            }
        }
        return null;
    }

    public static void bootServer(String serverName, CommandSender commandSender) {
        String dockerContainerName = "minecraft-" + serverName;
        String serverDirectory = Paths.get(plugin.configuration.getServersDir(), serverName).toString();
        Volume volume = new Volume("/server");
        HostConfig config = new HostConfig().withBinds(new Bind(serverDirectory, volume)).withAutoRemove(true).withNetworkMode("minecraft_network");

        String dockerContainerId = null;
        Container existingContainer = DockerUtil.findContainerByName(dockerContainerName);
        if (existingContainer != null) {
            dockerContainerId = DockerUtil.findContainerByName(dockerContainerName).getId();
        }

        if (dockerContainerId == null) {
            // Create the container
            CreateContainerResponse response = dockerClient.createContainerCmd("minecraft-paper-1.17.1-336")
                    .withHostConfig(config).withWorkingDir("/server").withVolumes(volume).withAliases(dockerContainerName).withName(dockerContainerName)
                    .exec();
            dockerContainerId = DockerUtil.findContainerByName(dockerContainerName).getId();
        }

        try {
            dockerClient.startContainerCmd(dockerContainerId).exec();
        } catch (NotModifiedException e) {
            if (commandSender != null) {
                commandSender.sendMessage(new TextComponent("This server is already running! Nothing to do."));
            }
            return;
        }

        // Waits for minecraft to sucessfully return
        Boolean online = false;
        while (online == false) {
            plugin.getLogger().info("Checking");
            Socket s = null;
            try
            {
                s = new Socket("minecraft-" + serverName, 25565);
                plugin.getLogger().info("ONLINE");
                online = true;
            }
            catch (Exception e)
            {
                plugin.getLogger().info("OFFLINE");
                online = false;
            }
            finally
            {
                if(s != null)
                    try {s.close();}
                    catch(Exception e){}
            }
            try { TimeUnit.SECONDS.sleep(1); }
            catch(Exception e) {}
        }
    }
}
