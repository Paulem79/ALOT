package fr.paulem.nms_v20_1;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_20_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import java.util.List;

public class Biome_1_20_1 {
    private static final Server server = Bukkit.getServer();
    private static final CraftServer craftserver = (CraftServer)server;
    private static final DedicatedServer dedicatedserver = craftserver.getServer();
    public static ResourceKey<Biome> getKey(){
        return ResourceKey.create(Registries.BIOME, new ResourceLocation("alot", "fancybiome"));
    }

    public static Biome retrieve(){
        ResourceKey<Biome> oldKey = ResourceKey.create(Registries.BIOME, new ResourceLocation("minecraft", "forest"));
        Registry<Biome> registrywritable = dedicatedserver.registryAccess().registryOrThrow(Registries.BIOME);
        return registrywritable.get(oldKey);
    }

    public static void newBiome() {
        Biome forestbiome = retrieve();
        Biome.BiomeBuilder newBiome = new Biome.BiomeBuilder();
        newBiome.hasPrecipitation(true);

        MobSpawnSettings biomeSettingMobs = forestbiome.getMobSettings();
        newBiome.mobSpawnSettings(biomeSettingMobs);

        BiomeGenerationSettings biomeSettingGen = forestbiome.getGenerationSettings();
        newBiome.generationSettings(biomeSettingGen);

        newBiome.temperature(0.7F); //Temperature of biome
        newBiome.downfall(0.8F); //Downfall of biome
        newBiome.temperatureAdjustment(Biome.TemperatureModifier.NONE); // Normal (NONE) or frozen

        BiomeSpecialEffects.Builder newFog = new BiomeSpecialEffects.Builder();
        int color = Integer.parseInt("ff9900",16);
        //This doesn't affect the actual final grass color, just leave this line as it is or you will get errors
        newFog.fogColor(color);
        newFog.waterColor(color); //water color
        newFog.waterFogColor(color); //water fog color
        newFog.skyColor(color); //sky color
        newFog.foliageColorOverride(color); //foliage color (leaves, fines and more)
        newFog.grassColorOverride(color); //grass blocks color

        newBiome.specialEffects(newFog.build());

        ((WritableRegistry<Biome>) dedicatedserver.registryAccess().registryOrThrow(Registries.BIOME)).register(getKey(), newBiome.build(), Lifecycle.stable());
    }

    public boolean setBiome(String newBiomeName, Chunk c) {

        Biome base;
        Registry<Biome> registrywritable = dedicatedserver.registryAccess().registryOrThrow(Registries.BIOME);

        ResourceKey<Biome> rkey = ResourceKey.create(Registries.BIOME, new ResourceLocation(newBiomeName.toLowerCase()));
        base = registrywritable.get(rkey);
        if(base == null) {
            if(newBiomeName.contains(":")) {
                ResourceKey<Biome> newrkey = ResourceKey.create(Registries.BIOME, new ResourceLocation(newBiomeName.split(":")[0].toLowerCase(), newBiomeName.split(":")[1].toLowerCase()));
                base = registrywritable.get(newrkey);
                if(base == null) {
                    return false;
                }
            } else {
                return false;
            }
        }

        ServerLevel w = ((CraftWorld)c.getWorld()).getHandle();

        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                for(int y = 0; y <= c.getWorld().getMaxHeight(); y++) {

                    setBiome(c.getX() * 16 + x, y, c.getZ() * 16 + z, w, base);
                }
            }
        }
        refreshChunksForAll(c);
        return true;
    }

    public boolean setBiome(String newBiomeName, Location l) {
        Biome base;
        Registry<Biome> registrywritable = dedicatedserver.registryAccess().registryOrThrow(Registries.BIOME);

        ResourceKey<Biome> rkey = ResourceKey.create(Registries.BIOME, new ResourceLocation(newBiomeName.toLowerCase()));
        base = registrywritable.get(rkey);
        if(base == null) {
            if(newBiomeName.contains(":")) {
                ResourceKey<Biome> newrkey = ResourceKey.create(Registries.BIOME, new ResourceLocation(newBiomeName.split(":")[0].toLowerCase(), newBiomeName.split(":")[1].toLowerCase()));
                base = registrywritable.get(newrkey);
                if(base == null) {
                    return false;
                }
            } else {
                return false;
            }
        }

        setBiome(l.getBlockX(), l.getBlockY(), l.getBlockZ(), ((CraftWorld)l.getWorld()).getHandle(), base);
        refreshChunksForAll(l.getChunk());
        return true;
    }

    private void setBiome(int x, int y, int z, ServerLevel w, Biome bb) {
        BlockPos pos = new BlockPos(x, 0, z);

        if (w.isLoaded(pos)) {

            ChunkAccess chunk = w.getChunk(pos);
            if (chunk != null) {

                chunk.setBiome(x >> 2, y >> 2, z >> 2, Holder.direct(bb));
                //chunk.markDirty();
            }
        }
    }

    private void refreshChunksForAll(Chunk chunk) {
        CraftChunk craftChunk = ((CraftChunk)chunk);
        ServerLevel level = craftChunk.getCraftWorld().getHandle();
        level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(craftChunk.getHandle(ChunkStatus.BIOMES)));
    }
}
