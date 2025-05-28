package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static public Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }


    static public void configureDatabase() throws DataAccessException {
        createDatabase(); // make DB before adding tables
        try (var conn = getConnection(); var stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS users (
                            username VARCHAR(255) PRIMARY KEY,
                            password VARCHAR(255) NOT NULL,
                            email VARCHAR(255) NOT NULL
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS auth_tokens (
                            token VARCHAR(255) PRIMARY KEY,
                            username VARCHAR(255) NOT NULL,
                            FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS games (
                            id INTEGER PRIMARY KEY AUTO_INCREMENT,
                            gameName VARCHAR(255) NOT NULL,
                            whitePlayer VARCHAR(255),
                            blackPlayer VARCHAR(255),
                            game TEXT NOT NULL,
                            FOREIGN KEY (whitePlayer) REFERENCES users(username) ON DELETE SET NULL,
                            FOREIGN KEY (blackPlayer) REFERENCES users(username) ON DELETE SET NULL
                        );
                    """);

            System.out.println("Tables created inside database: " + databaseName);

        } catch (SQLException ex) {
            throw new DataAccessException("Failed", ex);
        }
    }
}