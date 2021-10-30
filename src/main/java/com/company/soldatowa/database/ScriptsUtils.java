package com.company.soldatowa.database;

import com.company.soldatowa.config.application.ApplicationConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ScriptsUtils {

    public static List<String> readAllLines() throws IOException {

        return Files.readAllLines(
                Paths.get(ApplicationConfig.pathToUpdateScript), StandardCharsets.UTF_8);
    }

    public static void writeNewLine(String data, String flag) throws IOException {
        String row = "INSERT INTO data VALUES ('" + data + "', " + flag + ");";
        Files.write(Paths.get(ApplicationConfig.pathToUpdateScript), row.getBytes(StandardCharsets.UTF_8));
        Logger.getGlobal().log(Level.INFO, "Added row: [" + data + ", " + flag + "]");
    }

    public static void deleteLine(String data, String flag) throws IOException {
        List<String> lines = readAllLines();
        String row = "INSERT INTO data VALUES ('" + data + "', " + flag + ");";
        lines.remove(row);
        Files.write(Paths.get(ApplicationConfig.pathToUpdateScript), lines);
        Logger.getGlobal().log(Level.INFO, "Deleted row: [" + data + ", " + flag + "]");
    }
}
