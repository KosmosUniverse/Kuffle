package fr.kosmosuniverse.datagenerator;

import fr.kosmosuniverse.datagenerator.dataholders.*;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KosmosUniverse
 */
public class Main {
    private static Map<Integer, String> versions;
    private static List<RawAge> ages;
    private static List<RawLevel> levels;
    private static List<RawLang> langs;
    private static List<RawReward> rewards;
    private static List<RawTargets> targets;

    public static void main(String[] args) {
        boolean ret;

        try (FileInputStream file = new FileInputStream("KuffleFilesGenerator.xlsm")) {
            ReadableWorkbook workbook = new ReadableWorkbook(file);

            ret = loadVersion(workbook.getSheets().filter(sheet -> sheet.getName().equals("Versions")).findAny().orElse(null));

            if (ret) {
                ret = loadAge(workbook.getSheets().filter(sheet -> sheet.getName().equals("Ages")).findAny().orElse(null));
            } else {
                System.err.println("Version load failed !");
            }

            if (ret) {
                ret = loadLevel(workbook.getSheets().filter(sheet -> sheet.getName().equals("Levels")).findAny().orElse(null));
            } else {
                System.err.println("Age load failed !");
            }

            if (ret) {
                ret = loadLang(workbook.getSheets().filter(sheet -> sheet.getName().equals("Langs")).findAny().orElse(null));
            } else {
                System.err.println("Level load failed !");
            }

            if (ret) {
                ret = loadReward(workbook.getSheets().filter(sheet -> sheet.getName().equals("Versions")).findAny().orElse(null),
                        workbook.getSheets().filter(sheet -> sheet.getName().equals("Ages")).findAny().orElse(null),
                        workbook.getSheets().filter(sheet -> sheet.getName().equals("Rewards")).findAny().orElse(null));
            } else {
                System.err.println("Langs load failed !");
            }

            if (ret) {
                ret = loadTargets(workbook.getSheets().filter(sheet -> sheet.getName().equals("Versions")).findAny().orElse(null),
                        workbook.getSheets().filter(sheet -> sheet.getName().equals("Ages")).findAny().orElse(null),
                        workbook.getSheets().filter(sheet -> sheet.getName().equals("TargetsList")).findAny().orElse(null),
                        workbook.getSheets().filter(sheet -> sheet.getName().equals("TargetsLang")).findAny().orElse(null));
            } else {
                System.err.println("Rewards load failed !");
            }

            if (ret) {
                System.out.println("Everything is good !");
            } else {
                System.err.println("Targets load failed !");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean loadVersion(Sheet versionSheet) {
        if (versionSheet == null) {
            return false;
        }

        try {
            readVersion(versionSheet);
            writeVersion();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static void readVersion(Sheet versionSheet) throws IOException {
        versions = new HashMap<>();

        for (Row row : versionSheet.read()) {
            if (row.getRowNum() > 1) {
                versions.put(Integer.parseInt(row.getCell(1).getRawValue()), row.getCell(0).getRawValue());
            }
        }
    }

    private static void writeVersion() throws IOException {
        JSONObject mainObj = new JSONObject();

        versions.forEach((k, v) -> mainObj.put(v, k));

        try (FileWriter fw = new FileWriter("./src/main/resources/versions.json")) {
            fw.write(mainObj.toString(4));
        }

        versions.clear();
    }

    private static boolean loadAge(Sheet ageSheet) {
        if (ageSheet == null) {
            return false;
        }

        try {
            readAge(ageSheet);
            writeAge();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static void readAge(Sheet ageSheet) throws IOException {
        ages = new ArrayList<>();

        for (Row row : ageSheet.read()) {
            if (row.getRowNum() > 1 && row.getCell(0) != null) {
                ages.add(new RawAge(Integer.parseInt(row.getCell(0).getRawValue()),
                        row.getCell(1).getRawValue(),
                        row.getCell(2).getRawValue(),
                        row.getCell(3).getRawValue()));
            }
        }
    }

    private static void writeAge() throws IOException {
        JSONObject mainObj = new JSONObject();

        ages.forEach(age -> {
            JSONObject ageObj = new JSONObject();

            ageObj.put("Number", age.getNumber());
            ageObj.put("TextColor", age.getTextColor());
            ageObj.put("BoxColor", age.getBoxColor());

            mainObj.put(age.getName(), ageObj);
        });

        try (FileWriter fw = new FileWriter("./src/main/resources/ages.json")) {
            fw.write(mainObj.toString(4));
        }

        ages.clear();
    }

    private static boolean loadLevel(Sheet levelSheet) {
        if (levelSheet == null) {
            return false;
        }

        try {
            readLevel(levelSheet);
            writeLevel();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static void readLevel(Sheet levelSheet) throws IOException {
        levels = new ArrayList<>();

        for (Row row : levelSheet.read()) {
            if (row.getRowNum() > 1 && row.getCell(0) != null) {
                levels.add(new RawLevel(row.getCell(0).getRawValue(),
                        Integer.parseInt(row.getCell(1).getRawValue()),
                        Integer.parseInt(row.getCell(2).getRawValue()),
                        Boolean.parseBoolean(row.getCell(3).getRawValue())));
            }
        }
    }

    private static void writeLevel() throws IOException {
        JSONObject mainObj = new JSONObject();

        levels.forEach(level -> {
            JSONObject levelObj = new JSONObject();

            levelObj.put("Number", level.getNumber());
            levelObj.put("Seconds", level.getSeconds());
            levelObj.put("Lose", level.isLose());

            mainObj.put(level.getName(), levelObj);
        });

        try (FileWriter fw = new FileWriter("./src/main/resources/levels.json")) {
            fw.write(mainObj.toString(4));
        }

        levels.clear();
    }

    private static boolean loadLang(Sheet langSheet) {
        if (langSheet == null) {
            return false;
        }

        try {
            readLang(langSheet);
            writeLang();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static void readLang(Sheet langSheet) throws IOException {
        langs = new ArrayList<>();

        for (Row row : langSheet.read()) {
            if (row.getRowNum() > 1 && row.getCell(0) != null) {
                langs.add(new RawLang(row.getCell(0).getRawValue(),
                        row.getCell(1).getRawValue(),
                        row.getCell(2).getRawValue()));
            }
        }
    }

    private static void writeLang() throws IOException {
        JSONObject mainObj = new JSONObject();

        langs.forEach(lang -> {
            JSONObject levelObj = new JSONObject();

            levelObj.put("en", lang.getEn());
            levelObj.put("fr", lang.getFr());

            mainObj.put(lang.getKey(), levelObj);
        });

        try (FileWriter fw = new FileWriter("./src/main/resources/msgs_langs.json")) {
            fw.write(mainObj.toString(4));
        }

        langs.clear();
    }

    private static boolean loadReward(Sheet versionSheet, Sheet ageSheet, Sheet rewardSheet) {
        if (rewardSheet == null) {
            return false;
        }

        try {
            readReward(rewardSheet);
            writeReward(versionSheet, ageSheet);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static void readReward(Sheet langSheet) throws IOException {
        rewards = new ArrayList<>();

        for (Row row : langSheet.read()) {
            if (row.getRowNum() > 1 && row.getCell(0) != null) {
                rewards.add(new RawReward(row.getCell(1).getRawValue(),
                        row.getCell(0).getRawValue(),
                        row.getCell(2).getRawValue(),
                        Integer.parseInt(row.getCell(3).getRawValue()),
                        (row.getCell(4) != null &&
                                row.getCell(4).getRawValue() != null) ? row.getCell(4).getRawValue() : null,
                        (row.getCell(5) != null &&
                                row.getCell(5).getRawValue() != null) ? Integer.parseInt(row.getCell(5).getRawValue()) : null,
                        (row.getCell(6) != null && row.getCell(6).getRawValue() != null) ? row.getCell(6).getRawValue() : null));
            }
        }
    }

    private static void writeReward(Sheet versionSheet, Sheet ageSheet) throws IOException {
        List<String> tmpVersions = new ArrayList<>();

        for (Row row : versionSheet.read()) {
            if (row.getRowNum() > 1) {
                tmpVersions.add(row.getCell(0).getRawValue());
            }
        }

        List<String> tmpAges = new ArrayList<>();

        for (Row row : ageSheet.read()) {
            if (row.getRowNum() > 1 && row.getCell(0) != null) {
                tmpAges.add(row.getCell(1).getRawValue());
            }
        }

        JSONObject mainObj = new JSONObject();

        tmpVersions.forEach(v -> {
            JSONObject versionObj = new JSONObject();

            tmpAges.forEach(a -> {
                JSONObject ageObj = new JSONObject();

                rewards.stream().filter(r -> r.getVersion().equals(v) && r.getAge().equals(a)).forEach(r -> {
                    JSONObject rewardObj = new JSONObject();

                    rewardObj.put("Amount", r.getAmount());

                    if (r.getEffect() != null) {
                        rewardObj.put("Effect", r.getEffect());
                    }

                    if (r.getLevel() != null) {
                        rewardObj.put("Level", r.getLevel());
                    }

                    if (r.getEnchant() != null) {
                        rewardObj.put("Enchant", r.getEnchant());
                    }

                    ageObj.put(r.getName(), rewardObj);
                });

                if (ageObj.keySet().size() != 0) {
                    versionObj.put(a, ageObj);
                }
            });

            if (versionObj.keySet().size() != 0) {
                mainObj.put(v, versionObj);
            }
        });

        try (FileWriter fw = new FileWriter("./src/main/resources/rewards.json")) {
            fw.write(mainObj.toString(4));
        }

        rewards.clear();
        tmpAges.clear();
        tmpVersions.clear();
    }

    private static boolean loadTargets(Sheet versionSheet, Sheet ageSheet, Sheet targetSheet, Sheet targetLangSheet) {
        if (targetSheet == null) {
            return false;
        }

        try {
            readLang(targetLangSheet);
            readTarget(targetSheet);
            writeTarget(versionSheet, ageSheet);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static void readTarget(Sheet targetSheet) throws IOException {
        targets = new ArrayList<>();

        for (Row row : targetSheet.read()) {
            if (row.getRowNum() > 1 &&
                    row.getCell(0) != null &&
                    row.getCell(0).getRawValue() != null) {
                targets.add(new RawTargets(row.getCell(1).getRawValue(),
                        row.getCell(2).getRawValue(),
                        row.getCell(3).getRawValue(),
                        row.getCell(4).getRawValue().equals("N/A") ? null : row.getCell(4).getRawValue(),
                        Boolean.parseBoolean(row.getCell(5).getRawValue()),
                        row.getCell(0).getRawValue()));
            }
        }
    }

    private static void writeTarget(Sheet versionSheet, Sheet ageSheet) throws IOException {
        List<String> tmpVersions = new ArrayList<>();

        for (Row row : versionSheet.read()) {
            if (row.getRowNum() > 1) {
                tmpVersions.add(row.getCell(0).getRawValue());
            }
        }

        List<String> tmpAges = new ArrayList<>();

        for (Row row : ageSheet.read()) {
            if (row.getRowNum() > 1 && row.getCell(0) != null) {
                tmpAges.add(row.getCell(1).getRawValue());
            }
        }

        List<String> tmpCategory = new ArrayList<>();
        tmpCategory.add("Blocks");
        tmpCategory.add("Items");
        tmpCategory.add("Both");

        JSONObject mainObj = new JSONObject();

        tmpVersions.forEach(v -> {
            JSONObject versionObj = new JSONObject();

            tmpCategory.forEach(c -> {
                JSONObject categoryObj = new JSONObject();

                tmpAges.forEach(a -> {
                    JSONObject ageObj = new JSONObject();

                    targets.stream().filter(t -> t.getVersion().equals(v) && t.getCategory().equals(c) && t.getAge().equals(a)).forEach(t -> {
                        JSONObject targetObj = new JSONObject();

                        targetObj.put("Sbtt", t.isSbtt());

                        RawLang lang;
                        if ((lang = langs.stream().filter(l -> l.getKey().equals(t.getName())).findAny().orElse(null)) != null) {
                            JSONObject targetLangObj = new JSONObject();

                            targetLangObj.put("en", lang.getEn());
                            targetLangObj.put("fr", lang.getFr());

                            targetObj.put("Langs", targetLangObj);
                        } else {
                            System.out.println("target without Lang : " + t.getName());
                        }


                        ageObj.put(t.getName(), targetObj);
                    });

                    if (ageObj.keySet().size() != 0) {
                        categoryObj.put(a, ageObj);
                    }
                });

                if (categoryObj.keySet().size() != 0) {
                    versionObj.put(c, categoryObj);
                }
            });

            if (versionObj.keySet().size() != 0) {
                mainObj.put(v, versionObj);
            }
        });

        try (FileWriter fw = new FileWriter("./src/main/resources/targets.json")) {
            fw.write(mainObj.toString(4));
        }

        targets.clear();
        tmpAges.clear();
        tmpVersions.clear();
        langs.clear();
    }
}
