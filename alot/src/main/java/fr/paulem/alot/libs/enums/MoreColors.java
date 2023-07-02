package fr.paulem.alot.libs.enums;

import net.md_5.bungee.api.ChatColor;

import static fr.paulem.alot.ALOT.bukkitVersion;

public enum MoreColors {;
    final ChatColor color;

    MoreColors(ChatColor color, ChatColor elseColor) {
        this.color = bukkitVersion.minor() >= 16 ? color : elseColor;
    }

    public ChatColor getColor() {
        return color;
    }
}
