package fr.paulem.alot.gui;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIAddedItems extends CListener {
    public static Inventory creativeTabInventory;

    public GUIAddedItems(ALOT main) {
        super(main);
    }

    public static void setCreativeTabInventory(ALOT main){
        creativeTabInventory = Bukkit.createInventory(null, 54, "Creative TAB");

        for(ItemStack item : main.registeredItems.values()){
            creativeTabInventory.addItem(item);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        if (e.getInventory().equals(creativeTabInventory)) e.setCancelled(true);
        if (e.getInventory() == e.getWhoClicked().getInventory() && e.getInventory() != creativeTabInventory)
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != creativeTabInventory) return;
        if (e.getClickedInventory() == e.getWhoClicked().getInventory() || e.getClickedInventory() != creativeTabInventory) {
            e.setCancelled(true);
            return;
        }

        ItemStack clickedItem = e.getCurrentItem();
        if (!main.registeredItems.containsValue(clickedItem)) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);

        // verify current item is not null
        if (clickedItem == null) return;

        e.getWhoClicked().getInventory().addItem(clickedItem);
    }
}
