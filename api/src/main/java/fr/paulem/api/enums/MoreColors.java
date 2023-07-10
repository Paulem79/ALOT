package fr.paulem.api.enums;

import net.md_5.bungee.api.ChatColor;

import static fr.paulem.api.radios.LibVersion.getVersion;

public enum MoreColors {;
    final ChatColor color;

    MoreColors(ChatColor color, ChatColor elseColor) {
        this.color = getVersion(VersionMethod.BUKKIT).minor() >= 16 ? color : elseColor;
    }

    public ChatColor getColor() {
        return color;
    }
}
