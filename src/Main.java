
/*
Шифр Цезаря
*/


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Main {
    enum Mode {
        ENCODE,
        DECODE,
        BRUTEFORCE
    }
    public static void main(String[] args) throws IOException {
        Service.checkArgs(args);
        Mode mode = Mode.valueOf(args[0].toUpperCase());
        String srcFile = args[1];

        switch (mode) {
            case ENCODE -> {
                int key = Integer.parseInt(args[2]);
                EncodingEngine.encode(mode, srcFile, key);

            }
            case DECODE -> {
                int key = Integer.parseInt(args[2]) * -1;
                EncodingEngine.encode(mode, srcFile, key);

            }
            case BRUTEFORCE -> {
                String corpusFile = args[2];
                bruteforce(srcFile, corpusFile);
            }
        }
    }

    private static void bruteforce(String srcFile, String corpusFile) throws IOException {

        HashMap<Character, Letter> corpusMap = StatResearch.getProfile(corpusFile);
        int secretKey = StatResearch.findSecretKey(srcFile, corpusMap);

        String encodedText = Files.readString(Path.of(srcFile));
        String decodedText = EncodingEngine.encode(encodedText, secretKey * -1);
        Service.writeStringToFile(decodedText, Service.addFilenameSuffix(srcFile, Mode.BRUTEFORCE, secretKey));
    }
}