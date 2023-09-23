package fr.paulem.alot.block;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CMain;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class MushroomCustomBlock extends CMain {
    public MushroomCustomBlock(ALOT main) {
        super(main);
    }

    public MushroomCustomBlock init() {
        return this;
    }

    @NotNull
    public abstract ItemStack item();

    @Nullable
    public abstract NamespacedKey itemKey();

    @NotNull
    public abstract Set<BlockFace> faces();
}
