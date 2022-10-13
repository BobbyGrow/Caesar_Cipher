import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static java.nio.file.StandardOpenOption.WRITE;


public class StatResearch {
    public static HashMap<Character, Letter> initiateMap() {
        HashMap<Character, Letter> map = new HashMap<>();
        for (char ch : Main.ALPHABETS.toCharArray()) {
            map.put(ch, new Letter(0));
        }
        return map;
    }

    public static HashMap<Character, Letter> getProfile(String filename) {
        HashMap<Character, Letter> map = initiateMap();
        map = getAllChars(filename, map);
        map = normalise(map);
        return map;
    }

    private static HashMap<Character, Letter> normalise(HashMap<Character, Letter> map) {
        int maxCount = 0;
        for (char key : map.keySet()) {
            if (map.get(key).count > maxCount) {
                maxCount = map.get(key).count;
            }
        }
        for (char key : map.keySet()) {
            map.get(key).frequency = (int) ((map.get(key).count * 1.0 / maxCount) * 100);
        }
        return map;
    }

    private static HashMap<Character, Letter> getAllChars(String filename, HashMap<Character, Letter> corpusMap) {
        HashMap<Character, Letter> map = corpusMap;

        String corpusStr;
        try {
            corpusStr = Files.readString(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException();
        }

        for (char ch : corpusStr.toCharArray()) {
            char lowercase = Character.toLowerCase(ch);
            if (map.containsKey(lowercase)) {
                int temp = map.get(lowercase).count;
                map.put(lowercase, new Letter(temp + 1));
            }
        }
        return map;
    }

    public static int getFreqDiff(HashMap<Character, Letter> map1, HashMap<Character, Letter> map2) {
        int sum = 0;
        for (char key : map1.keySet()) {
            sum = sum + Math.abs(map1.get(key).frequency - map2.get(key).frequency);
        }
        return sum;
    }

    private static int getMaxABCLength() {
        int max = -1;
        for (String str : EncodingEngine.ABCs) {
            if (str.length() > max) {
                max = str.length();
            }
        }
        return max;
    }

    public static int findSecretKey(String srcFile, HashMap<Character, Letter> corpusMap) throws IOException {
        int minDiff = Integer.MAX_VALUE;
        int secretKey = 0;
        for (int i = 0; i < getMaxABCLength(); i++) {
            String srcString = Files.readString(Path.of(srcFile));
            String decodedText = EncodingEngine.encode(srcString, i * -1);
            Path tempFile = Files.createTempFile("temp", ".tmp");
            Files.writeString(tempFile, decodedText, StandardCharsets.UTF_8, WRITE);

            HashMap<Character, Letter> sourceMap = getProfile(tempFile.toString());

            int currentDiff = getFreqDiff(corpusMap, sourceMap);
            if (currentDiff < minDiff) {
                minDiff = currentDiff;
                secretKey = i;
            }
        }
        return secretKey;
    }
}

