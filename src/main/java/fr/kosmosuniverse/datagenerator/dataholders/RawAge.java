package fr.kosmosuniverse.datagenerator.dataholders;

import lombok.Getter;

/**
 * @author KosmosUniverse
 */
@Getter
public class RawAge {
    private final int number;
    private final String name;
    private final String textColor;
    private final String boxColor;

    public RawAge(int number, String name, String textColor, String boxColor) {
        this.number = number;
        this.name = name;
        this.textColor = textColor;
        this.boxColor = boxColor;
    }
}
