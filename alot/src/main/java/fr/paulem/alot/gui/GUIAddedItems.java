package fr.paulem.alot.gui;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.libs.classes.CListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(creativeTabInventory)) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(creativeTabInventory)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        p.getInventory().addItem(clickedItem);
    }
}
