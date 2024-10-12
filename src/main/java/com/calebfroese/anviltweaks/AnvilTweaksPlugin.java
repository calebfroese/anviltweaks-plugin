package com.calebfroese.anviltweaks;

import org.bukkit.plugin.java.JavaPlugin;

public class AnvilTweaksPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new PreventTooExpensive(), this);
    getServer().getPluginManager().registerEvents(new TridentRepair(), this);
  }
}
