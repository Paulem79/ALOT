package fr.paulem.api.classes;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * listener
 */
public class CPluginListener extends CPlugin implements Listener {
    public CPluginListener(JavaPlugin main){
        super(main);
    }
}
