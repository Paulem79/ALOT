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
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class HoloEntity {
    public ALOT main;
    public Entity hologram;
    public @Nullable LivingEntity attached;

    public HoloEntity(ALOT main, Location location, String text) {
        this(main, location, text, null);
    }

    public HoloEntity(ALOT main, Location location, String text, @Nullable LivingEntity attached) {
        this.main = main;
        if (ALOT.isDisplaySupported()) {
            this.hologram = Objects.requireNonNull(location.getWorld()).spawn(location, TextDisplay.class, (textDisplay) -> {
                textDisplay.setText(text);
                textDisplay.setBillboard(Display.Billboard.CENTER);
                textDisplay.getPersistentDataContainer().set(ALOT.hologramKey, PersistentDataType.INTEGER, 1);
                if (attached != null) {
                    textDisplay.getPersistentDataContainer().set(ALOT.healthbarKey, PersistentDataType.STRING, attached.getUniqueId().toString());
                    Transformation transformation = textDisplay.getTransformation();
                    transformation.getTranslation().add(0f, (float) (attached.getHeight() / 2), 0f);
                    textDisplay.setTransformation(transformation);
                }
            });
        } else {
            this.hologram = Objects.requireNonNull(location.getWorld()).spawn(location, ArmorStand.class, (stand) -> {
                stand.setInvisible(true);
                stand.setInvulnerable(true);
                stand.setGravity(false);
                stand.setMarker(true);
                stand.setCustomNameVisible(true);
                stand.setCustomName(text);
                stand.getPersistentDataContainer().set(ALOT.hologramKey, PersistentDataType.INTEGER, 1);
                if (attached != null) {
                    stand.getPersistentDataContainer().set(ALOT.healthbarKey, PersistentDataType.STRING, attached.getUniqueId().toString());
                }
            });
        }
        this.attached = attached;
    }

    public BukkitTask deleteAfter(long ticks) {
        return deleteAfter(ticks, null);
    }

    public BukkitTask deleteAfter(long ticks, @Nullable BukkitTask task) {
        Runnable runnable = () -> {
            hologram.remove();
            if (task != null) task.cancel();
        };
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(main, ticks);
    }
}