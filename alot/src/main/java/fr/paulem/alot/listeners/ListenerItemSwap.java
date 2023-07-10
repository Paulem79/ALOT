package fr.paulem.alot.listeners;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CListener;
import fr.paulem.api.functions.LibDamage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ListenerItemSwap extends CListener {
    public ListenerItemSwap(ALOT main) {
        super(main);
    }

    @EventHandler
    public void onItemSwap(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItem(e.getNewSlot());
        if (item == null) return;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;
        if (!(itemMeta instanceof Damageable damageable) || damageable.isUnbreakable() || item.getType().getMaxDurability() == 0)
            return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(LibDamage.getDurability(item) + "/" + item.getType().getMaxDurability()));
    }
}
