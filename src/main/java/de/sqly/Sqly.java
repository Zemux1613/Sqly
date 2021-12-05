package de.sqly;

import lombok.Getter;

import java.util.LinkedList;

public class Sqly {

    @Getter
    private final String prefix = "[Sqly] ";

    @Getter
    private LinkedList<SqlyConnection> connectionPool;

    private static Sqly instance;

    public Sqly(){
        connectionPool = new LinkedList<>();
    }

    public static Sqly getInstance() {

        if (instance == null) {
            instance = new Sqly();
        }

        return instance;
    }


}
