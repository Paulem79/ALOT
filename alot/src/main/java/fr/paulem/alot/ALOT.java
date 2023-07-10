package fr.paulem.alot;

import fr.paulem.alot.blocks.LandMine;
import fr.paulem.alot.commands.CommandALOT;
import fr.paulem.alot.commands.tabcompletes.TabCommandALOT;
import fr.paulem.alot.gui.GUIAddedItems;
import fr.paulem.alot.item.Handler;
import fr.paulem.alot.listeners.ListenerBlood;
import fr.paulem.alot.listeners.ListenerHealthDisplay;
import fr.paulem.alot.listeners.ListenerItemSwap;
import fr.paulem.alot.listeners.ListenerPloof;
import fr.paulem.api.classes.CondensedCraft;
import fr.paulem.api.enums.VersionMethod;
import fr.paulem.api.functions.LibRadius;
import fr.paulem.api.radios.LibVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.paulem.alot.blocks.LandMine.newLandMine;
import static fr.paulem.api.API.registerEvents;
import static fr.paulem.api.functions.LibDamage.consumeUsage;
import static fr.paulem.api.functions.LibDamage.dealDamage;
import static fr.paulem.api.functions.LibDamage.getDurability;
import static fr.paulem.api.functions.LibDamage.setDamage;
import static fr.paulem.api.functions.LibOther.RandomBtw;
import static fr.paulem.api.radios.LibVersion.getVersion;
import static fr.paulem.paperapi.PaperAPI.initPaper;

public class ALOT extends JavaPlugin implements CommandExecutor, Listener {
    public static final LibVersion bukkitVersion = getVersion(VersionMethod.BUKKIT);
    public static LibVersion serverVersion = getVersion(VersionMethod.SERVER);
    public final List<String> ALOT_SUBCOMMANDS = new ArrayList<>(Arrays.asList("give", "reload", "inventory"));
    public final List<LandMine> landMines = new ArrayList<>();
    public final HashMap<NamespacedKey, ItemStack> registeredItems = new HashMap<>();
    public final List<CondensedCraft> registeredRecipes = new ArrayList<>(); // ItemStack + Recipes
    public final HashMap<Player, BukkitTask> playersRecipesTasks = new HashMap<>();
    public final NamespacedKey hologramKey = new NamespacedKey(this, "hologram");
    public final NamespacedKey healthbarKey = new NamespacedKey(this, "healthbar");
    public FileConfiguration config;

    @Override
    public void onEnable() {
        reloadConfig();
        registerEvents(this, new ListenerPloof(this));
        registerEvents(this, new ListenerHealthDisplay(this));
        registerEvents(this, new ListenerBlood(this));
        registerEvents(this, new GUIAddedItems(this));
        registerEvents(this, new ListenerItemSwap(this));
        registerEvents(this, this);

        initPaper(this);

        Objects.requireNonNull(getCommand("alot")).setExecutor(new CommandALOT(this));
        Objects.requireNonNull(getCommand("alot")).setTabCompleter(new TabCommandALOT(this));

        for (Entity textDisplay : LibRadius.getEntitiesInAllWorlds(txt -> (txt instanceof ArmorStand || txt instanceof TextDisplay) && txt.getPersistentDataContainer().has(hologramKey, PersistentDataType.INTEGER))
                .toList()) {
            textDisplay.remove();
        }

        if (bukkitVersion.minor() >= 14) new Handler(this);

        if (bukkitVersion.minor() >= 19) landMines.addAll(LibRadius.getEntitiesInAllWorlds(
                        e -> e instanceof BlockDisplay blockDisplay && blockDisplay.getPersistentDataContainer().has(new NamespacedKey(ALOT.this, "landmine"), PersistentDataType.INTEGER),
                        e -> newLandMine(this, (BlockDisplay) e))
                .toList()
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            checkRecipe(player);
        }

        if (bukkitVersion.minor() >= 19) new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!landMines.isEmpty()) for (LandMine landMine : landMines) {
                        if (LibRadius.getEntitiesAroundLocation(landMine.getLocationLandMine(), .5, 1, .5).stream().filter(e -> !(e instanceof BlockDisplay)).toList().size() > 0) {
                            landMine.explose();
                        }
                    }
                } catch (ConcurrentModificationException ignored) {

                }
            }
        }.runTaskTimer(this, 1L, 5L);

        getLogger().info("Plugin activated !");
    }

    @Override
    public void onDisable(){
        getLogger().info("Plugin deactivated !");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        checkRecipe(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        if(playersRecipesTasks.containsKey(e.getPlayer())) playersRecipesTasks.get(e.getPlayer()).cancel();
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Arrow arrow)) return;
        if(arrow.getPersistentDataContainer().has(new NamespacedKey(this, "switch_bow"), PersistentDataType.INTEGER)) {
            if (!(arrow.getShooter() instanceof LivingEntity shooter)) return;
            if (!(e.getEntity() instanceof LivingEntity shooted)) return;
            Location shooterLoc = shooter.getLocation();

            shooter.teleport(shooted);
            shooted.teleport(shooterLoc);
        } else if(arrow.getPersistentDataContainer().has(new NamespacedKey(this, "tnt_bow"), PersistentDataType.INTEGER)){
            if (!(e.getEntity() instanceof LivingEntity shooted)) return;

            int power = 0;
            if(arrow.getPersistentDataContainer().has(new NamespacedKey(this, "power"), PersistentDataType.INTEGER)) power = arrow.getPersistentDataContainer().get(new NamespacedKey(this, "power"), PersistentDataType.INTEGER);
            arrow.remove();
            shooted.getWorld().createExplosion(shooted.getLocation(), 4F*((float) power /RandomBtw(1, power)));
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e){
        if(ALOT.bukkitVersion.minor() < 14) return;
        ItemStack bow = e.getBow();
        if(bow == null || bow.getItemMeta() == null) return;
        PersistentDataContainer bowContainer = bow.getItemMeta().getPersistentDataContainer();
        if(bowContainer.has(new NamespacedKey(this, "switch_bow"), PersistentDataType.INTEGER)) {
            e.getProjectile().getPersistentDataContainer().set(new NamespacedKey(this, "switch_bow"), PersistentDataType.INTEGER, 1);
            dealDamage(e.getEntity(), e.getBow(), 9);
        } else if(bowContainer.has(new NamespacedKey(this, "tnt_bow"), PersistentDataType.INTEGER)) {
            e.getProjectile().getPersistentDataContainer().set(new NamespacedKey(this, "tnt_bow"), PersistentDataType.INTEGER, 1);
            e.getProjectile().getPersistentDataContainer().set(new NamespacedKey(this, "power"), PersistentDataType.INTEGER, e.getBow().getEnchantmentLevel(Enchantment.ARROW_DAMAGE)+1);
            if(getDurability(e.getBow()) > 5) setDamage(e.getEntity(), e.getBow(), 5);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLandMinePlaceByUsingInteract(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clickedBlock = e.getClickedBlock();
        if(clickedBlock == null || clickedBlock.isEmpty() || !clickedBlock.getLocation().add(0, 1, 0).getBlock().isEmpty()) return;
        ItemStack landMine = e.getItem();
        if(landMine == null || landMine.getItemMeta() == null) return;
        if(!landMine.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this, "landmine"), PersistentDataType.INTEGER)) return;
        new LandMine(this, clickedBlock.getLocation().add(0, 1, 0), false);
        consumeUsage(e.getPlayer().getInventory(), landMine, 1);
        e.setCancelled(true);
    }

    public void checkRecipe(Player player) {
        if (bukkitVersion.minor() >= 13 && !playersRecipesTasks.containsKey(player)) {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    List<CondensedCraft> foundAvailableRecipe = registeredRecipes.stream()
                            .filter(c -> c.getRecipes().stream()
                                    .anyMatch(r -> {
                                        Set<Material> inventorySet = Arrays.stream(player.getInventory().getContents())
                                                .filter(Objects::nonNull)
                                                .map(ItemStack::getType)
                                                .collect(Collectors.toSet());
                                        return inventorySet.containsAll(r.getIngredientMap().values().stream().filter(Objects::nonNull).map(ItemStack::getType).toList());
                                    }))
                            .toList();
                    if (!foundAvailableRecipe.isEmpty()) {
                        List<NamespacedKey> discoveredRecipes = foundAvailableRecipe.stream()
                                .flatMap(craft -> craft.getRecipes().stream().map(ShapedRecipe::getKey))
                                .collect(Collectors.toList());
                        player.discoverRecipes(discoveredRecipes);
                    }
                }
            };
            playersRecipesTasks.put(player, task.runTaskTimer(this, 10L, 10L));
        }
    }

    public void reloadConfig() {
        super.reloadConfig();

        saveDefaultConfig();
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
    }
}