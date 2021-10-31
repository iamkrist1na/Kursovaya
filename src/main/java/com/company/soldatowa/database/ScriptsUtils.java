package com.company.soldatowa.database;

import com.company.soldatowa.config.application.ApplicationConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ScriptsUtils {

    public static List<String> readAllLines() throws IOException {
        return Files.readAllLines(Paths.get(ApplicationConfig.pathToUpdateScript), StandardCharsets.UTF_8);
    }

    public static void writeNewLine(String data, String flag) throws IOException {
        List<String> lines = readAllLines();
        String row = "INSERT INTO data VALUES ('" + data + "', " + flag + ");";
        if (!lines.contains(row)) {
            if (flag.equalsIgnoreCase("true")) {
                lines.add(1, row);
            } else {
                lines.add(row);
            }
            Files.write(Paths.get(ApplicationConfig.pathToUpdateScript), lines);
            Logger.getGlobal().log(Level.INFO, "Added row: '" + data + ", " + flag + "'");
        } else {
            Logger.getGlobal().log(Level.INFO, "Row: '" + data + ", " + flag + "' already exist in " + ApplicationConfig.pathToUpdateScript);
        }
    }

    public static void deleteLine(String data, String flag) throws IOException {
        List<String> lines = readAllLines();
        String row = "INSERT INTO data VALUES ('" + data + "', " + flag + ");";
        boolean remove = lines.remove(row);
        if (remove) {
            Files.write(Paths.get(ApplicationConfig.pathToUpdateScript), lines);
            Logger.getGlobal().log(Level.INFO, "Deleted row: '" + data + ", " + flag + "'");
        } else {
            Logger.getGlobal().log(Level.INFO, "Row '" + data + ", " + flag + "' was not found in " + ApplicationConfig.pathToUpdateScript);
        }
    }
}
