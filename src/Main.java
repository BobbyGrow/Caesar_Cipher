
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

    static final String ALPHABETS = "abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    static final int ABC1_LENGTH = 26;
    static final int ABC2_LENGTH = ALPHABETS.length() - ABC1_LENGTH;


    public static void main(String[] args) {


        OperationMode mode = OperationMode.valueOf(args[0].toUpperCase());
        String srcFile = args[1];
        int key = Integer.parseInt(args[2]);


        switch (mode) {
            case ENCODE -> encodeOrDecode(mode, srcFile, key);
            case DECODE -> encodeOrDecode(mode, srcFile, -key);
            case BRUTEFORCE -> bruteforce();
        }
    }

    private static void encodeOrDecode(OperationMode mode, String srcFile, int key) {
        String destFile = resolveDestFilename(srcFile, mode);
        try (FileReader reader = new FileReader(srcFile);
             StringWriter stringWriter = new StringWriter();
             FileWriter fileWriter = new FileWriter(destFile)) {

            char[] buffer = new char[4096];
            String encodedString = "";
            while (reader.ready()) {
                int real = reader.read(buffer);
                stringWriter.write(buffer, 0, real);
                encodedString = encodeString(stringWriter.toString(), key);
            }
            fileWriter.write(encodedString);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String encodeString(String text, int key) {
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

        return stringBuilder.toString();
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


}