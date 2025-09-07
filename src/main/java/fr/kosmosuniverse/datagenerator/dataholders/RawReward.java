package fr.kosmosuniverse.datagenerator.dataholders;

import lombok.Getter;

/**
 * @author KosmosUniverse
 */
@Getter
public class RawReward {
    private final String name;
    private final String version;
    private final String age;
    private final int amount;
    private final String enchant;
    private final Integer level;
    private final String effect;

    public RawReward(String name, String version, String age, int amount, String enchant, Integer level, String effect) {
        this.name = name;
        this.version = version;
        this.age = age;
        this.amount = amount;
        this.enchant = enchant;
        this.level = level;
        this.effect = effect;
    }
}
