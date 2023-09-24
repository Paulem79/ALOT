package fr.paulem.alot.listeners;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CListener;
import fr.paulem.api.functions.LibRadius;
import fr.paulem.nmsgate.NmsGate;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;

public class ListenerHerobrine extends CListener {
    public ListenerHerobrine(ALOT main) {
        super(main);
    }

    /*@EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        List<Material> herobrineStructure = List.of(Material.GOLD_BLOCK, Material.NETHERRACK, Material.FIRE, Material.REDSTONE_TORCH);
        if (!herobrineStructure.contains(e.getBlock().getType())) return;
        Location location = e.getBlock().getLocation();
        // Check if the player has placed 3x3 blocks of gold
        List<Block> blocks = LibRadius.getBlocksInRadius(location, 2).stream().filter(block -> herobrineStructure.contains(block.getType())).toList();
        if (blocks.stream().filter(block -> block.getType() == Material.GOLD_BLOCK).count() == 9 &&
                blocks.stream().filter(block -> block.getType() == Material.NETHERRACK).count() == 1 &&
                blocks.stream().filter(block -> block.getType() == Material.FIRE).count() == 1 &&
                blocks.stream().filter(block -> block.getType() == Material.REDSTONE_TORCH).count() == 4) {
            Optional<Block> herobrineSpawnOptional = blocks.stream().filter(block -> block.getType() == Material.FIRE).findFirst();
            if (herobrineSpawnOptional.isEmpty()) return;
            Block herobrineSpawn = herobrineSpawnOptional.get();
            if (herobrineSpawn.getRelative(BlockFace.DOWN).getType() == Material.NETHERRACK &&
                    herobrineSpawn.getRelative(BlockFace.DOWN, 2).getType() == Material.GOLD_BLOCK &&
                    LibRadius.getBlocksInRadius(herobrineSpawn.getRelative(BlockFace.DOWN, 2).getLocation(), 1, 0, 1).stream().filter(block -> block.getType() == Material.GOLD_BLOCK).count() == 9) {
                Location herobrineSpawnLocation = herobrineSpawn.getLocation().add(.5, 0, .5);
                Player herobrine = NmsGate.createHerobrine(main, herobrineSpawnLocation);
                assert herobrine != null;
                PlayerJoinEvent event = new PlayerJoinEvent(herobrine, ChatColor.YELLOW + herobrine.getName() + " joined the game");
                Bukkit.getPluginManager().callEvent(event);
                String message = event.getJoinMessage();
                if (message != null) Bukkit.broadcastMessage(event.getJoinMessage());
                e.getBlock().getWorld().spawn(herobrineSpawnLocation, LightningStrike.class);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        herobrine.teleport(herobrine.getLocation().setDirection(e.getPlayer().getLocation().subtract(herobrine.getLocation()).toVector()));
                    }
                }.runTaskTimer(main, 1L, 1L);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        herobrine.chat("Thanks");
                    }
                }.runTaskLater(main, 20L);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        //Redstone torch first, (don't want them to drop !)
                        for (Block block : blocks.stream().filter(block -> block.getType() == Material.REDSTONE_TORCH).toList()) {
                            block.setType(Material.AIR);
                        }
                        for (Block block : blocks.stream().filter(block -> block.getType() != Material.REDSTONE_TORCH).toList()) {
                            block.setType(Material.AIR);
                        }
                        //task.cancel();
                    }
                }.runTaskLater(main, 20L * 3);
            }
        }
    }*/

    @EventHandler
    public void onHerobrineHit(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!player.getName().equals("Herobrine") || !player.getPersistentDataContainer().has(new NamespacedKey(main, "fakePlayer"), PersistentDataType.INTEGER))
            return;
        e.setCancelled(true);
    }
}
