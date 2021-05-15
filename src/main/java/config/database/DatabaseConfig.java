package config.database;

public final class DatabaseConfig {
    public final static String[] DATABASE_CREDIT = new String[]
            {
                    "--url", "jdbc:hsqldb:mem:db",
                    "--user", "sa", "--password", ""
            };

    public final static String PATH_TO_INIT_SCRIPT = "src/main/resources/db/initDb.sql";

    public final static String PATH_TO_INSERT_SCRIPT = "src/main/resources/db/insertData.sql";
}
