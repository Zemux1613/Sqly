package de.sqly;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class representate the whole connection data for an @SqlyConnection
 */

@Getter
@AllArgsConstructor
public class SqlyData {
    /**
     * A string representing the host name of the target database management system
     */
    private final String host;

    /**
     * A string representing the target database management system port
     */
    private final String port;

    /**
     * A string representing the name of the target database management system
     */
    private final String database;

    /**
     * A string representing the username of the user to be used to create the database connection
     */
    private final String username;

    /**
     * A string representing the password of the user to be used to create the database connection
     */
    private final String password;

}
