package com.company.soldatowa.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DatabaseUtils {
    public static List<String> selectAllData() {
        List<String> resultList = new ArrayList<>();
        String SQL = "SELECT * FROM data";
        try {
            ResultSet resultSet = DatabaseStarter.getConnection().createStatement().executeQuery(SQL);
            while (resultSet.next()) {
                resultList.add(resultSet.getString(1) + "/" + resultSet.getString(2));
            }
        } catch (SQLException e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage());
        }

        return resultList;
    }

    public static List<String> selectIsTrueData() {
        String SQL = "SELECT * FROM data";
        List<String> resultList = new ArrayList<>();
        try {
            ResultSet resultSet = DatabaseStarter.getConnection().createStatement().executeQuery(SQL);
            while (resultSet.next()) {
                resultList.add(resultSet.getString(2));
            }
        } catch (SQLException e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage());
        }
        return resultList;
    }
}
