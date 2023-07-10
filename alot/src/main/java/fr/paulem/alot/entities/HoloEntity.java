package fr.paulem.alot.entities;

import fr.paulem.alot.ALOT;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static fr.paulem.alot.ALOT.bukkitVersion;

public class HoloEntity {
    public ALOT main;
    public Entity hologram;
    public @Nullable LivingEntity attached;

    public HoloEntity(ALOT main, Location location, String text) {
        this(main, location, text, null);
    }

    public HoloEntity(ALOT main, Location location, String text, @Nullable LivingEntity attached) {
        this.main = main;
        if (bukkitVersion.minor() > 19 || (bukkitVersion.revision() == 4 && bukkitVersion.major() == 19)) {
            this.hologram = Objects.requireNonNull(location.getWorld()).spawn(location, TextDisplay.class, (textDisplay) -> {
                textDisplay.setText(text);
                textDisplay.setBillboard(Display.Billboard.CENTER);
                textDisplay.getPersistentDataContainer().set(main.hologramKey, PersistentDataType.INTEGER, 1);
                if (attached != null)
                    textDisplay.getPersistentDataContainer().set(main.healthbarKey, PersistentDataType.STRING, attached.getUniqueId().toString());
            });
        } else {
            this.hologram = Objects.requireNonNull(location.getWorld()).spawn(location, ArmorStand.class, (stand) -> {
                stand.setInvisible(true);
                stand.setInvulnerable(true);
                stand.setGravity(false);
                stand.setMarker(true);
                stand.setCustomNameVisible(true);
                stand.setCustomName(text);
                stand.getPersistentDataContainer().set(main.hologramKey, PersistentDataType.INTEGER, 1);
                if (attached != null)
                    stand.getPersistentDataContainer().set(main.healthbarKey, PersistentDataType.STRING, attached.getUniqueId().toString());
            });
        }
        this.attached = attached;
    }

    public void deleteAfter(long ticks) {
        deleteAfter(ticks, null);
    }

    public void deleteAfter(long ticks, @Nullable BukkitTask task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                hologram.remove();
                if (task != null) task.cancel();
            }
        }.runTaskLater(main, ticks);
    }
}