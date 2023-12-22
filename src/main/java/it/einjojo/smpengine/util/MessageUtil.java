package it.einjojo.smpengine.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class MessageUtil {
    public static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Getter
    public enum KEY {
        NO_PERMISSION("no-permission"),
        COMMAND_TARGET_NOT_FOUND("command.targetNotFound"),
        GENERAL_ERROR("general-error");

        private final String key;

        KEY(String key) {
            this.key = key;
        }

    }

    public static Component format(String message, TextColor primaryColor, Component prefix) {
        message = legacyFormat(message);
        return miniMessage.deserialize(
                message,
                Placeholder.styling("pkr", primaryColor),
                Placeholder.component("prefix", prefix)
        );
    }

    public static Component format(String message, TextColor primaryColor) {
        message = legacyFormat(message);
        return miniMessage.deserialize(
                message,
                Placeholder.styling("pkr", primaryColor)
        );
    }

    public static Component format(String message) {
        message = legacyFormat(message);
        return miniMessage.deserialize(message);
    }

    public static String legacyFormat(String message) {
        if (message.contains("&")) {
            Component legacy = LegacyComponentSerializer.legacy('&').deserialize(message);
            message = miniMessage.serialize(legacy);
        }
        return message;
    }

}
