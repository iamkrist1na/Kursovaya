package com.company.soldatowa.database;

import com.company.soldatowa.config.application.ApplicationConfig;
import com.company.soldatowa.config.database.DatabaseConfig;
import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class DatabaseStarter {
    private final static EmbeddedDatabase db;

    static {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        db = builder
                .setType(EmbeddedDatabaseType.HSQL)
                .setName("db")
                .build();
        Connection connection;
        try {
            connection = db.getConnection();
            ScriptUtils.executeSqlScript(connection, new FileSystemResource(ApplicationConfig.pathToInitScript));
            ScriptUtils.executeSqlScript(connection, new FileSystemResource(ApplicationConfig.pathToUpdateScript));
        } catch (SQLException e) {
            Logger.getGlobal().warning(e.getLocalizedMessage());
        }
    }

    public static synchronized void startDbManager() {
        String[] dbCredits = DatabaseConfig.DATABASE_CREDIT;
        DatabaseManagerSwing.main(dbCredits);

    }

    public static synchronized Connection getConnection() throws SQLException {
        return db.getConnection();
    }
}
