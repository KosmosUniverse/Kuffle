package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.KuffleMain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleResultsTab extends AKuffleTabCommand {
	public KuffleResultsTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			try (Stream<Path> stream = Files.walk(Paths.get(KuffleMain.getInstance().getDataFolder().getPath()), 1)) {
				ret.addAll(stream
						.filter(file -> !Files.isDirectory(file))
						.map(Path::getFileName)
						.map(Path::toString)
								.filter(file -> file.toLowerCase().startsWith("results"))
						.collect(Collectors.toSet()));
			} catch (IOException e) {
				ret.add("No results to load.");
			}
		}
	}
}
