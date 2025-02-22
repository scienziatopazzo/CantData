package dev.vedcodee.it.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.vedcodee.it.data.CantData;
import dev.vedcodee.it.database.auth.Auth;
import dev.vedcodee.it.types.DataType;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public abstract class CantDatabase<T extends CantData> {

    private final Auth auth;
    private final boolean useCache;
    private final String tableName;
    private List<T> cached;

    @Getter private HikariDataSource dataSource;

    public CantDatabase(Auth auth, boolean useCache, String tableName) {
        this.auth = auth;
        this.useCache = useCache;
        this.tableName = tableName;
        this.cached = new CopyOnWriteArrayList<>();
        connect();
        if(useCache) this.cached = getDatas(true);
    }

    protected abstract T createData(UUID id, String data);

    private void connect() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(auth.getUrl());
        config.setUsername(auth.getUsername());
        config.setPassword(auth.getPassword());

        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);

        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "data TEXT NOT NULL" +
                ");";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void save(T data) {
        insertData(data.getUuid(), data.serialize());
    }

    public void delete(T data) {
        delete(data.getUuid());
    }

    private void insertData(UUID id, String data) {
        String sql = "INSERT INTO " + tableName + " (id, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = VALUES(data);";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.setString(2, data);
            stmt.executeUpdate();
            if (useCache) {
                if(cached.stream().noneMatch(d -> d.getUuid().equals(id))) {
                    T newData = createData(id, data);
                    cached.add(newData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void delete(UUID id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
            if (useCache) {
                cached.removeIf(d -> d.getUuid().equals(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private T queryDataByUUID(UUID uuid) {
        String sql = "SELECT data FROM " + tableName + " WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dataStr = rs.getString("data");
                    return createData(uuid, dataStr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<T> queryAllData() {
        List<T> dataList = new ArrayList<>();
        String sql = "SELECT id, data FROM " + tableName + ";";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                String dataStr = rs.getString("data");
                T t = createData(id, dataStr);
                dataList.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<T> getDatas(boolean query) {
        if (useCache) {
            if (query) {
                List<T> queriedData = queryAllData();
                cached.clear();
                cached.addAll(queriedData);
            }
            return cached;
        } else {
            return queryAllData();
        }
    }

    public List<T> getDatas() {
        return getDatas(false);
    }

    public T find(UUID uuid, boolean queryIfNull) {
        if (useCache) {
            for (T data : cached) {
                if (data.getUuid().equals(uuid)) {
                    return data;
                }
            }
            if (queryIfNull) {
                T data = queryDataByUUID(uuid);
                if (data != null) {
                    cached.add(data);
                }
                return data;
            } else {
                return null;
            }
        } else {
            return queryDataByUUID(uuid);
        }
    }

    public T find(UUID uuid) {
        return find(uuid, false);
    }

    public <E extends Enum<E>> T find(E key, Object value) {
        for (T data : getDatas()) {
            Optional<DataType<?>> opt = Optional.ofNullable(data.from(key));
            if (opt.isPresent() && opt.get().get().equals(value)) return data;
        }
        return null;
    }

    public <E extends Enum<E>> List<T> find(E key, int top, boolean reverse) {
        List<T> results = getDatas();

        results.sort((a, b) -> {
            DataType<?> aValue = a.from(key);
            DataType<?> bValue = b.from(key);

            if (aValue instanceof Comparable && bValue instanceof Comparable) {
                int compareResult = ((Comparable) aValue).compareTo(bValue);
                return reverse ? -compareResult : compareResult;
            }
            return 0;
        });

        return results.stream().limit(top).collect(Collectors.toList());
    }

}