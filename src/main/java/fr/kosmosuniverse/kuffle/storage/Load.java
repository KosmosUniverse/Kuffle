package fr.kosmosuniverse.kuffle.storage;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class Load {
    public static void loadStartParty(Player player, String folderPath) {
        PartyTmp.getInstance().setupParty();

        loadGlobal(player, folderPath + File.separator + "Game.k");

        loadPlayers(folderPath);

        PartyTmp.getInstance().initGame();
        PartyTmp.getInstance().getOptions().loadOption();

        initPlayers(folderPath);

        if (PartyTmp.getInstance().launchChecks(player)) {
            PartyTmp.getInstance().launch(player);
        } else {
            PartyTmp.getInstance().clearGame();
            PartyTmp.getInstance().clearParty();
        }
    }

    public static boolean loadGlobal(Player sender, String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            GameHolder holder = (GameHolder) ois.readObject();

            Mode mode = Mode.valueOf(holder.getKuffleType());

            if (PartyTmp.getInstance().getGameMode().getMode() != mode &&
                    PartyTmp.getInstance().getGameMode().getMode() != Mode.NO_MODE) {
                LogManager.getInstanceSystem().writeMsg(sender, LangManager.getMsgLang("WRONG_TYPE", Config.getLang()));
                return false;
            } else if (PartyTmp.getInstance().getGameMode().getMode() == Mode.NO_MODE) {
                PartyTmp.getInstance().setMode(mode);
            }

            Config.loadConfig(holder.getConfig());
            PartyTmp.getInstance().getGameManager().setRanks(holder.getRanks());
            PartyTmp.getInstance().getOptions().setGlobalTimerInterval(holder.getGlobalTimerInterval());

            holder.clear();
        } catch (IOException | ClassNotFoundException | KuffleFileLoadException e) {
            Utils.logException(e);
            return false;
        }

        return true;
    }

    public static boolean loadPlayers(String folderPath) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        players.stream()
                .filter(player -> Utils.fileExists(folderPath, player.getName() + ".k"))
                .forEach(player -> PartyTmp.getInstance().getPlayers().addPlayer(player.getName()));

        players.clear();

        return true;
    }

    public static void loadTeams(String path) throws IOException, ClassNotFoundException {
        try (FileInputStream fos = new FileInputStream(path + File.separator + "Teams.k")) {
            ObjectInputStream ois = new ObjectInputStream(fos);
            int size = ois.readInt();

            for (int i = 0; i < size; i++) {
                TeamManager.getInstance().addTeam((Team) ois.readObject());
            }

            ois.close();
        }
    }

    public static void initPlayers(String folderPath) {
        PartyTmp.getInstance().getPlayers()
                .getList()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> initPlayer(folderPath, player));
    }

    public static void initPlayer(String folderPath, Player player) {
        try (FileInputStream fos = new FileInputStream(folderPath + File.separator + player.getName() + ".k")) {
            ObjectInputStream ois = new ObjectInputStream(fos);

            PlayerData playerData = (PlayerData) ois.readObject();
            ois.close();

            PartyTmp.getInstance().getGameManager().loadPlayer(playerData);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

        //ScoreManager.setupPlayerScores(player.getName());

        //games.get(player.getName()).getScore().setScore(games.get(player.getName()).getTargetCount());
    }
}
