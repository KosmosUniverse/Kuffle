package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.utils.CommandUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author KosmosUniverse
 *
 */
public class KuffleTeam extends AKuffleCommand {
    private static Map<String, Function<String[], Boolean>> baseParams = null;
    private String error = null;

	public KuffleTeam() {
		super("k-team", null, false, 1, 4, true);

        baseParams = new HashMap<>();

        baseParams.put("load", (String[] args) -> loadTeams());
        baseParams.put("save", (String[] args) -> saveTeams());
        baseParams.put("random", (String[] args) -> randomTeams());
        baseParams.put("clear", (String[] args) -> clearTeams());
        baseParams.put("delete", this::deleteTeam);
        baseParams.put("create", this::createTeam);
        baseParams.put("players", this::teamPlayers);
        baseParams.put("players-reset", (String[] args) -> resetPlayers());
        baseParams.put("players-add", this::teamAddPlayers);
        baseParams.put("players-remove", this::teamRemovePlayers);
        baseParams.put("players-color", this::teamColor);
	}

    @Override
	public boolean runCommand() {
		if ("show".equals(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, TeamManager.getInstance().printTeams());
			return true;
		}

        if (!baseParams.containsKey(args[0])) {
            return false;
        }

		if (!player.hasPermission("k-op")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return true;
		}

        return processOptions(args);
	}

    /**
     * Process cmd options
     *
     * @param args The cmd args
     * @return True if success, False instead.
     */
    private boolean processOptions(String[] args) {
        boolean ret = baseParams.get(args[0]).apply(args);

        if (!ret && error != null) {
            LogManager.getInstanceSystem().writeMsg(player, error);
            error = null;
        } else return ret;

        return true;
    }

    /**
     * Method to load Teams from file
     *
     * @return True if success, False instead.
     */
    private boolean loadTeams() {
        if (args.length != 1) {
            return false;
        }

        if (!TeamManager.getInstance().getTeams().isEmpty()) {
            error = LangManager.getMsgLang("TEAMS_ALREADY_EXISTS", Config.getLang());
            return false;
        }

        try {
            TeamManager.getInstance().loadTeamsConfig(player, KuffleMain.getInstance().getDataFolder() + File.separator + "teamconfig.json");
        } catch (IOException e) {
            Utils.logException(e);
            error = LangManager.getMsgLang("TEAMS_LOAD_FAILED", Config.getLang());
            return false;
        }

        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAMS_LOADED", Config.getLang()));

        return true;
    }

    /**
     * Method to save Teams to file
     *
     * @return True if success, False instead.
     */
    private boolean saveTeams() {
        if (args.length != 1) {
            return false;
        }

        if (TeamManager.getInstance().getTeams().isEmpty()) {
            error = LangManager.getMsgLang("NO_TEAMS_SAVE", Config.getLang());
            return false;
        }

        try {
            TeamManager.getInstance().saveTeamsConfig(KuffleMain.getInstance().getDataFolder() + File.separator + "teamconfig.json");
        } catch (IOException e) {
            Utils.logException(e);
            error = LangManager.getMsgLang("TEAMS_SAVE_FAILED", Config.getLang());
            return false;
        }

        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAMS_SAVED", Config.getLang()));

        return true;
    }

    /**
     * Add players randomly into the teams
     *
     * @return True if success, False instead.
     */
    private boolean randomTeams() {
        if (args.length != 1) {
            return false;
        }

        if (Party.getInstance().getPlayers().getList().isEmpty()) {
            error = LangManager.getMsgLang("LIST_EMPTY", Config.getLang());
            return false;
        }

        if (calcMaxPlayers() < Party.getInstance().getPlayers().getList().size()) {
            error = LangManager.getMsgLang("TEAM_TOO_MANY_PLAYERS", Config.getLang());
            return false;
        }

        if (!checkEmptyTeams()) {
            error = LangManager.getMsgLang("TEAM_ALREADY_PLAYERS", Config.getLang());
            return false;
        }

        int cnt = 0;
        List<String> players = new ArrayList<>(Party.getInstance().getPlayers().getList());

        final SecureRandom random = new SecureRandom();

        while (!players.isEmpty()) {
            int idx = random.nextInt(players.size());

            TeamManager.getInstance().affectPlayer(TeamManager.getInstance().getTeams().get(cnt).getName(), players.get(idx));

            players.remove(idx);

            cnt++;

            if (cnt >= TeamManager.getInstance().getTeams().size()) {
                cnt = 0;
            }
        }

        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("RANDOM", Config.getLang()).replace("%i", String.valueOf(Party.getInstance().getPlayers().getList().size())).replace("%j", String.valueOf(TeamManager.getInstance().getTeams().size())));

        return true;
    }

    /**
     * Calculates the max amount of players allowed with config and team amount
     *
     * @return the multiplication of Config team size and team number
     */
    public int calcMaxPlayers() {
        return (Config.getTeamSize() * TeamManager.getInstance().getTeams().size());
    }

    /**
     * Checks if any team is empty
     *
     * @return True if all team are empty, False instead
     */
    public boolean checkEmptyTeams() {
        return TeamManager.getInstance().getTeams().stream().allMatch(t -> t.getPlayers().isEmpty());
    }

    /**
     * Method to clear all teams
     *
     * @return True if success, False instead.
     */
    private boolean clearTeams() {
        if (args.length != 1) {
            return false;
        }

        TeamManager.getInstance().clear();
        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAMS_CLEARED", Config.getLang()));
        return true;
    }

    /**
     * Methods to delete a Team
     *
     * @param args The cmd args that contains the Team to delete
     * @return True if success, False instead.
     */
    private boolean deleteTeam(String[] args) {
        if (args.length != 2) {
            return false;
        }

        if (!TeamManager.getInstance().hasTeam(args[1])) {
            error = LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[1] + ">");
            return false;
        }

        TeamManager.getInstance().deleteTeam(args[1]);

        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_DELETED", Config.getLang()).replace("<#>", "<" + args[1] + ">"));

        return true;
    }

    /**
     * Method to create a Team
     *
     * @param args The cmd args that contains the Team to create
     * @return True if success, False instead.
     */
    private boolean createTeam(String[] args) {
        if (args.length < 2 || args.length > 3) {
            return false;
        }

        if (TeamManager.getInstance().hasTeam(args[1])) {
            error = LangManager.getMsgLang("TEAM_EXISTS", Config.getLang()).replace("<#>", "<" + args[1] + ">");
            return false;
        }

        if (args.length == 2) {
            TeamManager.getInstance().createTeam(args[1]);
            LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_CREATED", Config.getLang()).replace("<#>", "<" + args[1] + ">"));
        } else {
            ChatColor tmp = CommandUtils.checkTeamColor(player, args[2]);

            if (tmp == null) {
                return false;
            }

            TeamManager.getInstance().createTeam(args[1], tmp);

            LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_CREATED", Config.getLang()).replace("<#>", "<" + args[1] + ">"));
        }

        return true;
    }

    /**
     * Method to operate on teams config
     *
     * @param args The cmd args that contains the Team config
     * @return True if success, False instead.
     */
    private boolean teamPlayers(String[] args) {
        if (args.length < 3 || args.length > 4) {
            return false;
        }

        if (!TeamManager.getInstance().hasTeam(args[1])) {
            error = LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[1] + ">");
            return false;
        }

        if (!baseParams.containsKey("players-" + args[2])) {
            return false;
        }

        return baseParams.get("players-" + args[2]).apply(args);
    }

    /**
     * Method to reset a Team players
     *
     * @return True if success, False instead.
     */
    private boolean resetPlayers() {
        if (args.length != 3) {
            return false;
        }

        TeamManager.getInstance().getTeam(args[1]).getPlayers().clear();
        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_RESETED", Config.getLang()).replace("<#>", "<" + args[1] + ">"));

        return true;
    }

    /**
     * Method to add a Player to a Team
     *
     * @param args The cmd args that contains the player to add
     * @return True if success, False instead.
     */
    private boolean teamAddPlayers(String[] args) {
        if (args.length != 4) {
            return false;
        }

        if (TeamManager.getInstance().getTeam(args[1]).getPlayers().size() == Config.getTeamSize()) {
            error = LangManager.getMsgLang("TEAM_FULL", Config.getLang());
            return false;
        }

        if (!Party.getInstance().getPlayers().has(args[3])) {
            error = LangManager.getMsgLang("PLAYER_NOT_IN_GAME", Config.getLang());
            return false;
        }

        if (TeamManager.getInstance().getTeam(args[1]).hasPlayer(args[3])) {
            error = LangManager.getMsgLang("TEAM_PLAYER", Config.getLang());
            return false;
        }

        TeamManager.getInstance().affectPlayer(args[1], args[3]);
        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_ADD_PLAYER", Config.getLang()).replace("<#>", "<" + args[3] + ">").replace("<##>", "<" + args[1] + ">"));

        return true;
    }

    /**
     * Method to remove a Player from a Team
     *
     * @param args The cmd args that contains the player to remove
     * @return True if success, False instead.
     */
    private boolean teamRemovePlayers(String[] args) {
        if (args.length != 4) {
            return false;
        }

        if (!TeamManager.getInstance().getTeam(args[1]).hasPlayer(args[3])) {
            error = LangManager.getMsgLang("TEAM_NO_PLAYER", Config.getLang());
            return false;
        }

        TeamManager.getInstance().removePlayer(args[1], Bukkit.getPlayer(args[3]));
        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_REMOVED", Config.getLang()).replace("<#>", "<" + args[3] + ">").replace("<##>", "<" + args[1] + ">"));

        return true;
    }

    /**
     * Method to change the Color of a team
     *
     * @param args The cmd args that contains the color to change
     * @return True if success, False instead.
     */
    private boolean teamColor(String[] args) {
        if (args.length != 4) {
            return false;
        }

        if (TeamManager.getInstance().getTeam(args[1]).hasPlayer(args[3])) {
            error = LangManager.getMsgLang("TEAM_PLAYER", Config.getLang());
            return false;
        }

        ChatColor tmp = CommandUtils.checkTeamColor(player, args[3]);

        if (tmp == null) {
            return false;
        }

        String tmpColor = TeamManager.getInstance().getTeam(args[1]).getColor().name();

        TeamManager.getInstance().changeTeamColor(args[1], tmp);
        LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("COLOR_CHANGED", Config.getLang()).replace("[#]", "[" + tmpColor + "]").replace("[##]", "[" + tmp.name() + "]").replace("<#>",	"<" + args[1] + ">"));

        return true;
    }
}
