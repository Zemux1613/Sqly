package de.sqly.interfaces;

import de.sqly.exceptions.ConnectionFailedException;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public interface ISqlyConnection {

    void connect() throws ConnectionFailedException;
    void disconnect();
    boolean isConnected();
    void executeUpdate(String qry);
    CompletableFuture<ResultSet> executeQuery(String qry);

}
