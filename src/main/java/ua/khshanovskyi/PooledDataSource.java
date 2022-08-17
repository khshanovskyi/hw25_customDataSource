package ua.khshanovskyi;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PooledDataSource extends PGSimpleDataSource {
    private final Queue<Connection> connectionQueue;

    @SneakyThrows
    public PooledDataSource(String url , String login, String password) {
        this.connectionQueue = new ConcurrentLinkedDeque<>();
        this.setURL(url);
        this.setUser(login);
        this.setPassword(password);

        for (int i = 0; i < 10; i++) {
            connectionQueue.add(new ConnectionProxy(super.getConnection(), this));
        }
    }

    public Queue<Connection> getConnectionQueue() {
        return connectionQueue;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionQueue.poll();
    }
}
