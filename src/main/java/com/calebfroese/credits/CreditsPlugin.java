package com.calebfroese.credits;

import org.bukkit.plugin.java.JavaPlugin;

public class CreditsPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    this.getCommand("credits").setExecutor(new CreditsCommand());
  }
}
