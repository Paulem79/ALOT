package fr.paulem.alot.item;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CMain;
import fr.paulem.alot.item.items.SwitchBow;
import fr.paulem.alot.item.items.TNTBow;
import fr.paulem.alot.item.items.TNTLandMine;
import fr.paulem.api.classes.CondensedCraft;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.stream.Stream;

public class ItemHandler extends CMain {
    public ItemHandler(ALOT main) {
        super(main);
        for(CondensedCraft condensedCraft : init()){
            for(ShapedRecipe recipe : condensedCraft.getRecipes()) {
                if (main.getServer().getRecipesFor(condensedCraft.getItemStack()).contains(recipe)) {
                    if (!recipe.getKey().getKey().contains("invert"))
                        main.registeredItems.putIfAbsent(recipe.getKey(), condensedCraft.getItemStack());
                    continue;
                }
                if (ALOT.bukkitVersion.minor() >= 16) {
                    main.getServer().removeRecipe(recipe.getKey());
                }
                main.getServer().addRecipe(recipe);
                if (!recipe.getKey().getKey().contains("invert"))
                    main.registeredItems.putIfAbsent(recipe.getKey(), condensedCraft.getItemStack());
            }
            main.registeredRecipes.add(new CondensedCraft(condensedCraft.getItemStack(), condensedCraft.getRecipes().stream().filter(r -> !r.getKey().getKey().contains("invert")).toList()));
        }
    }

    private List<CondensedCraft> init() {
        return Stream.of(new TNTBow(main), new SwitchBow(main), new TNTLandMine(main)).map(CustomItem::init).toList();
    }
}
