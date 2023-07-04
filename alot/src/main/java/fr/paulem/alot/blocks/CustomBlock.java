package fr.paulem.alot.blocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class CustomBlock {
    public CustomBlock(ItemStack item, Location location){
        World world = location.getWorld();
        assert world != null;
        world.spawn(location, ItemDisplay.class, (itemDisplay -> {
            itemDisplay.setItemStack(item);
            itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
            itemDisplay.setTransformation(new Transformation(new Vector3f(0, 0, 0), new AxisAngle4f(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(1.0002f, 1.0002f, 1.0002f), new AxisAngle4f(0.0f, 0.0f, 0.0f, 1.0f)));
            itemDisplay.setBrightness(new Display.Brightness(15, 15));
        }));
    }
}
