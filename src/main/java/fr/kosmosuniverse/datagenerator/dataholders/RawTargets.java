package fr.kosmosuniverse.datagenerator.dataholders;

import lombok.Getter;

/**
 * @author KosmosUniverse
 */
@Getter
public class RawTargets {
    private final String name;
    private final String age;
    private final String version;
    private final String remVersion;
    private final boolean sbtt;
    private final String category;

    public RawTargets(String name, String age, String version, String remVersion, boolean sbtt, String category) {
        this.name = name;
        this.age = age;
        this.version = version;
        this.remVersion = remVersion;
        this.sbtt = sbtt;
        this.category = category;
    }
}
