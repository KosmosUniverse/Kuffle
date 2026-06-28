package fr.kosmosuniverse.kuffle.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import fr.kosmosuniverse.kuffle.ranks.Ranks;
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
	private Ranks ranks;
	private long globalTimerInterval;

	/**
	 * Constructor
	 *
	 * @param conf					Config
	 * @param type					Kuffle Type
	 * @param xps					Xp max
	 * @param ranks					Ranks
	 * @param globalTimerInterval	Global timer in case of Coop Option
	 */
	public GameHolder(ConfigHolder conf, String type, Map<String, Integer> xps, Ranks ranks, long globalTimerInterval) {
		config = conf;
		kuffleType = type;
		xpMap = xps;
		this.ranks = ranks;
		this.globalTimerInterval = globalTimerInterval;
	}

	/**
	 * Clears maps
	 */
	public void clear() {
		xpMap.clear();
		ranks.clear();
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
		oStream.writeObject(ranks);

		if (config.isCoop()) {
			oStream.writeObject(globalTimerInterval);
		}
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
		ranks = (Ranks) iStream.readObject();

		if (config.isCoop()) {
			globalTimerInterval = iStream.readLong();
		}
	}
}