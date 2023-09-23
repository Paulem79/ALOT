package fr.paulem.nms_v18_2;

import fr.paulem.api.classes.CPluginListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Listeners_1_18_2 extends CPluginListener {
    public Listeners_1_18_2(JavaPlugin main) {
        super(main);
    }

    @EventHandler
    public void onNpcHit(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof CraftPlayer craftPlayer))
            return; // Well, a lot of "if" because I'm scared of combining them x)
        if (craftPlayer.getPlayer() == null) return;
        if (!craftPlayer.getPlayer().getPersistentDataContainer().has(new NamespacedKey(main, "fakePlayer"), PersistentDataType.INTEGER))
            return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (craftPlayer.getHealth() == 0) deleteEntity(craftPlayer.getHandle());
            }
        }.runTask(main);
    }

    public void deleteEntity(ServerPlayer toRemove) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            deleteEntity(p, toRemove);
        }
    }

    public void deleteEntity(Player p, ServerPlayer toRemove) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
        toRemove.kill();
        connection.send(new ClientboundRemoveEntitiesPacket(toRemove.getId()));
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_LATENCY, toRemove));
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, toRemove));
    }
}
