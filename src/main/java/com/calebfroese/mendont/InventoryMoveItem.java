package com.calebfroese.mendont;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class InventoryMoveItem implements Listener {
  Server s;
  MendontPlugin plugin;

  public InventoryMoveItem(MendontPlugin plugin, Server server) {
    this.plugin = plugin;
    this.s = server;
  }

  @EventHandler
  public void onTridentAnvilPrepare(PrepareAnvilEvent event) {
    if (!(event.getInventory() instanceof AnvilInventory)) {
      return;
    }
    AnvilInventory inventory = (AnvilInventory) event.getInventory();

    if (inventory.getItem(0) == null || inventory.getItem(1) == null) {
      return;
    }

    // Tridents being repairable by prismarine shards
    if (inventory.getItem(0).getType() == Material.TRIDENT
        && inventory.getItem(1).getType() == Material.PRISMARINE_SHARD) {
      ItemStack original = inventory.getItem(0);
      ItemStack updated = original.clone();
      Damageable itemMeta = (Damageable) original.getItemMeta();
      itemMeta.setDamage(0);
      updated.setItemMeta(itemMeta);
      event.setResult(updated);
    }
  }

  @EventHandler
  public void onTridentAnvilCollect(InventoryClickEvent event) {
    if (!(event.getInventory() instanceof AnvilInventory)) {
      return;
    }
    AnvilInventory inventory = (AnvilInventory) event.getInventory();

    if (inventory.getItem(0) == null || inventory.getItem(1) == null) {
      return;
    }

    // Must be a trident in the anvil
    if (inventory.getItem(0).getType() != Material.TRIDENT) {
      return;
    }

    // Must be clicking the "collect" slot of the anvil
    if (event.getSlot() != 2) {
      return;
    }

    ItemStack trident = inventory.getItem(0).clone();
    Damageable meta = (Damageable) trident.getItemMeta();
    meta.setDamage(0);
    trident.setItemMeta(meta);
    event.getWhoClicked().getInventory().addItem(trident);
    inventory.clear();
  }

  @EventHandler
  public void on(InventoryClickEvent event) {
    if (!(event.getInventory() instanceof AnvilInventory)) {
      return;
    }

    if (event.getWhoClicked() instanceof Player) {
      ItemStack item = event.getCurrentItem();
      if (item == null || !(item.getItemMeta() instanceof Repairable)) {
        return;
      }

      Repairable repairable = (Repairable) item.getItemMeta();
      if (repairable.getRepairCost() > 15) {
        repairable.setRepairCost(15);
        item.setItemMeta((ItemMeta) repairable);
        event.setCurrentItem(item);
      }
    }

  }
}
