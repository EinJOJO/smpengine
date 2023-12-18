package it.einjojo.smpengine.core.team;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.database.TeamDatabase;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class TeamManager {

    private final SMPEnginePlugin plugin;
    private final TeamDatabase teamDatabase;

    public TeamManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.teamDatabase = new TeamDatabase(plugin.getHikariCP());
    }

    public Team createTeam(String teamName, SMPPlayer owner) {
        TeamImpl team = new TeamImpl(-1, teamName, Component.text(teamName).color(TeamColor.DEFAULT), owner.getUuid(), Instant.now(), new ArrayList<>());
        applyPlugin(team);
        team.addMember(owner);
        return teamDatabase.createTeam((TeamImpl) team);
    }

    public Optional<Team> getTeamById(int teamId) {
        var team = teamDatabase.getTeam(teamId);
        applyPlugin(team);
        return Optional.of(team);
    }

    public Optional<Team> getTeamByName(String teamName) {
        var team = teamDatabase.getTeamByName(teamName);
        applyPlugin(team);
        return Optional.of(team);
    }

    public boolean deleteTeam(Team team) {
        if (team instanceof TeamImpl teamImpl) {
            for (SMPPlayer member : team.getMembers()) {
                teamImpl.removeMember(member, true);
                plugin.getPlayerManager().updatePlayer(member);
            }
            return teamDatabase.deleteTeam((TeamImpl) team);
        }
        return false;
    }

    private void applyPlugin(Team team) {
        if (team instanceof TeamImpl) {
            ((TeamImpl) team).setPlugin(plugin);
        }
    }

}
