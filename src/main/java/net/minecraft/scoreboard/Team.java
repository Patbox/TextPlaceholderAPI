package net.minecraft.scoreboard;

import net.minecraft.text.Text;

public class Team {
    public String getName() {
        return "team";
    }

    public Text getDisplayName() {
        return Text.literal("<TEAM>");
    }

    public Text getFormattedName() {
        return getDisplayName();
    }
}
