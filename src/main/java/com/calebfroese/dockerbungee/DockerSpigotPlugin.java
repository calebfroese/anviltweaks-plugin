package com.calebfroese.dockerbungee;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import de.codingair.warpsystem.api.events.AsyncPlayerTeleportEvent;

public class DockerSpigotPlugin extends JavaPlugin implements Listener, PluginMessageListener {
  ArrayList<String> onlineServerNames = new ArrayList<String>();
  // Whether we are currently pending a response from Bungeecord about the online
  // servers
  // This avoids having multiple players join in quick succession causing multiple
  // requests which could finish at different times and clear onlineServerNames
  // We save the player name, as bungeecord messages are sent along with an online
  // player. If that playere disconnects, clear it, as we will have lost the connection.
  String isPendingOnlineServerRequest = null;

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
    if (!channel.equals("dockerbungee:main")) {
      return;
    }
    ByteArrayDataInput in = ByteStreams.newDataInput(message);
    in.readUTF();
    String subchannel = in.readUTF();
    if (subchannel.equals("ServerOnline")) {
      // The online servers string arrives as a comma separated list, e.g.
      // "server1,server2,server3"
      String onlineServers = in.readUTF();
      getLogger().info("Online servers: " + onlineServers);
      for (String serverName : onlineServers.split(",")) {
        this.onlineServerNames.add(serverName);
      }
      this.isPendingOnlineServerRequest = null;
    }
  }

  @EventHandler
  public void onWarp(AsyncPlayerTeleportEvent e) {
    if (e.getOptions().getDestination().getTargetServer() == null) {
      getLogger().info("Target server is null, unable to teleport");
      return;
    }
    getLogger().info("Requesting to warp " + e.getPlayer().getName() + " to "
        + e.getOptions().getDestination().getTargetServer() + "...");
    waitForServer(e.getOptions().getDestination().getTargetServer());
    getLogger().info("Sending warp " + e.getPlayer().getName() + " to "
        + e.getOptions().getDestination().getTargetServer() + "...");
  }

  // @EventHandler
  // public void onTeleport(AsyncPlayerTeleportEvent e) {
  //   if (e.getOptions().getDestination().getTargetServer() == null) {
  //     getLogger().info("Target server is null, unable to teleport");
  //     return;
  //   }
  //   getLogger().info("Requesting to teleport " + e.getPlayer().getName() + " to "
  //       + e.getOptions().getDestination().getTargetServer() + "...");
  //   waitForServer(e.getOptions().getDestination().getTargetServer());
  //   getLogger().info("LETS GO");
  // }

  @EventHandler
  public void onPlayerDisconnect(PlayerQuitEvent e) {
    getLogger().info("Player has disconnected!");
    if (this.isPendingOnlineServerRequest != null && this.isPendingOnlineServerRequest == e.getPlayer().getName()) {
      getLogger().info("It was hee!");
      this.isPendingOnlineServerRequest = null;
    }
  }

  private void waitForServer(String serverName) {
    // We have to clear and request the teleport every time as we don't know for
    // sure if a server goes down.
    this.bootAndJoinServerRequest(serverName);

    while (!this.onlineServerNames.contains(serverName)) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    }
  }

  private void bootAndJoinServerRequest(String serverName) {
    if (this.isPendingOnlineServerRequest != null) {
      getLogger().info("Not sending teleport request as there is one pending!");
      return;
    }
    getLogger().info("Sending teleport request!");

    // If you don't care about the player
    Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
    this.isPendingOnlineServerRequest = player.getName();
    this.onlineServerNames.clear();
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("BootAndJoinServer");
    out.writeUTF(serverName);

    player.sendPluginMessage(this, "dockerbungee:main", out.toByteArray());
  }
}
