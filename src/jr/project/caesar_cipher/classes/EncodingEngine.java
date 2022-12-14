package jr.project.caesar_cipher.classes;

import jr.project.caesar_cipher.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class EncodingEngine {

    static final ArrayList<String> ABCs = new ArrayList<>();

    static {
        ABCs.add("abcdefghijklmnopqrstuvwxyz");
        ABCs.add("абвгдеёжзийклмнопрстуфхцчшщъыьэюя");
    }

    public static void encode(Main.Mode mode, String srcFile, int key) throws IOException {
        String srcString = Files.readString(Path.of(srcFile));
        String encodedString = encode(srcString, key);
        Service.writeStringToFile(encodedString, Service.addFilenameSuffix(srcFile, mode, 0));
    }

    public static String encode(String srcStr, int key) {
        StringBuilder result = new StringBuilder();
        char resultChar;
        char[] charArr = srcStr.toCharArray();

        for (char ch : charArr) {
            resultChar = encode(ch, key);
            result.append(resultChar);
        }

        return result.toString();
    }

    private static char encode(char srcChar, int key) {
        char ch = Character.toLowerCase(srcChar);
        int currentABCIndex = whichABCHas(ch);

        if (currentABCIndex != -1) {
            String currentABC = ABCs.get(currentABCIndex);
            int ABCLength = currentABC.length();
            int i = currentABC.indexOf(ch);

            key = key % ABCLength; //для значений ключа больше длины алфавита

            if (i + key < 0) {
                ch = currentABC.charAt(i + key + ABCLength);
            } else {
                ch = currentABC.charAt((i + key) % ABCLength);
            }

            if (Character.isUpperCase(srcChar)) {
                ch = Character.toUpperCase(ch);
            }
        }
        return ch;
    }

    private static int whichABCHas(char ch) {
        for (int i = 0; i < ABCs.size(); i++) {
            if (ABCs.get(i).indexOf(ch) != -1) {
                return i;
            }
        }
        return -1;
    }

}
