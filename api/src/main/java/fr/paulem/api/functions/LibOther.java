package fr.paulem.api.functions;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class LibOther {
    public static int RandomBtw(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static double RandomBtw(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }

    public static boolean isDay(World world) {
        long time = world.getTime();

        return time < 12300 || time > 23850;
    }

    public static FileConfiguration reloadConfig(JavaPlugin plugin) {
        plugin.reloadConfig();

        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        return plugin.getConfig();
    }
}
