package de.sqly;

import de.sqly.interfaces.ISqlyConnection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

@RequiredArgsConstructor
public class SqlyConnection implements ISqlyConnection {

    private final SqlyData sqlyData;

    @Getter
    private Connection connection;

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void connect() {

        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + sqlyData.getHost() + ":" + sqlyData.getPort() + "/" + sqlyData.getDatabase(), sqlyData.getUsername(), sqlyData.getPasswort());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void disconnect() {

        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isConnected() {

        boolean ret = false;

        try {
            if (this.connection != null) {
                if (!this.connection.isClosed()) {
                    if (this.connection.isValid(10)) {
                        ret = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;

    }

    @Override
    public void executeUpdate(String qry) {

        threadPool.submit(() -> {

            Callable<Integer> callable = null;

            try {
                callable = this.connection.prepareStatement(qry)::executeUpdate;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                return callable;
            }

        });

    }

    @Override
    public CompletableFuture<ResultSet> executeQuery(String qry) {

        CompletableFuture<ResultSet> future = new CompletableFuture<>();

        threadPool.submit(() -> {
            try {
                future.complete(this.connection.prepareStatement(qry).executeQuery());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });

        return future;

    }
}
