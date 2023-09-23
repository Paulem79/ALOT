package fr.paulem.alot;

import fr.paulem.alot.block.BlockHandler;
import fr.paulem.alot.block.CondensedBlock;
import fr.paulem.alot.block.blocks.LandMine;
import fr.paulem.alot.commands.CommandALOT;
import fr.paulem.alot.commands.tabcompletes.TabCommandALOT;
import fr.paulem.alot.gui.GUIAddedItems;
import fr.paulem.alot.item.ItemHandler;
import fr.paulem.alot.listeners.ListenerBlood;
import fr.paulem.alot.listeners.ListenerHealthDisplay;
import fr.paulem.alot.listeners.ListenerHerobrine;
import fr.paulem.alot.listeners.ListenerItemInBack;
import fr.paulem.alot.listeners.ListenerItems;
import fr.paulem.alot.listeners.ListenerPloof;
import fr.paulem.api.classes.CondensedCraft;
import fr.paulem.api.enums.VersionMethod;
import fr.paulem.api.functions.LibRadius;
import fr.paulem.api.radios.LibVersion;
import fr.paulem.nmsgate.NmsGate;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.paulem.alot.block.blocks.LandMine.newLandMine;
import static fr.paulem.api.API.registerEvents;
import static fr.paulem.api.functions.LibDamage.consumeUsage;
import static fr.paulem.api.functions.LibDamage.dealDamage;
import static fr.paulem.api.functions.LibDamage.getDurability;
import static fr.paulem.api.functions.LibDamage.setDamage;
import static fr.paulem.api.functions.LibOther.RandomBtw;
import static fr.paulem.api.functions.LibOther.isDisplaySupported;
import static fr.paulem.api.radios.LibVersion.getVersion;
import static fr.paulem.paperapi.PaperAPI.initPaper;

public class ALOT extends JavaPlugin implements CommandExecutor, Listener {
    public static final LibVersion serverVersion = getVersion(VersionMethod.SERVER);
    public static final LibVersion bukkitVersion = getVersion(VersionMethod.BUKKIT);
    public static ALOT instance;
    public static boolean firstWorldLoaded = false;
    public final List<LandMine> landMines = new ArrayList<>();
    public final HashMap<NamespacedKey, ItemStack> registeredItems = new HashMap<>();
    public final List<CondensedCraft> registeredRecipes = new ArrayList<>(); // ItemStack + Recipes
    public static int lastEntityId = 50000;
    public final HashMap<Player, BukkitTask> playersRecipesTasks = new HashMap<>();
    public static NamespacedKey hologramKey;
    public static NamespacedKey healthbarKey;
    public static NamespacedKey customBlockKey;
    public static FileConfiguration config;
    public final List<String> ALOT_SUBCOMMANDS = List.of("give", "reload", "inventory");
    public final List<CondensedBlock> registeredBlocks = new ArrayList<>(); // ItemStack + Faces
    public final List<Material> CUSTOM_BLOCKS = List.of(Material.MUSHROOM_STEM, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK);

    public static ALOT getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        config = getConfig();
        instance = this;
        hologramKey = new NamespacedKey(this, "hologram");
        healthbarKey = new NamespacedKey(this, "healthbar");
        customBlockKey = new NamespacedKey(this, "customblock");

        registerNametagInvisibleTeam();

        Metrics metrics = new Metrics(this, 19149);

        if (!serverVersion.equals(bukkitVersion))
            getLogger().warning("serverVersion and bukkitVersion aren't equals ! That is weird, plugin may not work correctly");

        registerEvents(this, NmsGate.nmsListeners(this));
        registerEvents(this, new ListenerHerobrine(this));
        registerEvents(this, new ListenerPloof(this));
        registerEvents(this, new ListenerHealthDisplay(this));
        registerEvents(this, new ListenerBlood(this));
        registerEvents(this, new GUIAddedItems(this));
        registerEvents(this, new ListenerItems(this));
        if (isDisplaySupported()) registerEvents(this, new ListenerItemInBack(this));
        registerEvents(this, this);

        initPaper(this);

        Objects.requireNonNull(getCommand("alot")).setExecutor(new CommandALOT(this));
        Objects.requireNonNull(getCommand("alot")).setTabCompleter(new TabCommandALOT(this));

        if (isDisplaySupported())
            for (ItemDisplay itemDisplay : LibRadius.getEntitiesInAllWorlds(ent -> ent instanceof ItemDisplay && ent.getPersistentDataContainer().has(new NamespacedKey(this, "itemOnBack"), PersistentDataType.INTEGER) && ent.getPersistentDataContainer().has(new NamespacedKey(this, "attachedPlayer"), PersistentDataType.STRING), it -> (ItemDisplay) it).toList()) {
                itemDisplay.remove();
            }

        if (isDisplaySupported())
            for (Entity textDisplay : LibRadius.getEntitiesInAllWorlds(txt -> (txt instanceof ArmorStand || txt instanceof TextDisplay) && txt.getPersistentDataContainer().has(hologramKey, PersistentDataType.INTEGER))
                    .toList()) {
                textDisplay.remove();
            }

        if (bukkitVersion.minor() >= 14) new ItemHandler(this);
        new BlockHandler(this);

        if (isDisplaySupported()) landMines.addAll(LibRadius.getEntitiesInAllWorlds(
                        e -> e instanceof ItemDisplay itemDisplay && itemDisplay.getPersistentDataContainer().has(new NamespacedKey(ALOT.this, "landmine"), PersistentDataType.INTEGER),
                        e -> newLandMine(this, (ItemDisplay) e))
                .toList()
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            checkRecipe(player);
        }

        if (isDisplaySupported()) new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!landMines.isEmpty()) for (LandMine landMine : landMines) {
                        if (landMine.getLandMineEntity().isDead()) landMines.remove(landMine);
                        if (!LibRadius.getEntitiesAroundLocation(landMine.getLocationLandMine(), .5, 1, .5).stream().filter(e -> !(e instanceof ItemDisplay) && (!(e instanceof Player player) || player.getGameMode() != GameMode.SPECTATOR || player.getGameMode() != GameMode.CREATIVE)).toList().isEmpty())
                            landMine.explose();
                    }
                } catch (ConcurrentModificationException ignored) {
                }
            }
        }.runTaskTimer(this, 1L, 5L);

        if (isDisplaySupported()) new BukkitRunnable() {
            @Override
            public void run() {
                LibRadius.getEntitiesInAllWorlds(
                        ent -> ent instanceof ItemDisplay && ent.getPersistentDataContainer().has(customBlockKey, PersistentDataType.INTEGER) && ent.getLocation().getBlock().isEmpty(),
                        it -> {
                            it.remove();
                            return (ItemDisplay) it;
                        }
                );
            }
        }.runTaskTimer(this, 1L, 1L);

        getLogger().info("Plugin activated !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin deactivated !");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        checkRecipe(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (playersRecipesTasks.containsKey(e.getPlayer())) playersRecipesTasks.get(e.getPlayer()).cancel();
    }

    @EventHandler
    public void onFirstWorldLoad(WorldLoadEvent e) {
        registerNametagInvisibleTeam();
    }

    public void registerNametagInvisibleTeam() {
        if (firstWorldLoaded) return;
        Team isPresent = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeam("nhide");
        if (isPresent != null) return;
        Team t = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().registerNewTeam("nhide");
        t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        firstWorldLoaded = Bukkit.getScoreboardManager() != null;
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow arrow)) return;
        if (arrow.getPersistentDataContainer().has(new NamespacedKey(this, "switch_bow"), PersistentDataType.INTEGER)) {
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
    public void onLandMinePlaceByUsingInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack landMine = e.getItem();
        if (landMine == null || landMine.getItemMeta() == null) return;
        if (!landMine.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this, "landmine"), PersistentDataType.INTEGER))
            return;
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null || clickedBlock.isEmpty() || !clickedBlock.getRelative(BlockFace.UP).isEmpty()) {
            e.setCancelled(true);
            return;
        }
        new LandMine(this, clickedBlock.getLocation().add(.5, 1.5, .5), false);
        consumeUsage(e.getPlayer().getInventory(), landMine, 1);
        e.setCancelled(true);
    }

    @EventHandler
    public void onMushroomBlockUpdate(BlockPhysicsEvent e) {
        if (CUSTOM_BLOCKS.contains(e.getBlock().getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMushroomBlockPush(BlockPistonExtendEvent e) {
        for (Block block : e.getBlocks()) cancelMushroomStateChange(block, e.getDirection());
    }

    @EventHandler
    public void onMushroomBlockRetract(BlockPistonRetractEvent e) {
        for (Block block : e.getBlocks()) cancelMushroomStateChange(block, e.getDirection());
    }

    @EventHandler
    public void onMushroomBreak(BlockBreakEvent e) {
        e.setDropItems(false);
        customBlockBreak(e.getBlock(), e.getPlayer().getGameMode() == GameMode.SURVIVAL);
    }

    @EventHandler
    public void onTntExplodeCustomBlock(BlockExplodeEvent e) {
        for (Block block : e.blockList()) {
            customBlockBreak(block, true);
        }
    }

    @EventHandler
    public void onCreeperExplodeCustomBlock(EntityExplodeEvent e) {
        for (Block block : e.blockList()) {
            customBlockBreak(block, true);
        }
    }

    public void customBlockBreak(Block block, boolean dropBlock) {
        if (!CUSTOM_BLOCKS.contains(block.getType())) return;
        MultipleFacing facing = (MultipleFacing) block.getBlockData();
        CondensedBlock customBlock = registeredBlocks.stream()
                .filter(b -> facing.getFaces().equals(b.getFaces()))
                .findFirst()
                .orElse(null);
        if (customBlock == null) return;
        if (dropBlock) block.getWorld().dropItemNaturally(block.getLocation(), customBlock.getAssociedItem());
    }

    public void cancelMushroomStateChange(Block mushroom, BlockFace pistonFace) {
        if (!CUSTOM_BLOCKS.contains(mushroom.getType())) return;
        MultipleFacing facing = (MultipleFacing) mushroom.getBlockData();
        new BukkitRunnable() {
            @Override
            public void run() {
                mushroom.getRelative(pistonFace).setBlockData(facing, false);
            }
        }.runTaskLater(this, 2L);
    }

    @EventHandler
    public void onMushroomBlockPlace(BlockPlaceEvent e) {
        if (!CUSTOM_BLOCKS.contains(e.getBlock().getType())) return;
        BlockData data = e.getBlock().getBlockData();
        MultipleFacing facing = (MultipleFacing) data;
        CondensedBlock block = registeredBlocks.stream()
                .filter(b -> e.getItemInHand().getItemMeta() != null && b.getAssociedItem().getItemMeta() != null && e.getItemInHand().getItemMeta().hasCustomModelData() && b.getAssociedItem().getItemMeta().getCustomModelData() == e.getItemInHand().getItemMeta().getCustomModelData() && e.getItemInHand().getType() == e.getBlock().getType() && b.getAssociedItem().getType() == e.getBlock().getType())
                .findFirst()
                .orElse(null);
        if (block != null) {
            //Le bloc est bien custom
            for (BlockFace face : facing.getAllowedFaces()) {
                facing.setFace(face, block.getFaces().contains(face));
            }
            e.getBlock().setBlockData(facing, false);
        } else {
            //Le bloc n'est pas custom, mettre par défaut
            facing.setFace(BlockFace.DOWN, true);
            facing.setFace(BlockFace.EAST, true);
            facing.setFace(BlockFace.NORTH, true);
            facing.setFace(BlockFace.SOUTH, true);
            facing.setFace(BlockFace.UP, true);
            facing.setFace(BlockFace.WEST, true);
            e.getBlock().setBlockData(facing, false);
        }
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
}