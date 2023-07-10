package fr.paulem.alot.listeners;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ListenerPloof extends CListener {
    private static final Sound water = ALOT.bukkitVersion.minor() >= 13 ? Sound.ENTITY_FISHING_BOBBER_SPLASH : Sound.BLOCK_WATER_AMBIENT;
    private static final Sound lava = Sound.BLOCK_LAVA_EXTINGUISH;

    public ListenerPloof(ALOT main) {
        super(main);
    }

    @EventHandler
    public void onPloof(PlayerMoveEvent e){
        Player entity = e.getPlayer();
        if(entity.getLocation().getBlock().isLiquid()) return;
        double pVelocity = entity.getVelocity().getY() + entity.getVelocity().getX() + entity.getVelocity().getZ();
        double calcPVelocity = Math.pow(-pVelocity / 3 * 10, 2) / 10;
        if(calcPVelocity < 0.4) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.getLocation().getBlock().isLiquid()) return;

                Ploof(main, entity, calcPVelocity);
            }
        }.runTask(main);
    }

    public static void Ploof(ALOT main, Entity entity, double calcPVelocity){
        Ploof(main, entity, entity.getWorld(), entity.getLocation(), entity.getLocation().getBlock().getType(), calcPVelocity);
    }

    public static void Ploof(ALOT main, Entity entity, Location location, double calcPVelocity){
        Ploof(main, entity, location.getWorld(), location, location.getBlock().getType(), calcPVelocity);
    }

    public static void Ploof(ALOT main, Entity entity, World world, Location location, Material blockMat, double calcPVelocity){
        double cylinderHeight = calcPVelocity/2;
        double cylinderRadius = calcPVelocity/4;
        Random random = new Random();
        for (double y = 0; y < cylinderHeight; y += 0.1) {
            double yOffset = Math.sin(y * Math.PI) * 0.5; // Redescend progressivement

            for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 8) {
                double x = cylinderRadius * Math.cos(theta);
                double z = cylinderRadius * Math.sin(theta);

                double offsetX = random.nextDouble() * 0.6 - 0.3; // Décalage aléatoire sur l'axe X
                double offsetY = random.nextDouble() * 0.6 - 0.3 + yOffset; // Décalage aléatoire sur l'axe Y avec redescente progressive
                double offsetZ = random.nextDouble() * 0.6 - 0.3; // Décalage aléatoire sur l'axe Z

                double finalY = y;
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(blockMat == Material.LAVA) world.spawnParticle(Particle.LAVA, location.getX() + x, location.getY() + finalY,
                                location.getZ() + z, new Random().nextInt(5) + 1, offsetX, offsetY, offsetZ, 0.1);
                        else world.spawnParticle(Particle.WATER_SPLASH, location.getX() + x, location.getY() + finalY,
                                location.getZ() + z, new Random().nextInt(15) + 1, offsetX, offsetY, offsetZ, 0.1);
                    }
                }.runTaskLater(main, new Random().nextInt(5) + 1);
            }
        }
        if(blockMat == Material.WATER) world.playSound(entity.getLocation(), water, (float) calcPVelocity, 1f);
        else if(blockMat == Material.LAVA) world.playSound(entity.getLocation(), lava, (float) calcPVelocity, 1f);
    }
}
