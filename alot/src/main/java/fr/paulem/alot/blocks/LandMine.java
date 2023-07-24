package fr.paulem.alot.blocks;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.item.items.TNTLandMine;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class LandMine {
    public final ALOT main;

    public ItemDisplay landMineEntity;
    public final Location locationLandMine;

    public LandMine(ALOT main, Location location, boolean passInit) {
        this.main = main;
        locationLandMine = location;
        World world = location.getWorld();
        if (world == null) throw new NullPointerException("world is null");

        landMineEntity = world.spawn(location, ItemDisplay.class, (itemDisplay -> {
            itemDisplay.setItemStack(new TNTLandMine(main).item());
            itemDisplay.setInvulnerable(true);
            itemDisplay.setPersistent(true);
            itemDisplay.getPersistentDataContainer().set(new NamespacedKey(this.main, "landmine"), PersistentDataType.INTEGER, 1);

            if (passInit) LandMine.this.main.landMines.add(LandMine.this);
            else {
                BukkitTask initLandMine = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Objects.requireNonNull(LandMine.this.getLocationLandMine().getWorld()).playSound(LandMine.this.getLocationLandMine(), Sound.ENTITY_CREEPER_PRIMED, 1F, 1F);
                    }
                }.runTaskTimer(this.main, 20L, 20L);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        initLandMine.cancel();
                    }
                }.runTaskLater(this.main, 20L * 2);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Objects.requireNonNull(LandMine.this.getLocationLandMine().getWorld()).playSound(LandMine.this.getLocationLandMine(), Sound.BLOCK_BELL_USE, 1F, 1F);
                        LandMine.this.main.landMines.add(LandMine.this);
                    }
                }.runTaskLater(this.main, 20L * 3);
            }
        }));
    }

    public static LandMine newLandMine(ALOT main, ItemDisplay itemDisplay) {
        return newLandMine(main, itemDisplay, true);
    }

    public static LandMine newLandMine(ALOT main, ItemDisplay itemDisplay, boolean passInit) {
        itemDisplay.remove();
        return new LandMine(main, itemDisplay.getLocation(), passInit);
    }

    public void explose() {
        getLandMineEntity().remove();
        Objects.requireNonNull(getLocationLandMine().getWorld()).createExplosion(getLocationLandMine(), 8F);
        main.landMines.remove(this);
    }

    public Location getLocationLandMine() {
        return locationLandMine;
    }

    public ItemDisplay getLandMineEntity() {
        return landMineEntity;
    }
}
