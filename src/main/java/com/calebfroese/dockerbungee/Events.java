package com.calebfroese.dockerbungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import static com.calebfroese.dockerbungee.DockerBungeePlugin.plugin;

import java.util.concurrent.TimeUnit;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.api.destinations.utils.IDestination;
import de.codingair.warpsystem.api.events.PlayerPreTeleportEvent;

public class Events implements Listener {
    
    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        String serverName = event.getRequest().getTarget().getName();
        if (DockerUtil.getRunningServers().contains(serverName)) {
            return;
        }
        
        // Boot the server
        plugin.getLogger().info(event.getPlayer().getName() + " attemped to join offline server " + serverName + ". Booting it...");
        DockerUtil.bootServer(serverName, null);
    }

    @EventHandler
    public void onWarp(PlayerPreTeleportEvent e) {
        plugin.getLogger().info("PlayerPreTeleportEvent");
        IDestination destination = e.getOptions().getDestination();
        Location location = destination.buildLocation();
        // location.
        String serverName = "another"; //event.getRequest().getTarget().getName();
        if (DockerUtil.getRunningServers().contains(serverName)) {
            plugin.getLogger().info("Online");
            return;
        }
        plugin.getLogger().info("Booting");
        DockerUtil.bootServer(serverName, null);
        plugin.getLogger().info("Done");
    }
}
