package com.calebfroese.anviltweaks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

/**
 * This prevents the "Too Expensive" from items in the anvil by capping the
 * repair cost
 */
public class PreventTooExpensive implements Listener {
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
