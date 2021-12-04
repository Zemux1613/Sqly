package de.sqly;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SqlyData {

    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String passwort;

}
