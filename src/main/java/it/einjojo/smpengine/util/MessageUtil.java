package it.einjojo.smpengine.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.TreeMap;

@UtilityClass
public class MessageUtil {
    public static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

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

    public static String toRoman(int number) {
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }

}
