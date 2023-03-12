package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class SpreadPlayer {
	/**
	 * Private SpreadPlayer constructor
	 * 
	 * @throws IllegalStateException
	 */
	private SpreadPlayer() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Spreads all players to locations that forms circle centered on the sender player
	 * 
	 * @param sender	The player that is calling for a spread
	 * @param distance	The minimum distance between two players
	 * @param minRadius	The minimum radius from player location (Used if the radius calculated from distance is smaller)
	 * @param players	The players list it needs to spread
	 */
    public static void spreadPlayers(Player sender, double distance, long minRadius, List<Player> players) {
        if (distance < 0) {
            sender.sendMessage(ChatColor.RED + LangManager.getMsgLang("TOO_SHORT", Config.getLang()));
            return ;
        }
        
        List<Team> teams = TeamManager.getInstance().getTeams();
        
        int spreadSize;
        
        if (players == null) {
            spreadSize = 15;
        } else {
        	spreadSize = Config.getTeam() ? teams.size() : players.size();
        }
        
        double angle = 360.0 / spreadSize;
        long radius = radiusCalc(angle, distance);
        
        radius = radius <= minRadius ? minRadius : radius;
        
        List<Location> locations = getSpreadLocations(radius, angle, spreadSize, sender.getLocation());
        
        if ((Config.getTeam() && locations.size() != teams.size()) ||
        		(!Config.getTeam() && players != null && locations.size() != players.size())) {
        	return ;
        }
        
        spread(players, teams, locations);
        
        locations.clear();
    }
    
    /**
     * Calculates the minimum radius for a specific distance between two points
     * 
     * @param angle		The angle between two points
     * @param distance	The distance between two points
     * 
     * @return the minimum radius
     */
    private static long radiusCalc(double angle, double distance) {
    	double cos = Math.cos(Math.toRadians(angle));
    	double sin = Math.sin(Math.toRadians(angle));
    	double tmp = cos - 1;
    	
    	tmp = Math.pow(tmp, 2);
    	tmp = tmp + sin;
    	tmp = Math.sqrt(tmp);
    	tmp = distance / tmp;
    	
    	return Math.round(tmp);
    }
    
    /**
     * Gets the spread locations list
     * 
     * @param radius	The distance between center and every players
     * @param angleInc	The angle between two players
     * @param size		The amount of players (or locations to calculate)
     * @param center	The center of the circle as Location
     * 
     * @return the list of the future players locations
     */
    private static List<Location> getSpreadLocations(long radius, double angleInc, int size, Location center) {
    	List<Location> locations = new ArrayList<>();
    	
    	double angle = 0;
    	double x;
    	double z;
    	
    	for (int cnt = 0; cnt < size; cnt++) {
    		x = radius * Math.cos(Math.toRadians(angle));
    		z = radius * Math.sin(Math.toRadians(angle));
    		
    		locations.add(new Location(center.getWorld(), x + center.getX(), 0, z + center.getZ()));
    		
    		angle+=angleInc;
    	}
    	
    	return locations;
    }
    
    /**
     * Spreads players at the defined locations
     * 
     * @param players	The players to teleport
     * @param teams		The Teams to teleport
     * @param locations	The locations where players will be teleported
     */
    private static void spread(List<Player> players, List<Team> teams, List<Location> locations) {
    	if (players == null) {
        	for (int j = 0; j < 15; j++) {
        		Location location = locations.get(j);
        		
        		location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
        		location.getBlock().setType(Material.BEDROCK);
        	}
        	
        	return ;
        }
    	
    	if (Config.getTeam()) {
    		for (int cnt = 0; cnt < teams.size(); cnt++) {
    			Location location = locations.get(cnt);
    			
    			location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
    			
				for (Player player : teams.get(cnt).getPlayers()) {
					player.teleport(locations.get(cnt));
				}
    		}
    	} else {
        	for (int cnt = 0; cnt < players.size(); cnt++) {
        		Location location = locations.get(cnt);
        		
        		location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
        		
        		players.get(cnt).teleport(location);
        	}	
    	}
    }
}
