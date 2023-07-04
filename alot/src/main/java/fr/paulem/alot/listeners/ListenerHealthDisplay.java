package fr.paulem.alot.listeners;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CListener;
import fr.paulem.alot.entities.HoloEntity;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import static fr.paulem.api.libs.functions.LibOther.RandomBtw;
import static fr.paulem.api.libs.functions.LibRadius.getNearestPlayer;

public class ListenerHealthDisplay extends CListener {
    public ListenerHealthDisplay(ALOT main) {
        super(main);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        healthBar(main, e.getPlayer());
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent e){
        healthBar(main, e.getPlayer());
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if(e.getEntity() instanceof LivingEntity entity){
            healthBar(main, entity, null, null);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageEvent e){
        if(e.getEntity() instanceof LivingEntity entity){
            Player damager = getNearestPlayer(e.getEntity());
            if(damager != null && e.getEntity().getLocation().distance(damager.getLocation()) > 20) damager = null;
            healthBar(main, entity, damager, e.getFinalDamage());
        }
    }

    public static void healthBar(ALOT main, LivingEntity entity, @Nullable Player damager, @Nullable Double finalDamage){
        if(damager != null) new HoloEntity(main, entity.getLocation().add(RandomBtw(-.1, .1), 1 + RandomBtw(-.1, .1), RandomBtw(-.1, .1)), ChatColor.RED + Long.toString(Math.round(finalDamage == null ? 0.00 : (finalDamage > entity.getHealth() ? entity.getHealth() : finalDamage)))).deleteAfter(20L*2);
        if(entity instanceof Player) {
            healthBar(main, (Player) entity);
            return;
        }
        if(entity.getCustomName() == null || isHealthName(entity.getCustomName())) {
            new BukkitRunnable(){
                @Override
                public void run() {
                    entity.setCustomName(markdown(entity.getType().name()) + rest(entity, null));
                    entity.setCustomNameVisible(damager != null);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            entity.setCustomNameVisible(false);
                        }
                    }.runTaskLater(main, 20L * 5L);
                }
            }.runTask(main);
        }
    }

    public static void healthBar(ALOT main, Player entity){
        new BukkitRunnable(){
            @Override
            public void run() {
                entity.setDisplayName(entity.getName() + rest(entity, null));
            }
        }.runTask(main);
    }

    private static String rest(LivingEntity entity, @Nullable LifeStyle lifeStyle) {
        if(lifeStyle == null) lifeStyle = LifeStyle.MULTIPLE;

        int scale = lifeStyle.getToFixed();
        int amount = lifeStyle.getAmount();
        double size = (entity.getHealth()/amount);

        StringBuilder healthDisplay = new StringBuilder();
        BigDecimal bigDecimal = null;
        if(lifeStyle.getAmount() != 2){
            bigDecimal = BigDecimal.valueOf(size).setScale(scale, RoundingMode.HALF_UP);
        }
        if(lifeStyle == LifeStyle.MULTIPLE){
            if(size > 10) return rest(entity, LifeStyle.TEN);
            else {
                for (int i = 0; i < size-1; i++) {
                    healthDisplay.append(lifeStyle.getStyle());
                }
                if((int) Math.ceil(size) != size) healthDisplay.append(LifeColors.SEMI.getColor()).append(lifeStyle.getStyle());
                else healthDisplay.append(lifeStyle.getStyle());
            }
        } else healthDisplay.append(lifeStyle.getStyle());
        return " " + lifeStyle.getColor().getColor() + (bigDecimal != null ? bigDecimal : "") + healthDisplay;
    }

    private static boolean isHealthName(String input) {
        EntityType[] entityTypes = EntityType.values();
        LifeColors[] colors = LifeColors.values();
        LifeStyle[] lifeStyles = LifeStyle.values();

        String[] entityNames = new String[entityTypes.length];
        ChatColor[] colorValues = new ChatColor[colors.length];
        String[] lifeValues = new String[lifeStyles.length];

        // Construire un tableau contenant les noms des types d'entités
        for (int i = 0; i < entityTypes.length; i++) {
            entityNames[i] = markdown(entityTypes[i].name());
        }

        // Construire un tableau contenant les valeurs des couleurs de vie
        for (int i = 0; i < colors.length; i++) {
            colorValues[i] = colors[i].getColor();
        }

        // Construire un tableau contenant les valeurs des couleurs de vie
        for (int i = 0; i < lifeStyles.length; i++) {
            lifeValues[i] = lifeStyles[i].getStyle();
        }

        // Vérifier la correspondance en utilisant des conditions
        boolean entityTypeMatch = Arrays.stream(entityNames).anyMatch(input::startsWith);
        boolean colorMatch = Arrays.stream(colorValues).anyMatch(color -> input.toLowerCase().contains(color.toString()));
        boolean heartMatch = Arrays.stream(lifeValues).anyMatch(input::endsWith);

        return entityTypeMatch && colorMatch && heartMatch;
    }

    private static String markdown(String input){
        String[] mots = input.split(" ");
        return input.replace(mots[0], firstLetterToUpperCase(mots[0])).replace("_", " ");
    }

    private static String firstLetterToUpperCase(String mot) {
        if (mot.length() < 2) {
            return mot.toUpperCase();
        }

        String premiereLettre = mot.substring(0, 1).toUpperCase();
        String resteMot = mot.substring(1).toLowerCase();

        return premiereLettre + resteMot;
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
