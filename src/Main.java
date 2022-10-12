
/*
Шифр Цезаря
*/


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    enum Mode {
        ENCODE,
        DECODE,
        BRUTEFORCE
    }

    static final String ALPHABETS = "abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    static final int ABC1_LENGTH = 26;
    static final int ABC2_LENGTH = ALPHABETS.length() - ABC1_LENGTH;


    public static void main(String[] args) throws IOException {
        checkArgs(args);
        Mode mode = Mode.valueOf(args[0].toUpperCase());
        String srcFile = args[1];

        switch (mode) {
            case ENCODE -> {
                int key = Integer.parseInt(args[2]);
                String encodedText = encodeFileToString(mode, srcFile, key);
                String destFile = resolveDestFilename(srcFile, mode);
                writeCodeToFile(encodedText, destFile);
            }
            case DECODE -> {
                int key = Integer.parseInt(args[2]) * -1;
                String encodedText = encodeFileToString(mode, srcFile, key);
                String destFile = resolveDestFilename(srcFile, mode);
                writeCodeToFile(encodedText, destFile);
            }
            case BRUTEFORCE -> {
                bruteforce(srcFile, args[2]);
            }
        }
    }

    private static String encodeFileToString(Mode mode, String srcFile, int key) {

        try (FileReader reader = new FileReader(srcFile);
             StringWriter stringWriter = new StringWriter()) {

            char[] buffer = new char[4096];
            String encodedText = "";
            while (reader.ready()) {
                int real = reader.read(buffer);
                stringWriter.write(buffer, 0, real);
                encodedText = encodePartOfFile(stringWriter.toString(), key);
            }
            return encodedText;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String encodePartOfFile(String text, int key) {
        char[] alphabetArr = ALPHABETS.toCharArray();
        char[] textArr = text.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (char symbol : textArr) {
            char temp = Character.toLowerCase(symbol);
            if (Character.isLetter(temp)) {
                int i = ALPHABETS.indexOf(temp);
                if (i >= 0 && i <= ABC1_LENGTH - 1) {
                    int nKey = normalizeKey(key, ABC1_LENGTH);
                    if (i + nKey < 0) {
                        temp = alphabetArr[(i + nKey + ABC1_LENGTH)];
                    } else {
                        temp = alphabetArr[(i + nKey) % ABC1_LENGTH];
                    }
                } else if (i >= ABC1_LENGTH) {
                    int nKey = normalizeKey(key, ABC2_LENGTH);
                    if (i + nKey < ABC1_LENGTH) {
                        temp = alphabetArr[i + nKey + ABC2_LENGTH];
                    } else {
                        temp = alphabetArr[((i - ABC1_LENGTH + nKey) % ABC2_LENGTH) + ABC1_LENGTH];
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

    private static void writeCodeToFile(String encodedText, String destFile) {
        try (FileWriter fileWriter = new FileWriter(destFile)) {

            fileWriter.write(encodedText);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int normalizeKey(int key, int length) {
        key = key % length;
        return key;
    }

    private static String resolveDestFilename(String srcFile, Mode mode) {
        String pathString = Path.of(srcFile).getFileName().toString();
        int pointLocation = pathString.lastIndexOf('.');
        String filenameStr = pathString.substring(0, pointLocation);
        String fileExtension = pathString.substring(pointLocation);

        if (mode == Mode.ENCODE) {
            filenameStr = filenameStr + "(encoded)" + fileExtension;

        } else if (mode == Mode.DECODE) {
            int bracketLocation = pathString.lastIndexOf('(');
            if (bracketLocation == -1) {
                bracketLocation = pointLocation;
            }
            filenameStr = pathString.substring(0, bracketLocation);
            fileExtension = pathString.substring(pointLocation);
            filenameStr = filenameStr + "(decoded)" + fileExtension;
        }

        return filenameStr;
    }

    private static void bruteforce(String srcFile, String arg){

    }


    private static void checkArgs(String[] args) throws IllegalArgumentException, IOException {
        if (args[0] == null || args[1] == null || args[2] == null) {
            throw new IllegalArgumentException("Неверное количество аргументов командной строки. Используй operation filePath key.");
        } else if (Files.notExists(Path.of(args[1]))) {
            throw new IllegalArgumentException("Исходный файл не найден.");
        } else if (!args[0].equalsIgnoreCase("encode") &&
                !args[0].equalsIgnoreCase("decode") &&
                !args[0].equalsIgnoreCase("bruteforce")) {
            throw new IllegalArgumentException("Неверный первый аргумент. Ожидаю один из: encode|decode|bruteforce.");
        } else if (args[0].equalsIgnoreCase("bruteforce")) {
            if (Files.notExists(Path.of(args[2]))) {
                throw new IllegalArgumentException("Не могу найти файл для статистического анализа кода. Ожидаю аргументы: bruteforce filepath filepath2.");
            }
        } else if (!isInteger(args[2])) {
            throw new IllegalArgumentException("Неверный ключ кодирования в третьем параметре. Ожидаю целое ненулевое число.");
        } else if (Integer.parseInt(args[2]) == 0) {
            throw new IllegalArgumentException(("Нет смысла кодировать ключом 0. Ожидаю целое ненулевое число в параметре 3."));
        } else if (Files.size(Path.of(args[1])) == 0) {
            throw new IllegalArgumentException("Исходный файл пуст. Кодировать нечего.");
        }
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            int a = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}