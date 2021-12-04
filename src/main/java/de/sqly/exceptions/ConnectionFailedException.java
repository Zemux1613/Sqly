package de.sqly.exceptions;

public class ConnectionFailedException extends Exception {

    public ConnectionFailedException(String connectString){
        super("Failed to connect to " + connectString);
    }

}
