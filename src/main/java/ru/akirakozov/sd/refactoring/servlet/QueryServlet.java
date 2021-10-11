package ru.akirakozov.sd.refactoring.servlet;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.akirakozov.sd.refactoring.db.repository.ProductRepository;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.view.QueryResultPage;
import ru.akirakozov.sd.refactoring.view.ResponsePage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akirakozov
 */
@RequiredArgsConstructor
public class QueryServlet extends HttpServlet {
    private static final @NotNull String REQUEST_PARAMETER_NAME = "command";
    private static final Map<String, QueryType> queryTypeByCommand = Arrays.stream(QueryType.values()).collect(Collectors.toMap(qt -> qt.queryCommandName, qt -> qt));
    private final ProductRepository productRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final @NonNull String command = request.getParameter(REQUEST_PARAMETER_NAME);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        final @Nullable QueryType queryType = queryTypeByCommand.get(command);
        if (queryType == null) {
            response.getWriter().println("Unknown command: " + command);
            return;
        }

        final ResponsePage responsePage;
        switch (queryType) {
            case MAX -> {
                final Optional<Product> maxPricedProduct = productRepository.getMaxPricedProduct();
                final String queryResult = maxPricedProduct.map(p -> String.format("%s %d", p.getName(), p.getPrice())).orElse("");
                responsePage = QueryResultPage.builder().queryHeader("<h1>Product with max price: </h1>").queryResult(queryResult).build();
            }
            case MIN -> {
                final Optional<Product> minPricedProduct = productRepository.getMinPricedProduct();
                final String queryResult = minPricedProduct.map(p -> String.format("%s %d", p.getName(), p.getPrice())).orElse("");
                responsePage = QueryResultPage.builder().queryHeader("<h1>Product with min price: </h1>").queryResult(queryResult).build();
            }
            case SUM -> {
                final int productsPriceSum = productRepository.getProductsPriceSum();
                responsePage = QueryResultPage.builder().queryHeader("Summary price: ").queryResult(String.valueOf(productsPriceSum)).build();
            }
            case COUNT -> {
                final int productCount = productRepository.getProductCount();
                responsePage = QueryResultPage.builder().queryHeader("Number of products: ").queryResult(String.valueOf(productCount)).build();
            }
            default -> {
                throw new IllegalStateException("Impossible query type");
            }
        }

        final @NotNull @NonNull String responsePageHTMLCode = responsePage.getHTMLCode();
        response.getWriter().println(responsePageHTMLCode);


    }

    public enum QueryType {
        MAX("max"),
        MIN("min"),
        SUM("sum"),
        COUNT("count");

        @Getter
        public final @NotNull @NonNull String queryCommandName;

        QueryType(final @NotNull String commandName) {
            this.queryCommandName = commandName;
        }

        public static @Nullable QueryType getTypeByCommandName(final @NotNull String commandName) {
            final List<QueryType> commands = Arrays.stream(QueryType.values()).filter(t -> Objects.equals(t.getQueryCommandName(), commandName)).toList();
            assert commands.size() <= 1 : "There are multiple commands with the same query command parameter";
            if (commands.isEmpty()) {
                // No such command
                return null;
            } else {
                return commands.get(0);
            }
        }
    }

}
