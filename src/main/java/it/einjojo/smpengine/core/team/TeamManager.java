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

    /**
     * Creates a team.
     * @param teamName name of the team
     * @param owner {@link SMPPlayer} who owns the team
     * @return {@link Team} if team was created, null if team was not created (probably because team name already exists)
     */
    public Team createTeam(String teamName, SMPPlayer owner) {
        TeamImpl team = new TeamImpl(-1, teamName, Component.text(teamName).color(TeamColor.DEFAULT), owner.getUuid(), Instant.now(), new ArrayList<>());
        applyPlugin(team);
        team.addMember(owner);
        return teamDatabase.createTeam((TeamImpl) team);
    }

    /**
     * Gets a team by id.
     * @param teamId id of the team
     * @return  {@link Optional} of {@link Team} if team exists, {@link Optional#empty()} if team does not exist
     */
    public Optional<Team> getTeamById(int teamId) {
        var team = teamDatabase.getTeam(teamId);
        applyPlugin(team);
        return Optional.of(team);
    }

    /**
     * Gets a team by name.
     * @param teamName name of the team
     * @return  {@link Optional} of {@link Team} if team exists, {@link Optional#empty()} if team does not exist
     */
    public Optional<Team> getTeamByName(String teamName) {
        var team = teamDatabase.getTeamByName(teamName);
        applyPlugin(team);
        return Optional.of(team);
    }

    /**
     * Deletes a team from the database and removes all members from the team.
     * @param team {@link Team} to delete
     * @return true if team was deleted, false if team was not deleted
     */
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
