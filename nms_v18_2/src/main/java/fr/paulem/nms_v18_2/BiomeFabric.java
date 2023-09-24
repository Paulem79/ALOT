package fr.paulem.nms_v18_2;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class BiomeFabric {
    public static final ResourceKey<Biome> OBSILAND_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("alot", "obsiland"));
    private static final Biome OBSILAND = createObsiland();

    public static void gen(){
        Registry.register(BuiltinRegistries.BIOME, OBSILAND_KEY, OBSILAND);
    }

    private static Biome createObsiland() {
        // We specify what entities spawn and what features generate in the biome.
        // Aside from some structures, trees, rocks, plants and
        //   custom entities, these are mostly the same for each biome.
        // Vanilla configured features for biomes are defined in DefaultBiomeFeatures.

        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals(spawnSettings);
        BiomeDefaultFeatures.monsters(spawnSettings, 95, 5, 100, true);

        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder();
        generationSettings.build();
        BiomeDefaultFeatures.addDefaultCarversAndLakes(generationSettings);
        BiomeDefaultFeatures.addDefaultOres(generationSettings);
        BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);
        BiomeDefaultFeatures.addDefaultSprings(generationSettings);

        return (new Biome.BiomeBuilder())
                .precipitation(Biome.Precipitation.RAIN)
                .biomeCategory(Biome.BiomeCategory.NONE)
                .temperature(0.8F)
                .downfall(0.4F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x050533)
                        .fogColor(0xc0d8ff)
                        .skyColor(0x77adff)
                        .build())
                .mobSpawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }
}
