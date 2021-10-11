package ru.akirakozov.sd.refactoring.db;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.ex.ApplicationBootstrapException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionProvider {
    private final @NotNull @NonNull String DB_URL;

    public DBConnectionProvider(final @NotNull @NonNull String DB_URL) throws ApplicationBootstrapException {
        this.DB_URL = DB_URL;
        testDBConnection();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void testDBConnection() {
        try (final Connection c = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (final SQLException sqlException) {
            throw new ApplicationBootstrapException("Cannot init DB Layer", sqlException);
        }
    }
}
