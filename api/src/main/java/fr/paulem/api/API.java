package fr.paulem.api;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class API {
    public static boolean isServerPaperBased() {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static void registerEvents(JavaPlugin plugin, Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Launch a ItemStack, return true if the item touch the ground / an Entity
     * @param plugin the plugin
     * @param e the PlayerInteractEvent
     * @return if the item touch the ground / an Entity
     */
    public static LaunchItemReturn LaunchItem(@NotNull JavaPlugin plugin, PlayerInteractEvent e){
        return LaunchItem(plugin, e, true);
    }

    /**
     * Launch a ItemStack, return true if the item touch the ground / an Entity
     * @param plugin the plugin
     * @param e the PlayerInteractEvent
     * @param cancel Cancel the event
     * @return if the item touch the ground / an Entity
     */
    public static LaunchItemReturn LaunchItem(@NotNull JavaPlugin plugin, PlayerInteractEvent e, boolean cancel){
        e.setCancelled(cancel);
        return LaunchItem(plugin, e.getItem(), e.getPlayer());
    }

    /**
     * Launch a ItemStack, return true if the item touch the ground / an Entity
     * @param plugin the plugin
     * @param item the ItemStack
     * @param player the Player
     * @return if the item touch the ground / an Entity
     */
    public static LaunchItemReturn LaunchItem(@NotNull JavaPlugin plugin, ItemStack item, @NotNull Player player){
        final LaunchItemReturn[] launchItemReturns = {new LaunchItemReturn(false, null, null)};
        if(item == null) return launchItemReturns[0];
        ArmorStand as = player.getWorld().spawn(player.getLocation().add(0, 0.5, 0), ArmorStand.class);

        as.setArms(true);
        as.setGravity(false);
        as.setVisible(false);
        as.setSmall(true);
        as.setMarker(true);
        Objects.requireNonNull(as.getEquipment()).setItem(EquipmentSlot.HAND, item);
        as.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(0), Math.toRadians(0)));

        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

        Location dest = player.getLocation().add(player.getLocation().getDirection().multiply(10));
        Vector vector = dest.subtract(player.getLocation()).toVector();

        new BukkitRunnable(){
            final int distance = 10;
            int i = 0;

            public void run(){

                EulerAngle rot = as.getRightArmPose();
                EulerAngle rotnew = rot.add(20, 0, 0);
                as.setRightArmPose(rotnew);

                as.teleport(as.getLocation().add(vector.normalize()));
                if(as.getTargetBlockExact(1) != null && !Objects.requireNonNull(as.getTargetBlockExact(1)).isPassable()){
                    if(!as.isDead()){
                        launchItemReturns[0] = new LaunchItemReturn(true, null, as.getTargetBlockExact(1));
                        as.remove();
                        if(player.getInventory().firstEmpty() != -1){
                            player.getInventory().addItem(item);
                        }
                        else{
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                        cancel();
                    }
                }
                for(Entity entity : as.getLocation().getChunk().getEntities()){
                    if(!as.isDead()){
                        if(as.getLocation().distanceSquared(entity.getLocation()) <= 1){
                            if(entity != as && entity != player){
                                if(entity instanceof LivingEntity livingEntity){
                                    launchItemReturns[0] = new LaunchItemReturn(true, livingEntity, null);
                                    as.remove();
                                    if(player.getInventory().firstEmpty() != -1){
                                        player.getInventory().addItem(item);
                                    }
                                    else{
                                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                                    }
                                    cancel();
                                }
                            }
                        }
                    }
                }
                if(i > distance){
                    launchItemReturns[0] = new LaunchItemReturn(true, null, null);
                    if(!as.isDead()){
                        as.remove();
                        if(player.getInventory().firstEmpty() != -1){
                            player.getInventory().addItem(item);
                        }
                        else{
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                        cancel();
                    }
                }

                i++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        return launchItemReturns[0];
    }

    static class LaunchItemReturn {
        public boolean okay;
        public @Nullable LivingEntity livingEntity;
        public @Nullable Block block;

        public LaunchItemReturn(boolean okay, @Nullable LivingEntity livingEntity, @Nullable Block block){
            this.okay = okay;
            this.livingEntity = livingEntity;
            this.block = block;
        }

        public @Nullable LivingEntity getLivingEntity() {
            return livingEntity;
        }

        public @Nullable Block getBlock() {
            return block;
        }

        public boolean isOkay() {
            return okay;
        }
    }
}