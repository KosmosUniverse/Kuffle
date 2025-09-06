package fr.kosmosuniverse.datagenerator.dataholders;

import lombok.Getter;

/**
 * @author KosmosUniverse
 */
@Getter
public class RawLang {
    private final String key;
    private final String fr;
    private final String en;

    public RawLang(String key, String en, String fr) {
        this.key = key;
        this.en = en;
        this.fr = fr;
    }
}
