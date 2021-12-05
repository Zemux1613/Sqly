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

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    public SqlyConnection(SqlyData sqlyData) {
        this.sqlyData = sqlyData;
        Sqly.getInstance().getConnectionPool().add(this);
    }

    /**
     * This method creates a connection to the database specified in the SqlyData object.
     *
     * @throws ConnectionFailedException If the connection to the database server could not be reached, this exception is thrown.
     */
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

    /**
     * This method clears an open connection between sqly and your database server.
     * If the connection is already closed, the method does nothing.
     */
    @Override
    public void disconnect() {

        try {
            if (!this.connection.isClosed()) {
                this.connection.close();
                System.out.println(Sqly.getInstance().getPrefix() + "The database connection to " + this.connection.getClientInfo().toString() + " was successfully closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method validates the open connection to the database server.
     *
     * @return True - If the connection is valid and usable
     */
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

    /**
     * Execute a PreparedStatement for the open connection as update.
     *
     * @param qry The query to be executed.
     */
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

    /**
     * Execute a PreparedStatement for the opened connection as a query.
     *
     * @param qry The query to be executed.
     * @return CompletableFuture with the ResultSet of your query.
     */
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
