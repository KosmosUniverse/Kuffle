package main.fr.kosmosuniverse.kuffle.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import main.fr.kosmosuniverse.kuffle.utils.SerializeUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public final class GameHolder implements Serializable {
	/*
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Current config
	 */
	private ConfigHolder config;
	
	/**
	 * Current Kuffle type
	 */
	private String kuffleType;

	/**
	 * Players final ranks
	 */
	private Map<String, Integer> ranks;
	
	/**
	 * Current xp needed for each activables
	 */
	private Map<String, Integer> xpMap;
	
	/**
	 * Constructor
	 * 
	 * @param _config		Config
	 * @param _kuffleType	Kuffle Type
	 * @param _ranks		Players Ranks
	 * @param _xpMap		Xp max
	 */
	public GameHolder(ConfigHolder _config, String _kuffleType, Map<String, Integer> _ranks, Map<String, Integer> _xpMap) {
		config = _config;
		kuffleType = _kuffleType;
		ranks = _ranks;
		xpMap = _xpMap;
	}
	
	/**
	 * Clears maps
	 */
	public void clear() {
		ranks.clear();
		xpMap.clear();
	}
	
	/**
	 * Gets the config
	 * 
	 * @return the ConfigHolder object
	 */
	public ConfigHolder getConfig() {
		return config;
	}
	
	/**
	 * Gets Kuffle Type
	 * 
	 * @return the type as String
	 */
	public String getKuffleType() {
		return kuffleType;
	}
	
	/**
	 * Gets Players ranks
	 * 
	 * @return the ranks map
	 */
	public Map<String, Integer> getRanks() {
		return ranks;
	}
	
	/**
	 * Gets the Xp max values
	 * 
	 * @return the xp max Map
	 */
	public Map<String, Integer> getXpMap() {
		return xpMap;
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
		oStream.writeObject(ranks);
		oStream.writeObject(xpMap);
	}
	
	/**
	 * Defines which field will be loaded and how at deserialization
	 * 
	 * @param iStream	Deserialization stream
	 * 
	 * @throws ClassNotFoundException 	Raised at read fail
	 * @throws IOException				Raised at read fail
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream iStream) throws ClassNotFoundException, IOException  {
		kuffleType = SerializeUtils.readString(iStream);
		config = (ConfigHolder) iStream.readObject();
		ranks = (Map<String, Integer>) iStream.readObject();
		xpMap = (Map<String, Integer>) iStream.readObject();
	}
}
