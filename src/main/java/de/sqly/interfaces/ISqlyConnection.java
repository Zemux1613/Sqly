package de.sqly.interfaces;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public interface ISqlyConnection {

    void connect();
    void disconnect();
    boolean isConnected();
    void executeUpdate(String qry);
    CompletableFuture<ResultSet> executeQuery(String qry);

}
