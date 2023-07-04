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

public class SwitchBow extends CustomItem {
    public SwitchBow(ALOT main) {
        super(main);
    }

    @Override
    public ItemStack item() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        ItemMeta im = item.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.DARK_AQUA + "Switch Bow");
        im.setLocalizedName("switch_bow");

        im.setCustomModelData(1);

        im.getPersistentDataContainer().set(new NamespacedKey(main, "switch_bow"), PersistentDataType.INTEGER, 1);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Switch between you and the targeted entity");
        lore.add("");
        lore.add(ChatColor.RED + "- Reduce damages by 50%");
        im.setLore(lore);

        item.setItemMeta(im);

        return item;
    }

    @Override
    public List<ShapedRecipe> recipe(ItemStack item) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(main, "switch_bow"), item);
        recipe.shape(
                "FS ",
                "FES",
                "FS ");
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('F', Material.STRING);
        recipe.setIngredient('E', Material.ENDER_PEARL);


        ShapedRecipe invertRecipe = new ShapedRecipe(new NamespacedKey(main, "switch_bow_invert"), item);
        invertRecipe.shape(
                " SF",
                "SEF",
                " SF");
        invertRecipe.setIngredient('S', Material.STICK);
        invertRecipe.setIngredient('F', Material.STRING);
        invertRecipe.setIngredient('E', Material.ENDER_PEARL);

        return List.of(recipe, invertRecipe);
    }
}
