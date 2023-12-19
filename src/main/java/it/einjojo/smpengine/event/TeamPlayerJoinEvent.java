package it.einjojo.smpengine.event;

import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.core.team.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class TeamPlayerJoinEvent extends Event {


    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final SMPPlayer player;
    private final Team team;

    public TeamPlayerJoinEvent(SMPPlayer player, Team team) {
        this.player = player;
        this.team = team;
    }
}
