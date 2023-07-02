package fr.paulem.alot.blocks;

import fr.paulem.alot.ALOT;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Objects;

public class LandMine {

    public ALOT main;

    public BlockDisplay landMineEntity;
    public Location locationLandMine;

    public LandMine(ALOT main, Location location, boolean passInit){
        this.main = main;
        locationLandMine = location;
        World world = location.getWorld();
        if(world == null) throw new NullPointerException("world is null");

        Block redstoneWhile = location.getBlock();
        while(!redstoneWhile.getType().isSolid()){
            redstoneWhile = redstoneWhile.getLocation().subtract(0, 1, 0).getBlock();
        }

        landMineEntity = world.spawn(location, BlockDisplay.class);
        landMineEntity.setBlock(redstoneWhile.getBlockData());
        landMineEntity.setTransformation(new Transformation(new Vector3f(0, 0, 0), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.15f, 0.1f, 0.15f), new AxisAngle4f(0, 0, 0, 0)));
        landMineEntity.setInvulnerable(true);
        landMineEntity.setPersistent(true);
        landMineEntity.getPersistentDataContainer().set(new NamespacedKey(this.main, "landmine"), PersistentDataType.INTEGER, 1);

        if(passInit){
            LandMine.this.main.landMines.add(LandMine.this);
        } else {
            BukkitTask initLandMine = new BukkitRunnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(LandMine.this.getLocationLandMine().getWorld()).playSound(LandMine.this.getLocationLandMine(), Sound.ENTITY_CREEPER_PRIMED, 1F, 1F);
                }
            }.runTaskTimer(this.main, 20L, 20L);

            new BukkitRunnable() {
                @Override
                public void run() {
                    initLandMine.cancel();
                }
            }.runTaskLater(this.main, 20L * 2);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(LandMine.this.getLocationLandMine().getWorld()).playSound(LandMine.this.getLocationLandMine(), Sound.BLOCK_BELL_USE, 1F, 1F);
                    LandMine.this.main.landMines.add(LandMine.this);
                }
            }.runTaskLater(this.main, 20L * 3);
        }
    }

    public static LandMine newLandMine(ALOT main, BlockDisplay blockDisplay){
        blockDisplay.remove();
        return new LandMine(main, blockDisplay.getLocation(), true);
    }

    public void explose(){
        this.getLandMineEntity().remove();
        Objects.requireNonNull(this.getLocationLandMine().getWorld()).createExplosion(this.getLocationLandMine(), 8F);
        this.main.landMines.remove(this);
    }

    public Location getLocationLandMine() {
        return locationLandMine;
    }

    public BlockDisplay getLandMineEntity() {
        return landMineEntity;
    }
}
