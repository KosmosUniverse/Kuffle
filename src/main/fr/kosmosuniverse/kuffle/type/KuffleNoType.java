package main.fr.kosmosuniverse.kuffle.type;

import org.bukkit.plugin.java.JavaPlugin;

import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleNoType extends KuffleType {
	/**
	 * Constructor
	 * 
	 * @param plugin	the plugin itself
	 * 
	 * @throws KuffleFileLoadException if one of the resource file load fails
	 */
	public KuffleNoType(JavaPlugin plugin) throws KuffleFileLoadException {
		super(plugin);
	}
	
	public void setupSbtt() {
		throw new UnsupportedOperationException("No type selected");
	}
	
	public void clearSbtt() {
		throw new UnsupportedOperationException("No type selected");
	}
	
	@Override
	public Type getType() {
		return Type.NO_TYPE;
	}
	
	@Override
	public KuffleType clearType() {
		return this;
	}
}
