package fr.paulem.alot.commands.tabcompletes;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.libs.classes.CMain;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabCommandALOT extends CMain implements TabCompleter {
    public TabCommandALOT(ALOT main) {
        super(main);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

        if(args.length >= 2 && args[0].equalsIgnoreCase("give")){
            StringUtil.copyPartialMatches(args[1], main.registeredItems.keySet().stream().map(NamespacedKey::getKey).collect(Collectors.toList()), completions);

            return completions;
        }

        StringUtil.copyPartialMatches(args[0], main.ALOT_SUBCOMMANDS, completions);

        return completions;
    }
}
