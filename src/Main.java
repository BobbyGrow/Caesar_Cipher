
/*
Шифр Цезаря
*/


import java.io.*;
import java.nio.file.Path;

public class Main {
    enum OperationMode {
        ENCODE,
        DECODE,
        BRUTEFORCE
    }

    static String srcFile;
    static String destFile;
    static int key;
    static final String ALPHABETS = "abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    static final int ABC1_LENGTH = 26;
    static final int ABC2_LENGTH = ALPHABETS.length() - ABC1_LENGTH;


    public static void main(String[] args) {
        checkStringArgs(args);

        OperationMode mode = determineOperation(args[0]);

        //operation = Operation.ENCODE;
        switch (mode) {
            case ENCODE -> encodeOrDecode(mode, srcFile, key);
            case DECODE -> encodeOrDecode(mode, srcFile, -key);
            case BRUTEFORCE -> bruteforce();
        }
    }

    private static void encodeOrDecode(OperationMode mode, String srcFile, int key) {
        destFile = resolveDestFilename(srcFile, mode);
        try (FileReader reader = new FileReader(srcFile);
             StringWriter stringWriter = new StringWriter();
             FileWriter fileWriter = new FileWriter(destFile)) {

            char[] buffer = new char[4096];
            String encodedString = "";
            while (reader.ready()) {
                int real = reader.read(buffer);
                stringWriter.write(buffer, 0, real);
                encodedString = encodeString(stringWriter.toString());
            }
            fileWriter.write(encodedString);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String encodeString(String text) {
        char[] alphabetArr = ALPHABETS.toCharArray();
        char[] textArr = text.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (char symbol : textArr) {
            char temp = Character.toLowerCase(symbol);
            if (Character.isLetter(temp)) {
                int i = ALPHABETS.indexOf(temp);
                if (i >= 0 && i <= ABC1_LENGTH - 1) {
                    if (i + key < 0) {
                        temp = alphabetArr[(i + key + ABC1_LENGTH)];
                    } else {
                        temp = alphabetArr[(i + key) % ABC1_LENGTH];
                    }
                } else if (i >= ABC1_LENGTH) {
                    if (i + key < ABC1_LENGTH) {
                        temp = alphabetArr[i + key + ABC2_LENGTH];
                    } else {
                        temp = alphabetArr[((i - ABC1_LENGTH + key) % ABC2_LENGTH) + ABC1_LENGTH];
                    }
                }
                if (Character.isUpperCase(symbol)) {
                    temp = Character.toUpperCase(temp);
                }
            }
            stringBuilder.append(temp);
        }

        //System.out.println(stringBuilder.toString());
        String encryptedString = stringBuilder.toString();
        return encryptedString;
    }

    private static String resolveDestFilename(String srcFile, OperationMode mode) {
        String pathString = Path.of(srcFile).getFileName().toString();
        int pointLocation = pathString.lastIndexOf('.');
        String filenameStr = pathString.substring(0, pointLocation);
        String fileExtension = pathString.substring(pointLocation);

        if (mode == OperationMode.ENCODE) {
            filenameStr = filenameStr + "(encoded)" + fileExtension;
        } else if (mode == OperationMode.DECODE) {
            filenameStr = filenameStr + "(decoded)" + fileExtension;
        }

        return filenameStr;
    }

    private static void bruteforce() {
        //TODO: bruteforce
    }

    private static OperationMode determineOperation(String input) {

        if (OperationMode.ENCODE.toString().equalsIgnoreCase(input)) {
            return OperationMode.ENCODE;
        } else if (OperationMode.DECODE.toString().equalsIgnoreCase(input)) {
            return OperationMode.DECODE;
        } else if (OperationMode.BRUTEFORCE.toString().equalsIgnoreCase(input)) {
            return OperationMode.BRUTEFORCE;
        }
        return null;
    }

    private static void checkStringArgs(String[] args) {
        // TODO: проверь все три параметра, проверь существование файла
        Main.srcFile = args[1];
        Main.key = Integer.parseInt(args[2]);
    }
}