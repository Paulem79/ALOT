package fr.paulem.alot.listeners;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CListener;
import fr.paulem.alot.entities.HoloEntity;
import fr.paulem.api.functions.LibRadius;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static fr.paulem.api.functions.LibOther.RandomBtw;

public class ListenerHealthDisplay extends CListener {
    public ListenerHealthDisplay(ALOT main) {
        super(main);
    }

    public static void healthBar(ALOT main, LivingEntity entity, boolean playerNear, @Nullable Double finalDamage) {
        if (!playerNear) return;
        if (entity instanceof Player) return;

        //Afficher les dégâts
        if (finalDamage != null && finalDamage > 0) new HoloEntity(main,
                entity.getLocation().add(RandomBtw(-.1, .1), 1 + RandomBtw(-.1, .1), RandomBtw(-.1, .1)),
                ChatColor.RED + Long.toString(Math.round(finalDamage > entity.getHealth() ? entity.getHealth() : finalDamage)))
                .deleteAfter(20L * 2);

        LibRadius.getEntitiesInAllWorlds()
                .filter(e -> e.getPersistentDataContainer().has(ALOT.healthbarKey, PersistentDataType.STRING) && Objects.equals(e.getPersistentDataContainer().get(main.healthbarKey, PersistentDataType.STRING), entity.getUniqueId().toString()))
                .forEach(Entity::remove);

        new BukkitRunnable() {
            @Override
            public void run() {
                Location location = entity.getLocation().clone().add(0, entity.getHeight() / 2, 0);

                HoloEntity holoEntity = new HoloEntity(main, location, healthText(entity), entity);

                entity.addPassenger(holoEntity.hologram);

                holoEntity.deleteAfter(20L * 5);
            }
        }.runTask(main);
    }

    private static String healthText(LivingEntity entity) {
        return healthText(entity, null);
    }

    private static String healthText(LivingEntity entity, @Nullable LifeStyle lifeStyle) {
        if (lifeStyle == null) lifeStyle = LifeStyle.MULTIPLE;

        int scale = lifeStyle.getToFixed();
        int amount = lifeStyle.getAmount();
        double size = (entity.getHealth() / amount);

        StringBuilder healthDisplay = new StringBuilder();
        BigDecimal bigDecimal = null;
        if (lifeStyle.getAmount() != 2) bigDecimal = BigDecimal.valueOf(size).setScale(scale, RoundingMode.HALF_UP);
        if (lifeStyle == LifeStyle.MULTIPLE) {
            if (size > 10) return healthText(entity, LifeStyle.TEN);
            else {
                for (int i = 0; i < size - 1; i++) {
                    healthDisplay.append(lifeStyle.getStyle());
                }
                if ((int) Math.ceil(size) != size)
                    healthDisplay.append(LifeColors.SEMI.getColor()).append(lifeStyle.getStyle());
                else healthDisplay.append(lifeStyle.getStyle());
            }
        } else healthDisplay.append(lifeStyle.getStyle());
        return String.valueOf(lifeStyle.getColor().getColor()) + (bigDecimal != null ? bigDecimal : "") + healthDisplay;
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof LivingEntity entity) {
            healthBar(main, entity, false, null);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity entity) {
            // L'entité est attaquée par un joueur, montrer la barre de vie
            healthBar(main, entity, e.getDamager() instanceof Player, e.getFinalDamage());
        }
    }

    private enum LifeColors {
        FULL(ALOT.bukkitVersion.minor() >= 16 ? ChatColor.of("#f10404") : ChatColor.DARK_RED),
        SEMI(ALOT.bukkitVersion.minor() >= 16 ? ChatColor.of("#fd7c7c") : ChatColor.RED);

        private final ChatColor color;

        LifeColors(ChatColor color) {
            this.color = color;
        }

        public ChatColor getColor() {
            return color;
        }
    }

    @SuppressWarnings("unused")
    private enum LifeStyle {
        HEART("❤", 2, 1, LifeColors.FULL),
        TWO("x❤❤", 4, 1, LifeColors.FULL),
        THREE("x❤❤❤", 6, 1, LifeColors.FULL),
        FOUR("x❤❤❤❤", 8, 1, LifeColors.FULL),
        FIVE("x❤❤❤❤❤", 10, 1, LifeColors.FULL),
        TEN("x❤❤❤❤❤❤❤❤❤❤", 20, 1, LifeColors.FULL),
        MULTIPLE("❤", 2, 1, LifeColors.FULL),
        HP(" HP", 1, 0, LifeColors.FULL);

        private final String style;
        private final int amount;
        private final int toFixed;
        private final LifeColors color;

        LifeStyle(String style, int amount, int toFixed, LifeColors color) {
            this.style = style;
            this.amount = amount;
            this.toFixed = toFixed;
            this.color = color;
        }

        public String getStyle() {
            return style;
        }

        public int getAmount() {
            return amount;
        }

        public int getToFixed() {
            return toFixed;
        }

        public LifeColors getColor() {
            return color;
        }
    }
}
