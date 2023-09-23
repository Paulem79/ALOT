package fr.paulem.api;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
}