package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAddDuringGame extends AKuffleCommand {
	public KuffleAddDuringGame() {
		super("k-add-during-game", null, true, 1, 2, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		Player retPlayer;

		if ((retPlayer = Utils.searchPlayerByName(args[0])) == null) {
			throw new KuffleCommandFalseException();
		}
		
		if (GameManager.hasSpectator(retPlayer)) {
			LogManager.getInstanceSystem().writeMsg(retPlayer, LangManager.getMsgLang("NO_GAME_ALREADY_SPEC", Config.getLang()));
			throw new KuffleCommandFalseException();
		}

		if (Config.getTeam() && args.length == 2) {
			if (!TeamManager.getInstance().hasTeam(args[1])) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[1] + ">"));
				throw new KuffleCommandFalseException();
			} else if (TeamManager.getInstance().getTeam(args[1]).getPlayers().size() == Config.getTeamSize()) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_FULL", Config.getLang()));
				throw new KuffleCommandFalseException();
			}

			startPlayer(player, retPlayer, args[1]);
		} else if (!Config.getTeam() && args.length == 1) {
			startPlayer(player, retPlayer, null);
		} else {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_PREVENT_ADD", Config.getLang()));			
			throw new KuffleCommandFalseException();
		}
		
		return true;
	}

	/**
	 * Starts a player
	 * 
	 * @param sender	The player that want to start @player
	 * @param player	The player for whom the game will start
	 * @param team		The player's team if needed
	 */
	private void startPlayer(Player sender, Player player, String team) {
		KuffleMain.getInstance().setPaused(true);

		GameManager.addPlayer(sender, player);
		LogManager.getInstanceSystem().writeMsg(sender, LangManager.getMsgLang("ADDED_ONE_LIST", Config.getLang()));

		if (team != null) {
			TeamManager.getInstance().affectPlayer(team, player);
			LogManager.getInstanceSystem().writeMsg(sender, LangManager.getMsgLang("TEAM_ADD_PLAYER", Config.getLang()).replace("<#>", "<" + team + ">").replace("<##>", "<" + player.getName() + ">"));

			GameManager.applyToPlayer(player.getName(), game -> {
				game.setTeamName(team);
				game.setSpawnLoc(GameManager.getPlayerSpawnLoc(TeamManager.getInstance().getTeam(team).getPlayersName().get(0)));
			});

			player.setBedSpawnLocation(GameManager.getPlayerSpawnLoc(player.getName()), true);
			player.teleport(GameManager.getPlayer(TeamManager.getInstance().getTeam(team).getPlayersName().get(0)).getPlayer());
		} else {
			GameManager.applyToPlayer(player.getName(), game -> {
				game.setSpawnLoc(player.getLocation());
				game.getSpawnLoc().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			});
			
			player.setBedSpawnLocation(player.getLocation(), true);
		}

		GameManager.addToPlayersRanks(player.getName());
		player.sendMessage(LangManager.getMsgLang("GAME_STARTED", Config.getLang()));

		GameManager.setupPlayer(player.getName());
		
		ScoreManager.setupPlayerScore(player.getName());
		GameManager.updatePlayersHeads();

		KuffleMain.getInstance().setPaused(false);

		player.getInventory().addItem(KuffleStart.getStartBox(player.getName()));

		if (Config.getSaturation()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
		}
	}
}
