package fr.paulem.alot.listeners;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CListener;
import fr.paulem.api.functions.LibDamage;
import fr.paulem.api.functions.LibRadius;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class ListenerItems extends CListener {
    public static HashMap<ItemStack, BukkitTask> itemSwapTasks = new HashMap<>();
    public static HashMap<ItemDisplay, BukkitTask> itemInBackTasks = new HashMap<>();

    public ListenerItems(ALOT main) {
        super(main);
    }

    @EventHandler
    public void onItemSwap(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItem(e.getNewSlot());
        if (item == null) return;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null || item.getType().getMaxDurability() == 0) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(LibDamage.getDurability(item) + "/" + item.getType().getMaxDurability()));
        if (itemSwapTasks.containsKey(item)) {
            itemSwapTasks.get(item).cancel();
            itemSwapTasks.remove(item);
        }
        itemSwapTasks.put(item, new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent());
            }
        }.runTaskLater(main, 30L));
    }

    @EventHandler
    public void itemOnBack(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        ItemStack previous = player.getInventory().getItem(e.getPreviousSlot());
        if (previous == null || previous.getType().getMaxDurability() == 0) return;
        Optional<ItemDisplay> it = player.getPassengers().stream().filter(ent -> ent instanceof ItemDisplay && ent.getPersistentDataContainer().has(new NamespacedKey(main, "itemOnBack"), PersistentDataType.INTEGER)).map(ent -> (ItemDisplay) ent).findFirst();
        if (it.isPresent()) {
            it.get().setItemStack(previous);
            return;
        }


        player.addPassenger(player.getWorld().spawn(player.getEyeLocation(), ItemDisplay.class, (itemDisplay) -> {
            if (ALOT.bukkitVersion.minor() >= 19) player.hideEntity(main, itemDisplay);

            itemDisplay.setItemStack(previous);

            itemDisplay.getPersistentDataContainer().set(new NamespacedKey(main, "itemOnBack"), PersistentDataType.INTEGER, 1);
            itemDisplay.getPersistentDataContainer().set(new NamespacedKey(main, "attachedPlayer"), PersistentDataType.STRING, String.valueOf(player.getUniqueId()));

            Transformation transformation = itemDisplay.getTransformation();
            transformation.getTranslation().set(new Vector3f(.1f, -.1f, .2f));
            itemDisplay.setTransformation(transformation);

            itemDisplay.setRotation(player.getLocation().getYaw() + 180f, 0f);

            itemInBackTasks.put(itemDisplay, new BukkitRunnable() {
                @Override
                public void run() {
                    if (itemDisplay.isDead()) cancel();

                    itemDisplay.setRotation(player.getLocation().getYaw() + 180f, 0f);
                }
            }.runTaskTimer(main, 1L, 1L));
        }));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Optional<ItemDisplay> itemDisplay = LibRadius.getEntitiesInAllWorlds(ent -> ent instanceof ItemDisplay && ent.getPersistentDataContainer().has(new NamespacedKey(main, "itemOnBack"), PersistentDataType.INTEGER) && ent.getPersistentDataContainer().has(new NamespacedKey(main, "attachedPlayer"), PersistentDataType.STRING) && Objects.equals(ent.getPersistentDataContainer().get(new NamespacedKey(main, "attachedPlayer"), PersistentDataType.STRING), String.valueOf(e.getPlayer().getUniqueId())), it -> (ItemDisplay) it).findFirst();
        if (itemDisplay.isPresent() && itemInBackTasks.containsKey(itemDisplay.get())) {
            itemInBackTasks.get(itemDisplay.get()).cancel();
            itemDisplay.get().remove();
            itemInBackTasks.remove(itemDisplay.get());
        }
    }
}
