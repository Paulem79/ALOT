package fr.paulem.nmsgate;

import fr.paulem.api.enums.VersionMethod;
import fr.paulem.api.radios.LibVersion;
import fr.paulem.nms_v17_1.Herobrine_1_17_1;
import fr.paulem.nms_v17_1.Listeners_1_17_1;
import fr.paulem.nms_v18_2.Herobrine_1_18_2;
import fr.paulem.nms_v18_2.Listeners_1_18_2;
import fr.paulem.nms_v19_4.Herobrine_1_19_4;
import fr.paulem.nms_v19_4.Listeners_1_19_4;
import fr.paulem.nms_v20_1.Herobrine_1_20_1;
import fr.paulem.nms_v20_1.Listeners_1_20_1;
import fr.paulem.nmsapi.IHerobrine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NmsGate {
    public static @Nullable IHerobrine createHerobrine(JavaPlugin plugin, Location loc) {
        IHerobrine herobrine = switch (LibVersion.getVersion(VersionMethod.SERVER).toString()) {
            case "1.20.1", "1.20.0" -> Herobrine_1_20_1.createHerobrine(plugin, loc);
            case "1.19.4", "1.19.3" -> Herobrine_1_19_4.createHerobrine(plugin, loc);
            case "1.18.2" -> Herobrine_1_18_2.createHerobrine(plugin, loc);
            case "1.17.1" -> Herobrine_1_17_1.createHerobrine(plugin, loc);
            default -> null;
        };
        if (Bukkit.getScoreboardManager() != null && herobrine != null)
            Objects.requireNonNull(Bukkit.getScoreboardManager().getMainScoreboard().getTeam("nhide")).addEntry(herobrine.getHerobrine().getName());
        return herobrine;
    }

    public static @Nullable Listener nmsListeners(JavaPlugin plugin) {
        return switch (LibVersion.getVersion(VersionMethod.SERVER).toString()) {
            case "1.20.1", "1.20.0" -> new Listeners_1_20_1(plugin);
            case "1.19.4", "1.19.3" -> new Listeners_1_19_4(plugin);
            case "1.18.2" -> new Listeners_1_18_2(plugin);
            case "1.17.1" -> new Listeners_1_17_1(plugin);
            default -> null;
        };
    }
}
