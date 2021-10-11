package ru.akirakozov.sd.refactoring.db;

import java.sql.Connection;

public class DBInteractor {
    private final Connection dbConnection = DBConnectionProvider.getDbConnection();

}
