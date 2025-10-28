package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.core.Team;
import fr.kosmosuniverse.kuffle.core.TeamManager;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamTab extends AKuffleTabCommand {
    private static final Map<String, Function<String[], List<String>>> baseParams = new HashMap<>();
    private static final Map<String, Function<String[], List<String>>> playerParams = new HashMap<>();

	public KuffleTeamTab() {
		super();

        baseParams.put("show", (String[] args) -> Collections.emptyList());
        baseParams.put("load", (String[] args) -> Collections.emptyList());
        baseParams.put("save", (String[] args) -> Collections.emptyList());
        baseParams.put("random", (String[] args) -> Collections.emptyList());
        baseParams.put("clear", (String[] args) -> Collections.emptyList());
        baseParams.put("create", (String[] args) -> createTeam());
        baseParams.put("delete", (String[] args) -> deleteTeam());
        baseParams.put("players", (String[] args) -> teamPlayers());

        playerParams.put("reset", (String[] args) -> teamReset());
        playerParams.put("add", (String[] args) -> addPlayer());
        playerParams.put("remove", (String[] args) -> removePlayer());
        playerParams.put("color", (String[] args) -> teamColor());
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(baseParams.keySet());
		} else if (baseParams.containsKey(currentArgs[0])) {
            ret.addAll(baseParams.get(currentArgs[0]).apply(currentArgs));
        }
	}

    private List<String> createTeam() {
        if (currentArgs.length == 3) {
            List<String> colorUsed = TeamManager.getInstance().getTeamColors();

            return Arrays.stream(ChatColor.values()).map(ChatColor::name).filter(c -> !colorUsed.contains(c)).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private List<String> deleteTeam() {
        if (currentArgs.length == 2) {
            return TeamManager.getInstance().getTeams().stream().map(Team::getName).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private List<String> teamPlayers() {
        if (currentArgs.length == 2) {
            return TeamManager.getInstance().getTeams().stream().map(Team::getName).collect(Collectors.toList());
        } else if (currentArgs.length == 3) {
            return new ArrayList<>(playerParams.keySet());
        } else {
            return playerParams.get(currentArgs[2]).apply(currentArgs);
        }
    }

    private List<String> teamReset() {
        return Collections.emptyList();
    }

    private List<String> addPlayer() {
        return Party.getInstance().getPlayers().getList().stream().filter(p -> !TeamManager.getInstance().isInTeam(p)).collect(Collectors.toList());
    }

    private List<String> removePlayer() {
        return TeamManager.getInstance().getTeam(currentArgs[1]).getPlayers().isEmpty() ? Collections.emptyList() : TeamManager.getInstance().getTeam(currentArgs[1]).getPlayers();
    }

    private List<String> teamColor() {
        List<String> colorUsed = TeamManager.getInstance().getTeamColors();

        return Arrays.stream(ChatColor.values()).map(ChatColor::name).filter(c -> !colorUsed.contains(c)).collect(Collectors.toList());
    }
}
