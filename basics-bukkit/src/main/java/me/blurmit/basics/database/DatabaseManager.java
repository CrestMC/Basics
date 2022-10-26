package me.blurmit.basics.database;

import com.zaxxer.hikari.HikariDataSource;
import me.blurmit.basics.Basics;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class DatabaseManager {

    private final Basics plugin;
    private final HikariDataSource dataSource;
    private final ExecutorService executorService;


    public DatabaseManager(Basics plugin, String host, String username, String password, String database) {
        this.plugin = plugin;
        this.executorService = Executors.newFixedThreadPool(4);

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + host + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8");
        dataSource.setPoolName("Basics" + "-" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setLeakDetectionThreshold(60 * 1000L);
        dataSource.addDataSourceProperty("cachePrepStmts" , "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize" , "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");

        try (Connection connection = dataSource.getConnection()) {
            plugin.getLogger().info("Connected to database successfully.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database.", e);
        }
    }

    public void shutdown() {
        dataSource.close();
    }

    public void useConnection(SQLConsumer<Connection> consumer) {
        try (Connection connection = getConnection()) {
            consumer.accept(connection);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to get database connection", e);
        }
    }

    public void useAsynchronousConnection(SQLConsumer<Connection> consumer) {
        executorService.submit(() -> useConnection(consumer));
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
