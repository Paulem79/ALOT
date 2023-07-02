package fr.paulem.alot.item;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.libs.classes.CondensedCraft;
import fr.paulem.alot.libs.classes.CListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public abstract class CustomItem extends CListener {
    public CustomItem(ALOT main) {
        super(main);
    }

    public CondensedCraft init(){
        ItemStack item = item();
        return new CondensedCraft(item, recipe(item));
    }

    public abstract ItemStack item();
    public abstract List<ShapedRecipe> recipe(ItemStack item);
}
