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

import javax.servlet.http.HttpServlet;
import java.util.Map;

/**
 * Main class of this HTTP Application Server.
 *
 * @author akirakozov
 */
public class Main {
    private static final @NotNull String DB_URL = "jdbc:sqlite:test.db";
    private static final int SERVER_PORT = 8081;

    /**
     * Launches HTTP Application Server.
     *
     * @param args An array of arguments.
     * @throws Exception In case component fails to start or was interrupted.
     */
    public static void main(String[] args) throws Exception {
        final Server server = new Server(SERVER_PORT);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        final DBConnectionProvider dbConnectionProvider = new DBConnectionProvider(DB_URL);
        final ProductRepository productRepository = new ProductRepository(dbConnectionProvider);

        final Map<String, HttpServlet> applicationServlets = Map.of(
                "/add-product",     new AddProductServlet(productRepository),
                "/get-products",    new GetProductsServlet(productRepository),
                "/query",           new QueryServlet(productRepository)
        );

        applicationServlets.forEach((path, servlet) ->
                context.addServlet(new ServletHolder(servlet), path)
        );

        server.start();
        try {
            server.join();
        } catch (final InterruptedException ignored) {
            // Ok, server is interrupted and will shut down
        }
    }
}
