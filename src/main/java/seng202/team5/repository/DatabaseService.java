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
import seng202.team5.exceptions.InstanceAlreadyExistsException;

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

    /**
     * gets or creates the database depending on whether it already exists
     * @param url of the database
     */
    private DatabaseService(String url) {
        if (url == null || url.isEmpty()) {
            this.url = getDataBasePath();
        } else {
            this.url = url;
        }

        if (!checkDataBaseExists(this.url)) {
            createDatabaseFile(this.url);
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
     * WARNING Allows for setting specific database url (currently only needed
     * for test databases, but may be useful in future) USE WITH CAUTION.
     * This does not override the current singleton instance so must be the first call.
     *
     * @param url string url of database to load (this needs to be full url e.g.
     *           "jdbc:sqlite:./src/...")
     * @return current singleton instance
     * @throws InstanceAlreadyExistsException if there is already a singleton instance
     */
    public static DatabaseService initialiseInstanceWithUrl(String url)
            throws InstanceAlreadyExistsException {
        if (instance == null) {
            instance = new DatabaseService(url);
        } else {
            throw new InstanceAlreadyExistsException(
                    "Database Manager instance already exists, cannot create with url: " + url);
        }

        return instance;
    }

    /**
     *  WARNING Sets the current singleton instance to null.
     */
    public static void removeInstance() {
        instance = null;
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
        try (InputStream in = getClass().getResourceAsStream("/sql/init_database.sql")) {
            executeSql(in);
        } catch (NullPointerException | IOException e) {
            log.error(e);
        }
    }

    /**
     * executes the given sql query
     * @param in input string of sql query
     */
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
