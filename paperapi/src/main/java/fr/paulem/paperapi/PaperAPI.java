package fr.paulem.paperapi;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import static fr.paulem.api.API.isServerPaperBased;
import static fr.paulem.api.API.registerEvents;

public class PaperAPI {
    public static @Nullable JavaPlugin plugin;
    public static boolean enabled = false;

    public static void initPaper(JavaPlugin plugin) {
        if (isServerPaperBased()) {
            PaperAPI.plugin = plugin;
            PaperAPI.enabled = isServerPaperBased();
            plugin.getLogger().info("Paper Integration enabled !");
            //enable();
        } else {
            plugin.getLogger().info("Paper Integration not enabled !");
        }
    }

    public static void enable() {
        if (plugin == null || !isServerPaperBased())
            throw new IllegalStateException("Paper Integration isn't enabled !");
        registerEvents(plugin, new PaperListeners(plugin));
    }
}