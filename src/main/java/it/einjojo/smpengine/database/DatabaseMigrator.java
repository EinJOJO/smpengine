package it.einjojo.smpengine.database;

import it.einjojo.smpengine.util.SQLUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * This class is responsible for migrating the database to the latest schema version.
 * @author EinJOJO
 * @since 1.0
 * @version 1.0 - 14.12.2023
 * @see DataSource
 * @see SQLUtil
 *
 */
public class DatabaseMigrator {
    private final DataSource cp;
    private final ClassLoader classLoader;
    private final Logger logger = Logger.getLogger("DatabaseMigrator");

    public DatabaseMigrator(DataSource cp) {
        this.cp = cp;
        this.classLoader = getClass().getClassLoader();
    }


    /**
     * Migrates the database to the latest schema version.
     * @throws MigrationException if the migration fails.
     */
    public void migrate() {
        try (Connection connection = cp.getConnection();) {
            createSchemaTracker(connection);
            int currentVersion = getSchemaVersion(connection);
            int latestVersion = currentVersion;
            if (latestVersion == 0) {
                logger.info("Migration: V0 - Initializing database...");
                executeFile("db/init_db.sql", connection);
                latestVersion = 1;
            }
            if (latestVersion != currentVersion) { // Has Updated version
                logger.info("Migration complete from " + currentVersion + " to " + latestVersion);
                setSchemaVersion(connection, latestVersion);
            }
        } catch (SQLException e) {
            throw new MigrationException("SQLEXCEPTION: Migration failed: " + e.getMessage(), e);
        }
    }

    private void createSchemaTracker(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS `db_schema` (`version` INT NOT NULL, migrated_at TIMESTAMP NOT NULL, PRIMARY KEY (`version`));")) {
            int res = ps.executeUpdate();
            logger.info("Created db_schema table + " + res);
        }
    }

    private int getSchemaVersion(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT `version` FROM `db_schema` ORDER BY `version` DESC LIMIT 1;")) {
            ps.executeQuery();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return rs.getInt("version");
                } else {
                    return 0;
                }
            }
        }
    }

    private void setSchemaVersion(Connection con, int version) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO `db_schema` (`version`, `migrated_at`) VALUES (?, CURRENT_TIMESTAMP);")) {
            ps.setInt(1, version);
            ps.executeUpdate();
        }
    }

    private void executeFile(String resourcePath, Connection connection) throws SQLException {
        try (InputStream stream = classLoader.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new MigrationException("InputStream is null - " + resourcePath + " not found?");
            }
            logger.info("Loading " + resourcePath + "...");
            String sqlFile = new String(stream.readAllBytes());
            String[] statementArray = sqlFile.split(";");
            logger.info("Executing " + statementArray.length + " queries");
            for (String sanitize : statementArray) {
                String statement = SQLUtil.sanitize(sanitize);
                if (statement.isBlank() || statement.isEmpty()) continue;
                logger.info(statement);
                try (PreparedStatement ps = connection.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
            logger.info("Migrated to schema version 1");
        } catch (IOException e) {
            throw new MigrationException("Failed to load init.sql", e);
        }
    }

}


/**
 * This exception is thrown when the migration fails.
 */
class MigrationException extends RuntimeException {
    public MigrationException(Exception e) {
        super(e);
    }

    public MigrationException(String message, Exception e) {
        super(message, e);
    }

    public MigrationException(String message) {
        super(message);
    }
}
