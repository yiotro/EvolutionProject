package yio.tro.evolution;

import java.util.Random;

/**
 * Created by ivan on 18.09.2015.
 */
public class NameGenerator {

    private static String syllables[] = {"tri", "ce", "ze", "ra", "mito", "hon", "ma", "as", "us", "os"};
    private static Random random = new Random();


    public static String generateAnimalNameByGenes(boolean genActive[]) {
        StringBuffer name = new StringBuffer();
        for (int i = 0; i < genActive.length; i++) {
            if (genActive[i]) {
                name.append(syllables[i]);
            }
        }
        if (name.length() == 0) name.append("simplos");
        name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
        return name.toString();
    }


    public static String generateAnimalName() {
        StringBuffer name = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            name.append(syllables[random.nextInt(syllables.length)]);
        }
        if (name.length() == 0) name.append("simplos");
        name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
        return name.toString();
    }


    public static String generateHerbName(boolean genActive[]) {
        return generateAnimalNameByGenes(genActive);
    }


    private static String getRandomElement(String list[]) {
        return list[random.nextInt(list.length)];
    }
}
