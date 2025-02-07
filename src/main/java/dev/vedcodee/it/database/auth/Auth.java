package dev.vedcodee.it.database.auth;

import lombok.Getter;

@Getter
public class Auth {

    private final String database, ip, username, password;
    private final String[] args;
    private final int port;

    public Auth(String database, String ip, int port, String username, String password, String... args) {
        this.database = database;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.args = args;
    }


    public String getUrl() {
        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append("jdbc:mysql://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/")
                .append(database);

        if (args.length > 0) {
            urlBuilder.append("?");
            for (int i = 0; i < args.length; i++) {
                urlBuilder.append(args[i]);
                if (i < args.length - 1) {
                    urlBuilder.append("&");
                }
            }
        }

        return urlBuilder.toString();
    }

}

