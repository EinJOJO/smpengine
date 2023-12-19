package it.einjojo.smpengine.core.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;

@Getter
@AllArgsConstructor
public class TeamColor implements TextColor {

    public static final TeamColor DEFAULT = new TeamColor(TextColor.color(0xFF8527));
    public static final TeamColor RED = new TeamColor(TextColor.color(0xFF4B52));
    public static final TeamColor BLUE = new TeamColor(TextColor.color(0x5555FF));
    public static final TeamColor GREEN = new TeamColor(TextColor.color(0x55FF55));

    private final TextColor color;

    public int value() {
        return color.value();
    }

}
