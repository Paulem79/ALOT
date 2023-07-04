package fr.paulem.alot.entities;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CMain;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static fr.paulem.alot.ALOT.bukkitVersion;

public class HoloEntity extends CMain implements Entity {
    private final Entity hologram;

    public HoloEntity(ALOT main, Location location, String text){
        super(main);
        if(bukkitVersion.minor() > 19 || (bukkitVersion.revision() == 4 && bukkitVersion.major() == 19)){
            hologram = Objects.requireNonNull(location.getWorld()).spawn(location, TextDisplay.class, (textDisplay) -> {
                textDisplay.setText(text);
                textDisplay.setBillboard(Display.Billboard.CENTER);
                textDisplay.getPersistentDataContainer().set(main.hologramKey, PersistentDataType.INTEGER, 1);
            });
        } else {
            hologram = Objects.requireNonNull(location.getWorld()).spawn(location, ArmorStand.class, (stand) -> {
                stand.setInvisible(true);
                stand.setInvulnerable(true);
                stand.setGravity(false);
                stand.setMarker(true);
                stand.setCustomNameVisible(true);
                stand.setCustomName(text);
                stand.getPersistentDataContainer().set(main.hologramKey, PersistentDataType.BOOLEAN, true);
            });
        }
    }

    public void edit(Location location, String text) {
        edit(text);
        edit(location);
    }

    public void edit(Location location){
        hologram.teleport(location);
    }

    public void edit(String text){
        hologram.setCustomName(text);
    }

    public void deleteAfter(long ticks){
        new BukkitRunnable(){
            @Override
            public void run() {
                delete();
            }
        }.runTaskLater(main, ticks);
    }

    public void delete(){
        hologram.remove();
    }

    public Entity getHologram() {
        return hologram;
    }

    @NotNull
    @Override
    public Location getLocation() {
        return hologram.getLocation();
    }

    @Nullable
    @Override
    public Location getLocation(@Nullable Location loc) {
        return hologram.getLocation(loc);
    }

    @Override
    public void setVelocity(@NotNull Vector velocity) {
        hologram.setVelocity(velocity);
    }

    @NotNull
    @Override
    public Vector getVelocity() {
        return hologram.getVelocity();
    }

    @Override
    public double getHeight() {
        return hologram.getHeight();
    }

    @Override
    public double getWidth() {
        return hologram.getWidth();
    }

    @NotNull
    @Override
    public BoundingBox getBoundingBox() {
        return hologram.getBoundingBox();
    }

    @Override
    public boolean isOnGround() {
        return hologram.isOnGround();
    }

    @Override
    public boolean isInWater() {
        return hologram.isInWater();
    }

    @NotNull
    @Override
    public World getWorld() {
        return hologram.getWorld();
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        hologram.setRotation(yaw, pitch);
    }

    @Override
    public boolean teleport(@NotNull Location location) {
        return hologram.teleport(location);
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        return hologram.teleport(location, cause);
    }

    @Override
    public boolean teleport(@NotNull Entity destination) {
        return hologram.teleport(destination);
    }

    @Override
    public boolean teleport(@NotNull Entity destination, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        return hologram.teleport(destination, cause);
    }

    @NotNull
    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return hologram.getNearbyEntities(x, y, z);
    }

    @Override
    public int getEntityId() {
        return hologram.getEntityId();
    }

    @Override
    public int getFireTicks() {
        return hologram.getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return hologram.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int ticks) {
        hologram.setFireTicks(ticks);
    }

    @Override
    public void setVisualFire(boolean fire) {
        hologram.setVisualFire(fire);
    }

    @Override
    public boolean isVisualFire() {
        return hologram.isVisualFire();
    }

    @Override
    public int getFreezeTicks() {
        return hologram.getFreezeTicks();
    }

    @Override
    public int getMaxFreezeTicks() {
        return hologram.getMaxFreezeTicks();
    }

    @Override
    public void setFreezeTicks(int ticks) {
        hologram.setFreezeTicks(ticks);
    }

    @Override
    public boolean isFrozen() {
        return hologram.isFrozen();
    }

    @Override
    public void remove() {
        hologram.remove();
    }

    @Override
    public boolean isDead() {
        return hologram.isDead();
    }

    @Override
    public boolean isValid() {
        return hologram.isValid();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        hologram.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
        hologram.sendMessage(messages);
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
        hologram.sendMessage(sender, message);
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
        hologram.sendMessage(sender, messages);
    }

    @NotNull
    @Override
    public Server getServer() {
        return hologram.getServer();
    }

    @NotNull
    @Override
    public String getName() {
        return hologram.getName();
    }

    @Override
    public boolean isPersistent() {
        return hologram.isPersistent();
    }

    @Override
    public void setPersistent(boolean persistent) {
        hologram.setPersistent(persistent);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public Entity getPassenger() {
        return hologram.getPassenger();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean setPassenger(@NotNull Entity passenger) {
        return hologram.setPassenger(passenger);
    }

    @NotNull
    @Override
    public List<Entity> getPassengers() {
        return hologram.getPassengers();
    }

    @Override
    public boolean addPassenger(@NotNull Entity passenger) {
        return hologram.addPassenger(passenger);
    }

    @Override
    public boolean removePassenger(@NotNull Entity passenger) {
        return hologram.removePassenger(passenger);
    }

    @Override
    public boolean isEmpty() {
        return hologram.isEmpty();
    }

    @Override
    public boolean eject() {
        return hologram.eject();
    }

    @Override
    public float getFallDistance() {
        return hologram.getFallDistance();
    }

    @Override
    public void setFallDistance(float distance) {
        hologram.setFallDistance(distance);
    }

    @Override
    public void setLastDamageCause(@Nullable EntityDamageEvent event) {
        hologram.setLastDamageCause(event);
    }

    @Nullable
    @Override
    public EntityDamageEvent getLastDamageCause() {
        return hologram.getLastDamageCause();
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return hologram.getUniqueId();
    }

    @Override
    public int getTicksLived() {
        return hologram.getTicksLived();
    }

    @Override
    public void setTicksLived(int value) {
        hologram.setTicksLived(value);
    }

    @Override
    public void playEffect(@NotNull EntityEffect type) {
        hologram.playEffect(type);
    }

    @NotNull
    @Override
    public EntityType getType() {
        return hologram.getType();
    }

    @NotNull
    @Override
    public Sound getSwimSound() {
        return hologram.getSwimSound();
    }

    @NotNull
    @Override
    public Sound getSwimSplashSound() {
        return hologram.getSwimSplashSound();
    }

    @NotNull
    @Override
    public Sound getSwimHighSpeedSplashSound() {
        return hologram.getSwimHighSpeedSplashSound();
    }

    @Override
    public boolean isInsideVehicle() {
        return hologram.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return hologram.leaveVehicle();
    }

    @Nullable
    @Override
    public Entity getVehicle() {
        return hologram.getVehicle();
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        hologram.setCustomNameVisible(flag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return hologram.isCustomNameVisible();
    }

    @SuppressWarnings("all")
    @Override
    public void setVisibleByDefault(boolean visible) {
        hologram.setVisibleByDefault(visible);
    }

    @SuppressWarnings("all")
    @Override
    public boolean isVisibleByDefault() {
        return hologram.isVisibleByDefault();
    }

    @Override
    public void setGlowing(boolean flag) {
        hologram.setGlowing(flag);
    }

    @Override
    public boolean isGlowing() {
        return hologram.isGlowing();
    }

    @Override
    public void setInvulnerable(boolean flag) {
        hologram.setInvulnerable(flag);
    }

    @Override
    public boolean isInvulnerable() {
        return hologram.isInvulnerable();
    }

    @Override
    public boolean isSilent() {
        return hologram.isSilent();
    }

    @Override
    public void setSilent(boolean flag) {
        hologram.setSilent(flag);
    }

    @Override
    public boolean hasGravity() {
        return hologram.hasGravity();
    }

    @Override
    public void setGravity(boolean gravity) {
        hologram.setGravity(gravity);
    }

    @Override
    public int getPortalCooldown() {
        return hologram.getPortalCooldown();
    }

    @Override
    public void setPortalCooldown(int cooldown) {
        hologram.setPortalCooldown(cooldown);
    }

    @NotNull
    @Override
    public Set<String> getScoreboardTags() {
        return hologram.getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(@NotNull String tag) {
        return hologram.addScoreboardTag(tag);
    }

    @Override
    public boolean removeScoreboardTag(@NotNull String tag) {
        return hologram.removeScoreboardTag(tag);
    }

    @NotNull
    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return hologram.getPistonMoveReaction();
    }

    @NotNull
    @Override
    public BlockFace getFacing() {
        return hologram.getFacing();
    }

    @NotNull
    @Override
    public Pose getPose() {
        return hologram.getPose();
    }

    @NotNull
    @Override
    public SpawnCategory getSpawnCategory() {
        return hologram.getSpawnCategory();
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return hologram.spigot();
    }

    @Nullable
    @Override
    public String getCustomName() {
        return hologram.getCustomName();
    }

    @Override
    public void setCustomName(@Nullable String name) {
        hologram.setCustomName(name);
    }

    @Override
    public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
        hologram.setMetadata(metadataKey, newMetadataValue);
    }

    @NotNull
    @Override
    public List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        return hologram.getMetadata(metadataKey);
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        return hologram.hasMetadata(metadataKey);
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        hologram.removeMetadata(metadataKey, owningPlugin);
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return hologram.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return hologram.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return hologram.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return hologram.hasPermission(perm);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return hologram.addAttachment(plugin, name, value);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return hologram.addAttachment(plugin);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return hologram.addAttachment(plugin, name, value, ticks);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return hologram.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        hologram.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        hologram.recalculatePermissions();
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return hologram.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return hologram.isOp();
    }

    @Override
    public void setOp(boolean value) {
        hologram.setOp(value);
    }

    @NotNull
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return hologram.getPersistentDataContainer();
    }
}
