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
        for (var placeholder : placeholders) {
            TextReplacementConfig.Builder configBuilder = TextReplacementConfig.builder();
            configBuilder.matchLiteral("{" + placeholder.getKey() + "}")
                    .replacement(placeholder.getValue());
            component = component.replaceText(configBuilder.build());
        }
        return component;
    }

    public static Placeholder player(String value) {
        return new Placeholder("player", value);
    }

    public static Placeholder world(String value) {
        return new Placeholder("world", value);
    }
}
