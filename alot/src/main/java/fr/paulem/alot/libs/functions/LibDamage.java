package fr.paulem.alot.libs.functions;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.libs.classes.CMain;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class LibDamage extends CMain {
    public LibDamage(ALOT main) {
        super(main);
    }

    public int dealDamage(ItemStack item, int damage){
        return dealDamage(item, damage, true);
    }

    public int dealDamage(Entity entity, ItemStack item, int damage){
        if(entity instanceof Player player){
            if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return 0;
        }

        return dealDamage(item, damage, true);
    }

    public int dealDamage(ItemStack item, int damage, boolean considerUnbreaking){
        if(ALOT.bukkitVersion.minor() < 17) return 0;
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta instanceof Damageable damageable) {
            int unbreakingLevel = considerUnbreaking ? item.getEnchantmentLevel(Enchantment.DURABILITY)+1 : 1;
            int toDeal = damageable.getDamage() + (damage/unbreakingLevel);
            damageable.setDamage(toDeal); // Durabilité max - durabilité mise
            item.setItemMeta(damageable);
            return toDeal;
        }

        return 0;
    }

    public int setDamage(Entity entity, ItemStack item, int damage) {
        if(entity instanceof Player player){
            if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return 0;
        }

        return setDamage(item, damage);
    }

    public int setDamage(ItemStack item, int damage) {
        if(ALOT.bukkitVersion.minor() < 17) return 0;
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta instanceof Damageable damageable) {
            int maxDurability = item.getType().getMaxDurability();
            int toDeal = Math.max(0, maxDurability - damage);
            damageable.setDamage(toDeal);
            item.setItemMeta(damageable);
            return toDeal;
        }

        return 0;
    }

    public int getDamage(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta instanceof Damageable damageable) {
            return damageable.getDamage();
        }

        return 0;
    }

    public int getDurability(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta instanceof Damageable damageable) {
            return item.getType().getMaxDurability() - damageable.getDamage();
        }

        return 0;
    }
}
