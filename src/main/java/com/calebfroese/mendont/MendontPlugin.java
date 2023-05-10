package com.calebfroese.mendont;

import org.bukkit.plugin.java.JavaPlugin;

public class MendontPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    getServer().getLogger().info("eeeee");
    getServer().getPluginManager().registerEvents(new InventoryMoveItem(this, getServer()), this);
  }
}
