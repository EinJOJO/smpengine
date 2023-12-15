package it.einjojo.smpengine.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

@AllArgsConstructor
@Getter
public class Placeholder {

    private final String key;
    private final String value;


    public static Component applyPlaceholders(Component component, Placeholder... placeholders) {
        TextReplacementConfig.Builder configBuilder = TextReplacementConfig.builder();
        for (var placeholder : placeholders) {
            configBuilder.matchLiteral("{" + placeholder.getKey() + "}")
                    .replacement(placeholder.getValue());
        }
        return component.replaceText(configBuilder.build());
    }

    public static Placeholder player(String value) {
        return new Placeholder("player", value);
    }

    public static Placeholder world(String value) {
        return new Placeholder("world", value);
    }
}
