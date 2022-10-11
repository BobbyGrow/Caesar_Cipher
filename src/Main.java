
/*
Шифр Цезаря
*/


import java.io.*;
import java.nio.file.Files;
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
        checkArgs(args);
        OperationMode mode = OperationMode.valueOf(args[0].toUpperCase());
        String srcFile = args[1];

        switch (mode) {
            case ENCODE -> {
                int key = Integer.parseInt(args[2]);
                encodeOrDecodeFile(mode, srcFile, key);
            }
            case DECODE -> {
                int key = Integer.parseInt(args[2]);
                encodeOrDecodeFile(mode, srcFile, key * -1);
            }
            case BRUTEFORCE -> {
                bruteforce();
            }
        }
    }

    private static void encodeOrDecodeFile(OperationMode mode, String srcFile, int key) {
        String destFile = resolveDestFilename(srcFile, mode);
        try (FileReader reader = new FileReader(srcFile);
             StringWriter stringWriter = new StringWriter();
             FileWriter fileWriter = new FileWriter(destFile)) {

            char[] buffer = new char[4096];
            String encodedString = "";
            while (reader.ready()) {
                int real = reader.read(buffer);
                stringWriter.write(buffer, 0, real);
                encodedString = encodeOrDecodeString(stringWriter.toString(), key);
            }
            fileWriter.write(encodedString);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String encodeOrDecodeString(String text, int key) {
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

    private static void bruteforce() {
        //TODO: bruteforce
    }

    private static void checkArgs(String[] args) throws IllegalArgumentException {
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