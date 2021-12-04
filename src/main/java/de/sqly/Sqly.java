package de.sqly;

import lombok.Getter;

public class Sqly {

    @Getter
    private final String prefix = "[Sqly] ";

    private static Sqly instance;

    public static Sqly getInstance() {

        if (instance == null) {
            instance = new Sqly();
        }

        return instance;
    }


}
