package fr.kosmosuniverse.datagenerator.dataholders;

import lombok.Getter;

/**
 * @author KosmosUniverse
 */
@Getter
public class RawLevel {
    private final String name;
    private final int number;
    private final int seconds;
    private final boolean lose;

    public RawLevel(String name, int number, int seconds, boolean lose) {
        this.name = name;
        this.number = number;
        this.seconds = seconds;
        this.lose = lose;
    }
}
