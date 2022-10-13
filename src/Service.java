import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.WRITE;

public class Service {

    static void writeStringToFile(String text, String destFile) throws IOException {
        Path destFilePath = Path.of(destFile);
        if (Files.notExists(destFilePath)) {
            Files.createFile(destFilePath);
        }
        Files.writeString(destFilePath, text, StandardCharsets.UTF_8, WRITE);

    }

    static String addFilenameSuffix(String srcFile, Main.Mode mode, int key) {
        int pointLocation = srcFile.lastIndexOf('.');
        String pathWithoutExt = srcFile.substring(0, pointLocation);
        String fileExtension = srcFile.substring(pointLocation);
        String filename;

        if (mode == Main.Mode.ENCODE) {
            filename = pathWithoutExt + "(encoded)" + fileExtension;

        } else if (mode == Main.Mode.DECODE) {
            int bracketLocation = srcFile.lastIndexOf('(');
            if (bracketLocation == -1) {
                bracketLocation = pointLocation;
            }
            pathWithoutExt = srcFile.substring(0, bracketLocation);
            fileExtension = srcFile.substring(pointLocation);
            filename = pathWithoutExt + "(decoded)" + fileExtension;
        } else {
            filename = pathWithoutExt + "(decoded key-" + key + ")" + fileExtension;
        }

        return filename;
    }

    static void checkArgs(String[] args) throws IllegalArgumentException, IOException {
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
    }// TODO: move to Service

    static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}