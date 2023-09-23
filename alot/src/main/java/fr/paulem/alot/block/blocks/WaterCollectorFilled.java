package fr.paulem.alot.block.blocks;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.block.MushroomCustomBlock;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WaterCollectorFilled extends MushroomCustomBlock {
    public WaterCollectorFilled(ALOT main) {
        super(main);
    }

    @Override
    public @NotNull ItemStack item() {
        ItemStack item = new ItemStack(Material.BROWN_MUSHROOM_BLOCK);
        ItemMeta im = item.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.DARK_AQUA + "Récupérateur d'eau");
        im.setLocalizedName("water_collector_filled");

        im.setCustomModelData(1);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Collecte l'eau de pluie");
        im.setLore(lore);

        item.setItemMeta(im);
        return item;
    }

    @Override
    public NamespacedKey itemKey() {
        return null;
    }

    @Override
    public @NotNull Set<BlockFace> faces() {
        return Set.of(BlockFace.EAST);
    }
}
