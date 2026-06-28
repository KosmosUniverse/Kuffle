package fr.kosmosuniverse.kuffle.storage;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public class Save {
    public static void saveStopParty() {
        if (!PartyTmp.getInstance().pause()) {
            return ;
        }

        savePlayers(KuffleMain.getInstance().getDataFolder().getPath());

        PartyTmp.getInstance().clearGame();
        PartyTmp.getInstance().getOptions().saveOption();

        saveGlobal();

        PartyTmp.getInstance().stop();
        PartyTmp.getInstance().clearParty();
    }

    /**
     * Saves players data into files
     *
     * @param path	The location in which files will be generated
     */
    public static void savePlayers(String path) {
        PartyTmp.getInstance()
                .getPlayers()
                .getList()
                .forEach(playerName -> savePlayer(path, PartyTmp.getInstance().getGameManager().getPlayerData(playerName)));
    }

    /**
     * Save Player game in a file
     *
     * @param path	File Path
     */
    public static void savePlayer(String path, PlayerData playerData) {
        try (FileOutputStream fos = new FileOutputStream(path + File.separator + playerData.getPlayerName() + ".k")) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(playerData);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            Utils.logException(e);
        }
    }

    /**
     * Gets JSON string of all teams
     *
     * @param path	The path to the Kuffle plugin folder
     */
    public static void saveTeams(String path, List<Team> teams) {
        try (FileOutputStream fos = new FileOutputStream(path + File.separator + "Teams.k")) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeInt(teams.size());

            for (Team team : teams) {
                oos.writeObject(team);
            }

            oos.flush();
            oos.close();
        } catch (IOException e) {
            Utils.logException(e);
        }
    }

    /**
     * Saves the game
     */
    public static void saveGlobal() {
        GameHolder holder = new GameHolder(Config.getHolder(),
                PartyTmp.getInstance().getGameMode().getMode().name(),
                PartyTmp.getInstance().getGameManager().getXpActivables(),
                PartyTmp.getInstance().getGameManager().getRanks(),
                PartyTmp.getInstance().getOptions().getGlobalTimerInterval());

        try (FileOutputStream fos = new FileOutputStream(KuffleMain.getInstance().getDataFolder().getPath() + File.separator + "Game.k")) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(holder);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            Utils.logException(e);
        }
    }
}
