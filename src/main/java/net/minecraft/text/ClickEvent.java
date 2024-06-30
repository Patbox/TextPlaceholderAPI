package net.minecraft.text;

import java.util.Locale;

public record ClickEvent(Action action, String value) {
    public Action getAction() {
        return action;
    }

    public String getValue() {
        return this.value;
    }

    public enum Action {
        OPEN_URL, RUN_COMMAND, SUGGEST_COMMAND, COPY_TO_CLIPBOARD, CHANGE_PAGE;

        public String asString() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
