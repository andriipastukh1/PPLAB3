package io;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BattleRecorder {
    private static final String LOGS_FOLDER = "logs";

    public static String saveBattleLog(List<String> log) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss"));
        String filename = "Battle[" + timestamp + "].txt";
        Path p = Paths.get(LOGS_FOLDER, filename);
        Files.createDirectories(p.getParent());
        Files.write(p, log, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return p.toString();
    }

    public static List<String> loadLog(String filename) throws IOException {
        Path p = Paths.get(filename);
        if (!Files.exists(p)) throw new FileNotFoundException("Файл не знайдено: " + filename);
        return Files.readAllLines(p);
    }
}