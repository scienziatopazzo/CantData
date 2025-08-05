package common.vedcodee.it.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.vedcodee.it.cantdata.auth.ConnectionAuth;
import common.vedcodee.it.utils.Auth;

import java.sql.*;
import java.util.*;

public class MySQLConnection implements ConnectionAuth {

    private final Auth auth;
    private HikariDataSource dataSource;

    public MySQLConnection(Auth auth) {
        this.auth = auth;
    }

    @Override
    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(auth.getUrl() + "?rewriteBatchedStatements=true&useSSL=false&serverTimezone=UTC");
        config.setUsername(auth.getUsername());
        config.setPassword(auth.getPassword());

        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(3000);
        config.setLeakDetectionThreshold(60000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void createDepartment(String department) {
        String sql = "CREATE TABLE IF NOT EXISTS `" + department + "` (" +
                "`key` VARCHAR(255) PRIMARY KEY," +
                "`json` TEXT NOT NULL," +
                "`group_id` VARCHAR(255) NOT NULL," +
                "INDEX idx_group_id (`group_id`)" +
                ")";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(String department, List<String> keys, String json) {
        String groupId = UUID.randomUUID().toString();
        String sql = "INSERT INTO `" + department + "` (`key`, `json`, `group_id`) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `json` = VALUES(`json`), `group_id` = VALUES(`group_id`)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String key : keys) {
                ps.setString(1, key);
                ps.setString(2, json);
                ps.setString(3, groupId);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(String department, String key) {
        String sql = "DELETE FROM `" + department + "` WHERE `group_id` = " +
                "(SELECT group_id FROM (SELECT group_id FROM `" + department + "` WHERE `key` = ? LIMIT 1) AS sub)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String department, String key) {
        String sql = "SELECT json FROM `" + department + "` WHERE `key` = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("json");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<List<String>, String> all(String department) {
        Map<List<String>, String> result = new HashMap<>();
        String sql = "SELECT `key`, `json` FROM `" + department + "`";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String key = rs.getString("key");
                String json = rs.getString("json");
                result.put(Collections.singletonList(key), json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void clear(String department) {
        String sql = "TRUNCATE TABLE `" + department + "`";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
