package fr.paulem.api.libs.functions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public class LibRadius {
    public static Collection<Block> getBlocksInRadius(Location location, int radius) {
        Collection<Block> blockList = new ArrayList<>();
        // Récupérer les informations du joueur
        World world = location.getWorld();
        if(world == null) throw new IllegalArgumentException("Location must contain a world");

        // Calculer les limites du rayon autour du joueur
        int minX = location.getBlockX() - radius;
        int minY = location.getBlockY() - radius;
        int minZ = location.getBlockZ() - radius;

        int maxX = location.getBlockX() + radius;
        int maxY = location.getBlockY() + radius;
        int maxZ = location.getBlockZ() + radius;

        // Parcourir chaque bloc dans le rayon
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    blockList.add(block);
                }
            }
        }

        return blockList;
    }

    public static Collection<Entity> getEntitiesAroundLocation(World world, double x, double y, double z, double radius) {
        return getEntitiesAroundLocation(new Location(world, x, y, z), radius, radius, radius);
    }

    public static Collection<Entity> getEntitiesAroundLocation(World world, double x, double y, double z, double radiusx, double radiusy, double radiusz) {
        return getEntitiesAroundLocation(new Location(world, x, y, z), radiusx, radiusy, radiusz);
    }

    public static Collection<Entity> getEntitiesAroundLocation(Location loc, double radius) {
        return getEntitiesAroundLocation(loc, radius, radius, radius);
    }

    public static Collection<Entity> getEntitiesAroundLocation(Location loc, double radiusx, double radiusy, double radiusz) {
        return Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc, radiusx, radiusy, radiusz);
    }

    public static boolean isTherePlayerNearby(Entity entity) {
        return !entity.getNearbyEntities(20, 255, 20).stream().filter(ent -> ent instanceof Player).toList().isEmpty();
    }

    public static boolean isTherePlayerNearby(Location location) {
        return !getEntitiesAroundLocation(location, 20, 255, 20).stream().filter(ent -> ent instanceof Player).toList().isEmpty();
    }

    public static Player getNearestPlayer(Entity entity) {
        return entity.getWorld().getPlayers()
                .stream()
                .map(Player.class::cast)
                .min(Comparator.comparingDouble(player -> entity.getLocation().distance(player.getLocation())))
                .orElse(null);
    }
}
