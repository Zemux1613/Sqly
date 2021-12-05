package de.sqly;

import de.sqly.exceptions.ConnectionFailedException;
import de.sqly.interfaces.ISqlyConnection;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

public class SqlyConnection implements ISqlyConnection {

    private SqlyData sqlyData;

    @Getter
    private Connection connection;

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public SqlyConnection(SqlyData sqlyData){
        this.sqlyData = sqlyData;
        Sqly.getInstance().getConnectionPool().add(this);
    }

    @Override
    public void connect() throws ConnectionFailedException {

        final String url = "jdbc:mysql://" + sqlyData.getHost() + ":" + sqlyData.getPort() + "/" + sqlyData.getDatabase();
        try {
            this.connection = DriverManager.getConnection(url, sqlyData.getUsername(), sqlyData.getPasswort());
            System.out.println(Sqly.getInstance().getPrefix() + "The database connection was successfully created.");
        } catch (SQLException e) {
            throw new ConnectionFailedException(url);
        }

    }

    @Override
    public void disconnect() {

        try {
            if(!this.connection.isClosed()){
                this.connection.close();
                System.out.println(Sqly.getInstance().getPrefix() + "The database connection to "+this.connection.getClientInfo().toString()+" was successfully closed.");
            }
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
