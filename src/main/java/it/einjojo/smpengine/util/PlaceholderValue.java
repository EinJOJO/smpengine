package it.einjojo.smpengine.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlaceholderValue {
    private final String key;
    private final String value;

    public static PlaceholderValue player(String value) {
        return new PlaceholderValue("%player%", value);
    }
    public static PlaceholderValue world(String value) {
        return new PlaceholderValue("%world%", value);
    }
}
