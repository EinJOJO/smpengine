package it.einjojo.smpengine.event;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class TeamPlayerLeaveEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final SMPPlayer player;
    private final Team team;

    public TeamPlayerLeaveEvent(SMPPlayer player, Team team) {
        this.player = player;
        this.team = team;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
