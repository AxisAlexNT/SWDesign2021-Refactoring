package ru.akirakozov.sd.refactoring.db;

import ru.akirakozov.sd.refactoring.ex.ApplicationBootstrapException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionProvider {
    private static final Connection dbConnection;

    static {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();

            dbConnection = c;
        } catch (final SQLException sqlException) {
            throw new ApplicationBootstrapException("Cannot init DB Layer", sqlException);
        }
    }

    public static Connection getDbConnection(){
        return dbConnection;
    }
}
