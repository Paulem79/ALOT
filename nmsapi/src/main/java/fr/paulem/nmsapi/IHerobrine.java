package fr.paulem.nmsapi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IHerobrine {
    void spawn();

    void spawnFor(Player p);

    void remove();

    boolean isEntity(Entity et);

    Player getHerobrine();
}
