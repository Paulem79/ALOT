package fr.paulem.alot.item.items;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.item.CustomItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class TNTLandMine extends CustomItem {

    public TNTLandMine(ALOT main) {
        super(main);
    }

    @Override
    public ItemStack item() {
        ItemStack item = new ItemStack(Material.TNT, 1);
        ItemMeta im = item.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.RED + "Landmine");
        im.setLocalizedName("landmine");

        im.getPersistentDataContainer().set(new NamespacedKey(main, "landmine"), PersistentDataType.INTEGER, 1);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Place a landmine");
        lore.add("");
        lore.add(ChatColor.RED + "- Really dangerous");
        im.setLore(lore);

        item.setItemMeta(im);

        return item;
    }

    @Override
    public List<ShapedRecipe> recipe(ItemStack item) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(main, "landmine"), item);
        recipe.shape(
                " P ",
                "TTT",
                "RBR");
        recipe.setIngredient('P', Material.STONE_PRESSURE_PLATE);
        recipe.setIngredient('T', Material.TNT);
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('B', Material.REDSTONE_BLOCK);

        return List.of(recipe);
    }
}
