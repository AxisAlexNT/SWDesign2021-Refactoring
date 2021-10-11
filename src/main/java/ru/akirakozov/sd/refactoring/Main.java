package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.db.DBConnectionProvider;
import ru.akirakozov.sd.refactoring.db.repository.ProductRepository;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

/**
 * @author akirakozov
 */
public class Main {
    private static final @NotNull String DB_URL = "jdbc:sqlite:test.db";
    private static final int SERVER_PORT = 8081;

    public static void main(String[] args) throws Exception {
        final Server server = new Server(SERVER_PORT);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        final DBConnectionProvider dbConnectionProvider = new DBConnectionProvider(DB_URL);
        final ProductRepository productRepository = new ProductRepository(dbConnectionProvider);

        context.addServlet(new ServletHolder(new AddProductServlet()), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(productRepository)), "/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(productRepository)), "/query");

        server.start();
        server.join();
    }
}
