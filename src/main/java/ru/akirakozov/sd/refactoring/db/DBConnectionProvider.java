package ru.akirakozov.sd.refactoring.db;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.ex.ApplicationBootstrapException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class that's responsible with JDBC communications between an Application and Database.
 */
public class DBConnectionProvider {
    private final @NotNull @NonNull String DB_URL;

    /**
     * Constructs new {@link DBConnectionProvider} using given Database URL.
     *
     * @param DB_URL An URL of database to be connected to.
     * @throws ApplicationBootstrapException In case initial connection to the database cannot be established and/or if schema cannot be defined.
     */
    public DBConnectionProvider(final @NotNull @NonNull String DB_URL) throws ApplicationBootstrapException {
        this.DB_URL = DB_URL;
        testDBConnection();
    }

    /**
     * Returns a JDBC connection to the Application's Database. This method could be modified to cache connections, so it does not guarantee that connection would be newly created, but guarantees that it's in a valid state.
     *
     * @return A JDBC connection to the Application's Database.
     * @throws SQLException In case connection to the database cannot be established.
     */
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
