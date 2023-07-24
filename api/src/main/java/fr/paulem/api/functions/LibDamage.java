package fr.paulem.api.functions;

import fr.paulem.api.enums.VersionMethod;
import fr.paulem.api.radios.LibVersion;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import static fr.paulem.api.radios.LibVersion.getVersion;

public class LibDamage {
    private static final LibVersion bukkitVersion = getVersion(VersionMethod.BUKKIT);

    public int dealDamage(ItemStack item, int damage){
        return dealDamage(item, damage, true);
    }

    public static int dealDamage(Entity entity, ItemStack item, int damage){
        if(entity instanceof Player player){
            if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return 0;
        }

        return dealDamage(item, damage, true);
    }

    public static int dealDamage(ItemStack item, int damage, boolean considerUnbreaking){
        if(bukkitVersion.minor() < 17) return 0;
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

    public static int setDamage(Entity entity, ItemStack item, int damage) {
        if(entity instanceof Player player){
            if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return 0;
        }

        return setDamage(item, damage);
    }

    public static int setDamage(ItemStack item, int damage) {
        if(bukkitVersion.minor() < 17) return 0;
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

    public static int getDamage(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta instanceof Damageable damageable) {
            return damageable.getDamage();
        }

        return 0;
    }

    public static int getDurability(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta instanceof Damageable damageable) {
            return item.getType().getMaxDurability() - damageable.getDamage();
        }

        return 0;
    }

    public static int consumeUsage(Inventory inv, ItemStack item, int amount){
        if (inv.getHolder() instanceof Player player && player.getGameMode() != GameMode.CREATIVE)
            item.setAmount(item.getAmount() - amount);
        if (item.getAmount() <= 0) inv.remove(item);
        return item.getAmount();
    }
}
