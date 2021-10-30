package com.calebfroese.dockerbungee;

import java.util.concurrent.TimeUnit;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.api.destinations.utils.IDestination;
import de.codingair.warpsystem.api.events.AsyncPlayerTeleportEvent;

public class DockerSpigotPlugin extends JavaPlugin implements Listener
, PluginMessageListener 
{
  String serverName = null;

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
    // Register events for communication with the Bungeecord plugin
    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "dockerbungee:main");
    this.getServer().getMessenger().registerIncomingPluginChannel(this, "dockerbungee:main", this);

  }

  @Override
  public void onDisable() {
    // make sure to unregister the registered channels in case of a reload
    this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
  }

  @Override
  public void onPluginMessageReceived(String channel, Player player, byte[] message) {
    getLogger().info("onPluginMessageReceived");
    if (!channel.equals("dockerbungee:main")) {
      getLogger().info("REreceived a mesage for some other chabnnel" + channel);
      return;
    }
    ByteArrayDataInput in = ByteStreams.newDataInput(message);
    in.readUTF();
    String subchannel = in.readUTF();
    getLogger().info("Subchannel" + subchannel);
    if (subchannel.equals("ServerOnline")) {
      this.serverName = in.readUTF();
      getLogger().info("A NEW SERVER IS ONLINE! WOOHOO:: " + this.serverName);
    }
  }

  @EventHandler
  public void onWarp(AsyncPlayerTeleportEvent e) {
    getLogger().info("AsyncPlayerTeleportEvent AsyncPlayerTeleportEvent onWarp");
    // e.setCancelled(true);
    this.requestTeleportToServer("another");
    
    while (!this.serverName.equalsIgnoreCase("another")) {
      try {
        TimeUnit.SECONDS.sleep(1);
        getLogger().info("AsyncPlayerTeleportEvent AsyncPlayerTeleportEvent onWarpnope......");
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        getLogger().info("AsyncPlayerTeleportEvent AsyncPlayerTeleportEvent onWarperror......");
        e1.printStackTrace();
      }
    }
    getLogger().info("AsyncPlayerTeleportEvent AsyncPlayerTeleportEvent onWarp CONTINNUNIGINEGINEGIEN");
    // IDestination destination = e.getOptions().getDestination();
    // Location location = destination.buildLocation();
    // if (location != null && inSpecificArea(location)) {
    // e.setCancelled(true);
    // e.getPlayer().sendMessage("§cWarps §8» §7This area is §ccurrently
    // unavailable§7.");
    // }
  }

  @EventHandler
  public void onTeleport(AsyncPlayerTeleportEvent e) {
    getLogger().info("AsyncPlayerTeleportEvent AsyncPlayerTeleportEvent onTeleport");
    // e.setCancelled(true);
    this.requestTeleportToServer("another");
    
    while (!this.serverName.equalsIgnoreCase("another")) {
      try {
        TimeUnit.SECONDS.sleep(1);
        getLogger().info("AsyncPlayerTeleportEvent AsyncPlayerTeleportEvent  onTeleportnope......");
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        getLogger().info("AsyncPlayerTeleportEvent AsyncPlayerTeleportEvent onTeleporterror......");
        e1.printStackTrace();
      }
    }
    getLogger().info("AsyncPlayerTeleportEvent AsyncPlayerTeleportEvent onTeleport CONTINNUNIGINEGINEGIEN");
    // this.requestTeleportToServer("another");
    // IDestination destination = e.getOptions().getDestination();
    // String targetServer = destination.getTargetServer();

    // boolean onSameServer = targetServer == null;
    // if (onSameServer) return;

    // if (isOffline(targetServer)) startSync(targetServer);
  }

  private void requestTeleportToServer(String serverName) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("RequestTeleport");
    out.writeUTF(serverName);

    // If you don't care about the player
    Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

    player.sendPluginMessage(this, "dockerbungee:main", out.toByteArray());
  }
}
