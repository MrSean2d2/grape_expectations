package seng202.team5.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A singleton service to handle database connections. Based on DataBaseManager
 * from the
 * <a href="https://eng-git.canterbury.ac.nz/men63/seng202-advanced-fx-public">advanced JavaFX lab</a>.
 *
 * @author Sean Reitsma
 * @author (Advanced JavaFX lab) Morgan English
 */
public class DatabaseService {
    private static DatabaseService instance;
    private static final Logger log = LogManager.getLogger(DatabaseService.class);
    private final String url;

    private DatabaseService(String url) {
        if (url == null || url.isEmpty()) {
            this.url = getDataBasePath();
        } else {
            this.url = url;
        }
        if (!checkDataBaseExists(url)) {
            createDatabaseFile(url);
            resetDb();
        }
    }

    /**
     * Singleton method to get the current instance if it exists. Otherwise,
     * create one.
     *
     * @return the singleton instance of {@link DatabaseService}
     */
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService(null);
        }
        return instance;
    }

    /**
     * Connect to the database and return the connection object.
     *
     * @return the database connection.
     */
    public Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.error(e);
        }
        return connection;
    }

    /**
     * Initialise the database.
     */
    public void resetDb() {
        try (InputStream in = getClass().getResourceAsStream("/sql/init_db.sql")) {
            executeSql(in);
        } catch (NullPointerException | IOException e) {
            log.error(e);
        }
    }

    private void executeSql(InputStream in) {
        String s;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            String[] individualStatements = sb.toString().split("--SPLIT");
            try (Connection conn = connect();
                    Statement statement = conn.createStatement()) {
                for (String singleStatement : individualStatements) {
                    statement.executeUpdate(singleStatement);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("Error could not find specified database initialisation file", e);
        } catch (IOException e) {
            log.error("Error working with database initialisation file", e);
        } catch (SQLException e) {
            log.error("Error executing sql statements in database initialisation file", e);
        }
    }

    private boolean checkDataBaseExists(String url) {
        File file = new File(url.substring(url.lastIndexOf(":") + 1));
        return file.exists();
    }

    private void createDatabaseFile(String url) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                String metaDriverLog = String.format(
                        "A new database has been created. The driver name is %s",
                        meta.getDriverName());
                log.info(metaDriverLog);
            }
        } catch (SQLException e) {
            log.error("Error creating new database file url:{}", url);
            log.error(e);
        }
    }

    private String getDataBasePath() {
        String path = DatabaseService.class.getProtectionDomain().getCodeSource()
                .getLocation().getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File jarDir = new File(path);
        return "jdbc:sqlite:" + jarDir.getParentFile() + "/database.db";
    }
}
