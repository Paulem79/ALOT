package fr.paulem.alot.listeners;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

import static fr.paulem.alot.listeners.ListenerPloof.Ploof;
import static fr.paulem.api.functions.LibRadius.isTherePlayerNearby;

public class ListenerBlood extends CListener {

    public ListenerBlood(ALOT main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageEvent e){
        if(!(e.getEntity() instanceof LivingEntity entity) || !isTherePlayerNearby(e.getEntity())) return;
        if(ALOT.bukkitVersion.minor() < 13) return;

        double maxHealthAttr = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / 2;

        if(maxHealthAttr > 100) maxHealthAttr /= 2;

        double maxHealth = maxHealthAttr;

        new BukkitRunnable(){
            @Override
            public void run() {
                double multiplier = maxHealth;
                if(entity.isDead()) {
                    multiplier = maxHealth * 2;
                    Location location = entity.getLocation();
                    if(location.getBlock().getRelative(BlockFace.DOWN).isLiquid()) location = entity.getLocation().subtract(0, 1, 0);
                    if (location.getBlock().isLiquid()) {
                        if (List.of(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION).contains(e.getCause()))
                            Ploof(main, entity, location, 3);
                        else Ploof(main, entity, location, 1);
                    }
                }

                BlockData redstoneBlockData = Bukkit.createBlockData(Material.REDSTONE_BLOCK);
                entity.getWorld().spawnParticle(Particle.BLOCK_DUST, e.getEntity().getLocation().add(0, 1, 0), (int) ((int) e.getFinalDamage()*multiplier), .2, .2, .2, redstoneBlockData);
            }
        }.runTask(main);
    }
}
