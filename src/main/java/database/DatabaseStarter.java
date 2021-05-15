package database;

import config.database.DatabaseConfig;
import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseStarter {
    private final static EmbeddedDatabase db;

    static {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        db = builder
                .setType(EmbeddedDatabaseType.HSQL)
                .setName("db")
                .addScript("db/initDb.sql")
                .addScript("db/insertData.sql")
                .build();
    }

    public static synchronized void startDbManager() {
        String[] dbCredits = DatabaseConfig.DATABASE_CREDIT;
        DatabaseManagerSwing.main(dbCredits);

    }

    public static synchronized Connection getConnection() throws SQLException {

        return db.getConnection();
    }
}
