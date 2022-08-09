package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class SpreadPlayer {
	private SpreadPlayer() {
		throw new IllegalStateException("Utility class");
	}
	
    public static List<Location> spreadPlayers(Player sender, double distance, long minRadius, List<Player> players) {
        if (distance < 0.0D) {
            sender.sendMessage(ChatColor.RED + Utils.getLangString(null, "TOO_SHORT"));
            return null;
        }

        World world = sender.getWorld();

        if (world == null) {
            return null;
        }
        
        List<Team> teams = KuffleMain.teams.getTeams();
        
        int spreadSize;
        
        if (players == null) {
            spreadSize = 15;
        } else {
        	spreadSize = teams == null ? players.size() : teams.size();
        }
        
        double angle = 360.0 / spreadSize;
        long radius = radiusCalc(angle, distance);
        
        radius = radius <= minRadius ? minRadius : radius;
        
        List<Location> locations = getSpreadLocations(radius, angle, spreadSize, world, sender.getLocation());
        
        if ((teams != null && locations.size() != teams.size()) ||
        		(teams == null && players != null && locations.size() != players.size())) {
        	return null;
        }
        
        spread(players, teams, locations, world);
       
        return locations;
    }
    
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
    
    private static List<Location> getSpreadLocations(long radius, double angleInc, int size, World world, Location center) {
    	List<Location> locations = new ArrayList<>();
    	
    	double angle = 0;
    	double x;
    	double z;
    	
    	for (int cnt = 0; cnt < size; cnt++) {
    		x = radius * Math.cos(Math.toRadians(angle));
    		z = radius * Math.sin(Math.toRadians(angle));
    		
    		locations.add(new Location(world, x + center.getX(), 0, z + center.getZ()));
    		
    		angle+=angleInc;
    	}
    	
    	return locations;
    }
    
    private static void spread(List<Player> players, List<Team> teams, List<Location> locations, World world) {
    	if (players == null) {
        	for (int j = 0; j < 15; j++) {
        		Location location = locations.get(j);
        		
        		location.setY(74);
        		location.getBlock().setType(Material.BEDROCK);
        	}
        	
        	return ;
        }
    	
    	if (teams != null) {
    		for (int cnt = 0; cnt < teams.size(); cnt++) {
    			 for (Player player : teams.get(cnt).players) {
    				 Location location = locations.get(cnt);
    	        		
    				 location.setY(world.getHighestBlockYAt(location) + 1);
    				 player.teleport(locations.get(cnt));
    			 }
    		}
    	} else {
        	for (int cnt = 0; cnt < players.size(); cnt++) {
        		Location location = locations.get(cnt);
        		
        		location.setY(world.getHighestBlockYAt(location) + 1);
        		
        		players.get(cnt).teleport(location);
        	}	
    	}
    }
}
