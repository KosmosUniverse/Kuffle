package fr.kosmosuniverse.kuffle.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import fr.kosmosuniverse.kuffle.utils.SerializeUtils;
import lombok.Getter;

/**
 *
 * @author KosmosUniverse
 *
 */
@Getter
public final class GameHolder implements Serializable {
	/*
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;
	private ConfigHolder config;
	private String kuffleType;
	private Map<String, Integer> xpMap;
	private Map<String, Integer> playerRanks;
	private Map<String, Integer> teamRanks;
	private Map<String, Integer> nextRanks;

	/**
	 * Constructor
	 *
	 * @param conf				Config
	 * @param type				Kuffle Type
	 * @param playerRanksMap	Players Ranks
	 * @param xps				Xp max
	 */
	public GameHolder(ConfigHolder conf, String type, Map<String, Integer> xps, Map<String, Integer> playerRanksMap, Map<String, Integer> teamRanksMap, Map<String, Integer> nextRanks) {
		config = conf;
		kuffleType = type;
		xpMap = xps;
		playerRanks = playerRanksMap;
		teamRanks = teamRanksMap;
		this.nextRanks = nextRanks;

	}

	/**
	 * Clears maps
	 */
	public void clear() {
		xpMap.clear();
		playerRanks.clear();
		teamRanks.clear();
		nextRanks.clear();
	}

	/**
	 * Defines which field will be stored and how at serialization
	 *
	 * @param oStream	Serialization stream
	 *
	 * @throws IOException Raised at write fail
	 */
	private void writeObject(ObjectOutputStream oStream) throws IOException {
		oStream.writeUTF(kuffleType);
		oStream.writeObject(config);
		oStream.writeObject(xpMap);
		oStream.writeObject(playerRanks);

		if (Config.getTeam()) {
			oStream.writeObject(teamRanks);
		}

		oStream.writeObject(nextRanks);
	}

	/**
	 * Defines which field will be loaded and how at deserialization
	 *
	 * @param iStream	Deserialization stream
	 *
	 * @throws ClassNotFoundException 	Raised at read fail
	 * @throws IOException				Raised at read fail
	 */
	private void readObject(ObjectInputStream iStream) throws ClassNotFoundException, IOException  {
		kuffleType = SerializeUtils.readString(iStream);
		config = (ConfigHolder) iStream.readObject();
		xpMap = (Map<String, Integer>) iStream.readObject();
		playerRanks = (Map<String, Integer>) iStream.readObject();

		if (config.isTeam()) {
			teamRanks = (Map<String, Integer>) iStream.readObject();
		}

		nextRanks = (Map<String, Integer>) iStream.readObject();
	}
}