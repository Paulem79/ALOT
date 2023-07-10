package fr.paulem.paperapi;

import fr.paulem.api.classes.CPluginListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperListeners extends CPluginListener implements Listener {
    public PaperListeners(JavaPlugin main) {
        super(main);
    }
}
