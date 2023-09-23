package fr.paulem.alot.block;

import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class CondensedBlock {
    private final ItemStack associedItem;
    private final Set<BlockFace> faces;

    public CondensedBlock(ItemStack associedItem, BlockFace... faces) {
        this(associedItem, Set.of(faces));
    }

    public CondensedBlock(ItemStack associedItem, Set<BlockFace> faces) {
        this.associedItem = associedItem;
        this.faces = faces;
    }

    public ItemStack getAssociedItem() {
        return associedItem;
    }

    public Set<BlockFace> getFaces() {
        return faces;
    }
}
