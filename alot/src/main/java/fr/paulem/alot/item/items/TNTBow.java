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
public class TNTBow extends CustomItem {
    public TNTBow(ALOT main) {
        super(main);
    }

    public ItemStack item() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        ItemMeta im = item.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.RED + "TNT" + ChatColor.WHITE + " bow");
        im.setLocalizedName("tnt_bow");

        im.setCustomModelData(2);

        im.getPersistentDataContainer().set(new NamespacedKey(main, "tnt_bow"), PersistentDataType.INTEGER, 1);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Make the targeted entity explode");
        lore.add("");
        lore.add(ChatColor.GREEN + "- The explosion power can be increased");
        lore.add(ChatColor.RED + "- Broke after 5 usages (without unbreaking)");
        im.setLore(lore);

        item.setItemMeta(im);

        return item;
    }

    public List<ShapedRecipe> recipe(ItemStack item) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(main, "tnt_bow"), item);
        recipe.shape(
                "FST",
                "FDS",
                "FST");
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('F', Material.STRING);
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('T', Material.TNT);


        ShapedRecipe invertRecipe = new ShapedRecipe(new NamespacedKey(main, "tnt_bow_invert"), item);
        invertRecipe.shape(
                "TSF",
                "SDF",
                "TSF");
        invertRecipe.setIngredient('S', Material.STICK); //new RecipeChoice.ExactChoice(main.registeredItems.get("switch_bow"))
        invertRecipe.setIngredient('F', Material.STRING);
        invertRecipe.setIngredient('D', Material.DIAMOND);
        invertRecipe.setIngredient('T', Material.TNT);

        return List.of(recipe, invertRecipe);
    }
}
