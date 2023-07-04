package fr.paulem.alot.commands;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CMain;
import fr.paulem.alot.blocks.CustomBlock;
import fr.paulem.api.API;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import static fr.paulem.alot.gui.GUIAddedItems.creativeTabInventory;
import static fr.paulem.alot.gui.GUIAddedItems.setCreativeTabInventory;

public class CommandALOT extends CMain implements CommandExecutor {
    public CommandALOT(ALOT main) {
        super(main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        StringBuilder message = new StringBuilder();
        for(String string : main.ALOT_SUBCOMMANDS){
            message.append(string);
            if(main.ALOT_SUBCOMMANDS.indexOf(string) != main.ALOT_SUBCOMMANDS.size()-1) message.append("/");
        }

        String defaultMessage = "Commands : /alot [" + message + "]";

        if (args.length == 0) {
            sender.sendMessage(defaultMessage);
            return true;
        }

        String sub = args[0];

        if (sub.equalsIgnoreCase("reload") || sub.equalsIgnoreCase("rl")) {
            main.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Config successfully reloaded !");
            return true;
        } else if (sub.equalsIgnoreCase("give")){
            if(!(sender instanceof Player player)){
                sender.sendMessage(ChatColor.RED + "You must be a player to do this command !");
                return true;
            }
            if(args.length < 2 || args[1].isEmpty()){
                sender.sendMessage(ChatColor.RED + "You must specify an item !");
                return true;
            }
            ItemStack itemStack = main.registeredItems.get(main.registeredItems.keySet().stream().filter(key -> key.getKey().equals(args[1])).findFirst().orElse(null));
            if(itemStack == null){
                sender.sendMessage(ChatColor.RED + "This item doesn't exist !");
                return true;
            }
            player.getInventory().addItem(itemStack);
            sender.sendMessage(ChatColor.GREEN + (itemStack.getItemMeta() != null ? itemStack.getItemMeta().getDisplayName() : "The item") + ChatColor.GREEN + " has been added to your inventory !");
            return true;
        } else if(sub.equalsIgnoreCase("inventory") || sub.equalsIgnoreCase("inv")){
            if(sender instanceof Player player){
                setCreativeTabInventory(main);
                player.openInventory(creativeTabInventory);
            } else sender.sendMessage(ChatColor.RED + "You must be a player to use this command !");
            return true;
        } else if(sub.equalsIgnoreCase("test")){
            if(sender instanceof Player player){
                API.LaunchItem(main, player.getInventory().getItemInMainHand(), player);
            } else sender.sendMessage(ChatColor.RED + "You must be a player to use this command !");
            return true;
        } else if(sub.equalsIgnoreCase("tt")){
            if(sender instanceof Player player){
                ItemStack item = new ItemStack(Material.BOW, 1);
                ItemMeta im = item.getItemMeta();
                assert im != null;
                im.setCustomModelData(2);
                item.setItemMeta(im);
                new CustomBlock(item, player.getLocation());
            } else sender.sendMessage(ChatColor.RED + "You must be a player to use this command !");
            return true;
        }

        sender.sendMessage(defaultMessage);
        return true;
    }
}
