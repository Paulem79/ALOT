package fr.paulem.alot.blocks;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Objects;

public class CustomBlock {
    public CustomBlock(JavaPlugin plugin, ItemStack itemStack, Location location) {
        Objects.requireNonNull(location.getWorld()).spawn(location, ItemDisplay.class, (itemDisplay -> {
            itemDisplay.setItemStack(itemStack);
            itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
            itemDisplay.setTransformation(new Transformation(new Vector3f(0, 0, 0), new AxisAngle4f(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(1.0002f, 1.0002f, 1.0002f), new AxisAngle4f(0.0f, 0.0f, 0.0f, 1.0f)));
            itemDisplay.setBrightness(new Display.Brightness(15, 15));
            new BukkitRunnable() {
                @Override
                public void run() {
                    itemDisplay.setBrightness(new Display.Brightness(itemDisplay.getLocation().getBlock().getLightFromSky(), itemDisplay.getLocation().getBlock().getLightFromSky()));
                }
            }.runTaskTimer(plugin, 1L, 20L);
        }));
    }
}
