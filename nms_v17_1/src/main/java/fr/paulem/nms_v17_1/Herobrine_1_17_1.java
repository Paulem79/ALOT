package fr.paulem.nms_v17_1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.paulem.nmsapi.IHerobrine;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Herobrine_1_17_1 extends ServerPlayer implements IHerobrine {
    private final Location loc;
    private final JavaPlugin plugin;

    public Herobrine_1_17_1(JavaPlugin plugin, ServerLevel ws, GameProfile gp, Location loc) {
        super(ws.getServer(), ws, gp);
        this.loc = loc;
        this.plugin = plugin;
        moveTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()); // set location
    }

    public static @Nullable IHerobrine createHerobrine(JavaPlugin plugin, Location loc) {
        if (loc.getWorld() == null) return null;
        // get NMS world
        ServerLevel nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "Herobrine"); // create game profile
        Property textures = new Property("textures",
                "eyJ0aW1lc3RhbXAiOjE0MjE0ODczMzk3MTMsInByb2ZpbGVJZCI6ImY4NGM2YTc5MGE0ZTQ1ZTA4NzliY2Q0OWViZDRjNGUyIiwicHJvZmlsZU5hbWUiOiJIZXJvYnJpbmUiLCJpc1B1YmxpYyI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk4YjdjYTNjN2QzMTRhNjFhYmVkOGZjMThkNzk3ZmMzMGI2ZWZjODQ0NTQyNWM0ZTI1MDk5N2U1MmU2Y2IifX19",
                "Edb1R3vm2NHUGyTPaOdXNQY9p5/Ez4xButUGY3tNKIJAzjJM5nQNrq54qyFhSZFVwIP6aM4Ivqmdb2AamXNeN0KgaaU/C514N+cUZNWdW5iiycPytfh7a6EsWXV4hCC9B2FoLkbXuxs/KAbKORtwNfFhQupAsmn9yP00e2c3ZQmS18LWwFg0vzFqvp4HvzJHqY/cTqUxdlSFDrQe/4rATe6Yx6v4zbZN2sHbSL+8AwlDDuP2Xr4SS6f8nABOxjSTlWMn6bToAYiymD+KUPoO0kQJ0Uw/pVXgWHYjQeM4BYf/FAxe8Bf1cP8S7VKueULkOxqIjXAp85uqKkU7dR/s4M4yHm6fhCOCLSMv6hi5ewTaFNYyhK+NXPftFqHcOxA1LbrjOe6NyphF/2FI79n90hagxJpWwNPz3/8I5rnGbYwBZPTsTnD8PszgQTNuWSuvZwGIXPIp9zb90xuU7g7VNWjzPVoOHfRNExEs7Dn9pG8CIA/m/a8koWW3pkbP/AMMWnwgHCr/peGdvF5fN+hJwVdpbfC9sJfzGwA7AgXG/6yqhl1U7YAp/aCVM9bZ94sav+kQghvN41jqOwy4F4i/swc7R4Fx2w5HFxVY3j7FChG7iuhqjUclm79YNhTG0lBQLiZbN5FmC9QgrNHRKlzgSZrXHWoG3YXFSqfn4J+Om9w=");

        profile.getProperties().put(textures.getName(), textures);
        // use class given just before
        Herobrine_1_17_1 herobrine = new Herobrine_1_17_1(plugin, nmsWorld, profile, loc);
        // now quickly made player connection
        herobrine.connection = new ServerGamePacketListenerImpl(herobrine.server,
                new Connection(PacketFlow.CLIENTBOUND),
                herobrine
        );

        nmsWorld.addNewPlayer(herobrine); // add entity to world
        herobrine.getBukkitEntity().getPersistentDataContainer().set(new NamespacedKey(plugin, "fakePlayer"), PersistentDataType.INTEGER, 1);
        herobrine.spawn(); // spawn for actual online players
        // now you can keep the FakePlayer instance for next player or just to check
        return herobrine;
    }

    public void spawn() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            spawnFor(pl); // send all spawn packets
        }
    }

    public void spawnFor(Player p) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;

        // add player in player list for player
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this));
        // make player spawn in world
        connection.send(new ClientboundAddPlayerPacket(this));
        // change head rotation
        connection.send(new ClientboundRotateHeadPacket(this, (byte) ((loc.getYaw() * 256f) / 360f)));
        // add player to tab list
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_LATENCY, this));
        // here the entity is showed, you can show item in hand like that :
        // connection.sendPacket(new PacketPlayOutEntityEquipment(getId(), 0, CraftItemStack.asNMSCopy(itemInHand)));
    }

    public void remove() {
        this.kill();
    }

    public boolean isEntity(Entity et) {
        return this.getId() == et.getEntityId(); // check if it's this entity
    }

    public Player getHerobrine() {
        return this.getBukkitEntity().getPlayer();
    }

    @Override
    public void tick() {
        super.tick();
        doTick();
    }

    @Override
    public void doTick() {
        super.baseTick();
        moveWithFallDamage();
    }

    private void moveWithFallDamage() {
        double y = getY();
        travel(Vec3.ZERO);
        doCheckFallDamage(getY() - y, onGround);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.checkBlocking(damageSource)) {
            //this.attackBlocked = true;
            this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + this.getLevel().random.nextFloat() * 0.4F);
        }
        boolean damaged = super.hurt(damageSource, f);
        if (damaged) {
            if (this.hurtMarked) {
                this.hurtMarked = false;
                Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(plugin.getClass()), () -> this.hurtMarked = true);
            }
        }
        return damaged;
    }

    private boolean checkBlocking(DamageSource damagesource) {
        net.minecraft.world.entity.Entity entity = damagesource.getDirectEntity();
        boolean flag = false;
        if (entity instanceof Arrow entityarrow) {
            if (entityarrow.getPierceLevel() > 0) {
                flag = true;
            }
        }
        if (this.isBlocking() && !flag) {
            Vec3 vec3d = damagesource.getSourcePosition();
            if (vec3d != null) {
                Vec3 vec3d1 = this.getViewVector(1.0F);
                Vec3 vec3d2 = vec3d.vectorTo(this.getDeltaMovement()).normalize();
                vec3d2 = new Vec3(vec3d2.x, 0.0D, vec3d2.z);
                return vec3d2.dot(vec3d1) < 0.0D;
            }
        }
        return false;
    }
}