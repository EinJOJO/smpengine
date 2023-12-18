package it.einjojo.smpengine.core.team;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.database.TeamDatabase;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class TeamManager {

    private final SMPEnginePlugin plugin;
    private final TeamDatabase teamDatabase;

    private final Cache<UUID, Integer> teamInvites;

    public TeamManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.teamDatabase = new TeamDatabase(plugin.getHikariCP());
        teamInvites = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(5)).build();
    }

    /**
     * Creates a team.
     *
     * @param teamName name of the team
     * @param owner    {@link SMPPlayer} who owns the team
     * @return {@link Team} if team was created, null if team was not created (probably because team name already exists)
     */
    public Team createTeam(String teamName, SMPPlayer owner) {
        teamDatabase.createTeam(new TeamImpl(-1, teamName, Component.text(teamName).color(TeamColor.DEFAULT), owner.getUuid(), Instant.now(), new ArrayList<>()));
        Team result = getTeamByName(teamName).orElse(null);
        if (result != null) {
            plugin.getLogger().info("Created team " + teamName + " (" + result.getId() + ")");
            applyPlugin(result);
            result.addMember(owner);
        }

        return result;
    }

    /**
     * Gets a team by id.
     *
     * @param teamId id of the team
     * @return {@link Optional} of {@link Team} if team exists, {@link Optional#empty()} if team does not exist
     */
    public Optional<Team> getTeamById(int teamId) {
        var team = teamDatabase.getTeam(teamId);
        applyPlugin(team);
        return Optional.ofNullable(team);
    }

    /**
     * Gets a team by name.
     *
     * @param teamName name of the team
     * @return {@link Optional} of {@link Team} if team exists, {@link Optional#empty()} if team does not exist
     */
    public Optional<Team> getTeamByName(String teamName) {
        var team = teamDatabase.getTeamByName(teamName);
        applyPlugin(team);
        return Optional.ofNullable(team);
    }

    /**
     * Deletes a team from the database and removes all members from the team.
     *
     * @param team {@link Team} to delete
     * @return true if team was deleted, false if team was not deleted
     */
    public boolean deleteTeam(Team team) {
        if (team instanceof TeamImpl teamImpl) {
            for (SMPPlayer member : team.getMembers()) {
                teamImpl.removeMember(member, true);
                Player player = member.getPlayer();
                if (player != null) {
                    player.sendMessage(plugin.getMessage("command.team.delete.member-info"));
                }
                plugin.getPlayerManager().updatePlayer(member); // Update player in database
            }
            plugin.getLogger().info("Deleted team " + team.getName() + " (" + team.getId() + ")");
            return teamDatabase.deleteTeam((TeamImpl) team);

        }
        return false;
    }

    public void updateTeam(Team team) {
        if (team == null) return;
        teamDatabase.updateTeam(team);
    }

    public void createInvite(UUID uuid, int teamId) {
        teamInvites.put(uuid, teamId);
    }

    public Optional<Integer> getInvite(UUID uuid) {
        return Optional.ofNullable(teamInvites.getIfPresent(uuid));
    }

    public void removeInvite(UUID uuid) {
        teamInvites.invalidate(uuid);
    }


    private void applyPlugin(Team team) {
        if (team instanceof TeamImpl) {
            ((TeamImpl) team).setPlugin(plugin);
        }
    }

    public ArrayList<String> getTeams() {
        return teamDatabase.getTeams();
    }

}
