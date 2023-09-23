package fr.paulem.alot.block;

import fr.paulem.alot.ALOT;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class ItemDisplayCustomBlock {
    public ItemDisplayCustomBlock(ItemStack itemStack, Location location, @Nullable Block block) {
        location = location.getBlock().getLocation();
        World world = location.getWorld();
        assert world != null;
        if (block != null) location.getBlock().setBlockData(block.getBlockData());
        world.spawn(location.getBlock().getLocation().clone().add(.5, .5, .5), ItemDisplay.class, (itemDisplay -> {
            itemDisplay.setItemStack(itemStack);
            itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
            itemDisplay.setTransformation(new Transformation(itemDisplay.getTransformation().getTranslation(), new AxisAngle4f(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(1.0002f, 1.0002f, 1.0002f), new AxisAngle4f(0.0f, 0.0f, 0.0f, 1.0f)));
            itemDisplay.setBrightness(new Display.Brightness(15, 15));
            itemDisplay.getPersistentDataContainer().set(ALOT.customBlockKey, PersistentDataType.INTEGER, 1);
        }));
    }
}
