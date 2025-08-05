package common.vedcodee.it.database;

import dev.vedcodee.it.cantdata.auth.ConnectionAuth;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.api.sync.RedisKeyCommands;
import lombok.var;

import java.util.*;

public class RedisConnector implements ConnectionAuth {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;

    public RedisConnector(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect() {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(host)
                .withPort(port);

        if (username != null && !username.isEmpty()) {
            builder.withAuthentication(username, password == null ? new char[0] : password.toCharArray());
        } else if (password != null && !password.isEmpty()) {
            builder.withPassword(password.toCharArray());
        }

        redisClient = RedisClient.create(builder.build());
        connection = redisClient.connect();
        commands = connection.sync();
    }

    @Override
    public void disconnect() {
        if (connection != null) connection.close();
        if (redisClient != null) redisClient.shutdown();
    }

    @Override
    public void createDepartment(String department) {
    }

    @Override
    public void set(String department, List<String> keys, String json) {
        String groupId = UUID.randomUUID().toString();
        Map<String, String> toSet = new HashMap<>();
        for (String key : keys) {
            String redisKey = department + ":" + key;
            toSet.put(redisKey, json);
            toSet.put(department + ":group:" + key, groupId);
        }
        commands.mset(toSet);
        commands.sadd(department + ":group-keys:" + groupId, keys.toArray(new String[0]));
    }

    @Override
    public void remove(String department, String key) {
        String groupId = commands.get(department + ":group:" + key);
        if (groupId == null) return;
        Set<String> keys = commands.smembers(department + ":group-keys:" + groupId);
        if (keys == null || keys.isEmpty()) return;

        List<String> allKeysToDelete = new ArrayList<>(keys.size() * 2 + 1);
        for (String k : keys) {
            allKeysToDelete.add(department + ":" + k);
            allKeysToDelete.add(department + ":group:" + k);
        }
        allKeysToDelete.add(department + ":group-keys:" + groupId);
        commands.del(allKeysToDelete.toArray(new String[0]));
    }

    @Override
    public String get(String department, String key) {
        return commands.get(department + ":" + key);
    }

    @Override
    public Map<List<String>, String> all(String department) {
        Map<List<String>, String> result = new HashMap<>();
        Set<String> keys = new HashSet<>();
        ScanCursor cursor = ScanCursor.INITIAL;
        ScanArgs args = ScanArgs.Builder.matches(department + ":*");

        do {
            KeyScanCursor<String> scanResult = commands.scan(cursor, args);
            cursor = scanResult;
            for (String key : scanResult.getKeys()) {
                if (key.contains(":group")) continue;
                keys.add(key);
            }
        } while (!cursor.isFinished());

        if (!keys.isEmpty()) {
            List<String> keyList = new ArrayList<>(keys);
            List<KeyValue<String, String>> values = commands.mget(keyList.toArray(new String[0]));
            for (int i = 0; i < keyList.size(); i++) {
                String fullKey = keyList.get(i);
                String value = values.get(i).getValueOrElse(null);
                if (value != null) {
                    String rawKey = fullKey.substring(department.length() + 1);
                    result.put(Collections.singletonList(rawKey), value);
                }
            }
        }

        return result;
    }

    @Override
    public void clear(String department) {
        ScanCursor cursor = ScanCursor.INITIAL;
        ScanArgs args = ScanArgs.Builder.matches(department + ":*");

        do {
            KeyScanCursor<String> scanResult = commands.scan(cursor, args);
            cursor = scanResult;
            List<String> keysToDelete = scanResult.getKeys();
            if (!keysToDelete.isEmpty()) {
                commands.del(keysToDelete.toArray(new String[0]));
            }
        } while (!cursor.isFinished());
    }

}
