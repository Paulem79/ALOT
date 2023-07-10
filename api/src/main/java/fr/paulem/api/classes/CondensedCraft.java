package fr.paulem.api.classes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class CondensedCraft {
    private final ItemStack itemStack;
    private final List<ShapedRecipe> recipes;

    public CondensedCraft(ItemStack itemStack, ShapedRecipe... recipes){
        this(itemStack, List.of(recipes));
    }

    public CondensedCraft(ItemStack itemStack, List<ShapedRecipe> recipes){
        this.itemStack = itemStack;
        this.recipes = recipes;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<ShapedRecipe> getRecipes() {
        return recipes;
    }
}
