package it.einjojo.smpengine.core.team;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.core.player.SMPPlayer;
import it.einjojo.smpengine.database.TeamDatabase;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TeamManager {

    private final SMPEnginePlugin plugin;
    private final TeamDatabase teamDatabase;
    private final LoadingCache<Integer, Team> teamCache;
    private final Cache<String, Integer> teamIds;
    private final Cache<UUID, Integer> teamInvites;

    public TeamManager(SMPEnginePlugin plugin) {
        this.plugin = plugin;
        this.teamDatabase = new TeamDatabase(plugin.getHikariCP());
        teamInvites = Caffeine.newBuilder()
                .evictionListener((key, value, cause) -> {
                    if (key == null) return;
                    Player player = Bukkit.getPlayer((UUID) key);
                    if (player != null) {
                        player.sendMessage(plugin.getMessage("command.team.invite.expired"));
                    }
                })
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
        teamIds = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(5))
                .build();
        teamCache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(5))
                .build(this::getTeam);

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
            applyPlugin(result);
            plugin.getLogger().info("Created team " + teamName + " (" + result.getId() + ")");
            result.addMember(owner);
        }

        return result;
    }

    private Team getTeam(int teamId) {
        var team = teamDatabase.getTeam(teamId);
        applyPlugin(team);
        return team;
    }

    /**
     * Gets a team by id.
     *
     * @param teamId id of the team
     * @return {@link Optional} of {@link Team} if team exists, {@link Optional#empty()} if team does not exist
     */
    public Optional<Team> getTeamById(int teamId) {
        return Optional.ofNullable(teamCache.get(teamId));
    }

    /**
     * Gets a team by name.
     *
     * @param teamName name of the team
     * @return {@link Optional} of {@link Team} if team exists, {@link Optional#empty()} if team does not exist
     */
    public Optional<Team> getTeamByName(String teamName) {
        if (teamName == null) return Optional.empty();
        Integer teamId = teamIds.get(teamName, s -> {
            int id = teamDatabase.getTeamIdByName(s);
            if (id == -1) return null;
            return id;
        });
        if (teamId == null) return Optional.empty();
        teamIds.put(teamName, teamId);
        return getTeamById(teamId);
    }

    public CompletableFuture<Optional<Team>> getTeamByNameAsync(String teamName) {
        return CompletableFuture.supplyAsync(() -> getTeamByName(teamName)).exceptionally(e -> {
            e.printStackTrace();
            return Optional.empty();
        });
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
            }
            plugin.getLogger().info("Deleted team " + team.getName() + " (" + team.getId() + ")");
            teamIds.invalidate(team.getName());
            teamCache.invalidate(team.getId());
            return teamDatabase.deleteTeam(team);
        }
        return false;
    }

    public void updateTeam(Team team) {
        if (team == null) return;
        teamDatabase.updateTeam(team);
        teamCache.invalidate(team.getId());
    }

    public void createInvite(UUID player, Team team) {
        teamInvites.put(player, team.getId());
    }

    public Optional<Integer> getInvite(UUID player) {
        return Optional.ofNullable(teamInvites.getIfPresent(player));
    }

    public void removeInvite(UUID player) {
        teamInvites.invalidate(player);
    }


    private void applyPlugin(Team team) {
        if (team instanceof TeamImpl) {
            ((TeamImpl) team).setPlugin(plugin);
        }
    }

    public ArrayList<String> getTeamNames() {
        return teamDatabase.getTeams();
    }

}
