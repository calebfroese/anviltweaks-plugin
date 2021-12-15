package com.calebfroese.mendont;

import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryMoveItem implements Listener {
  Server s;

  public InventoryMoveItem(Server server) {
    this.s = server;
  }

  @EventHandler
  public void onPrepareAnvilEvent(PrepareAnvilEvent event) {
    this.s.getLogger().info("Event called!");
    if (event.getResult().containsEnchantment(Enchantment.MENDING)) {
      this.s.getLogger().info("REPLACING!");
      ItemStack result = event.getResult();
      result.removeEnchantment(Enchantment.MENDING);
      event.setResult(result);
    }
    // if (event.getItem().containsEnchantment(Enchantment.MENDING)) {
    //   event.getItem().removeEnchantment(Enchantment.MENDING);
    // }
    // event.
  }
}
