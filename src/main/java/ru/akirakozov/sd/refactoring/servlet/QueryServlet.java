package ru.akirakozov.sd.refactoring.servlet;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.akirakozov.sd.refactoring.db.repository.ProductRepository;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.view.QueryResultPage;
import ru.akirakozov.sd.refactoring.view.ResponsePage;
import ru.akirakozov.sd.refactoring.view.SimpleTextPage;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A servlet that implements queries on {@link Product}s processed by this application.
 *
 * @author akirakozov
 */
public class QueryServlet extends AbstractProductServlet {
    private static final @NotNull String REQUEST_PARAMETER_NAME = "command";
    private static final @NotNull Map<@NotNull String, @NotNull QueryType> queryTypeByCommand = Arrays.stream(QueryType.values()).collect(Collectors.toMap(qt -> qt.queryCommandName, qt -> qt));

    /**
     * Constructs new {@link QueryServlet} using given {@link ProductRepository}.
     *
     * @param productRepository A {@link ProductRepository} that's backed by this application's database.
     */
    public QueryServlet(final @NotNull @NonNull ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    protected @NotNull @NonNull ResponsePage generateResponsePage(final @NotNull @NonNull HttpServletRequest request) throws IOException {
        final @NonNull String command = request.getParameter(REQUEST_PARAMETER_NAME);

        final @Nullable QueryType queryType = queryTypeByCommand.get(command);
        if (queryType == null) {
            return SimpleTextPage.builder().pageText("Unknown command: " + command).build();
        }

        final ResponsePage responsePage;
        switch (queryType) {
            case MAX -> {
                final Optional<Product> maxPricedProduct = this.getProductRepository().getMaxPricedProduct();
                final String queryResult = maxPricedProduct.map(p -> String.format("%s %d", p.getName(), p.getPrice())).orElse("");
                responsePage = QueryResultPage.builder().queryHeader("<h1>Product with max price: </h1>").queryResult(queryResult).build();
            }
            case MIN -> {
                final Optional<Product> minPricedProduct = this.getProductRepository().getMinPricedProduct();
                final String queryResult = minPricedProduct.map(p -> String.format("%s %d", p.getName(), p.getPrice())).orElse("");
                responsePage = QueryResultPage.builder().queryHeader("<h1>Product with min price: </h1>").queryResult(queryResult).build();
            }
            case SUM -> {
                final int productsPriceSum = this.getProductRepository().getProductsPriceSum();
                responsePage = QueryResultPage.builder().queryHeader("Summary price: ").queryResult(String.valueOf(productsPriceSum)).build();
            }
            case COUNT -> {
                final int productCount = this.getProductRepository().getProductCount();
                responsePage = QueryResultPage.builder().queryHeader("Number of products: ").queryResult(String.valueOf(productCount)).build();
            }
            default -> throw new IllegalStateException("Impossible query type");
        }

        return responsePage;
    }


    /**
     * An enumeration that describes which query is requested.
     */
    public enum QueryType {
        /**
         * Maximum query will return a {@link Product} with the maximal value of field {@code price}, or nothing in case no {@link Product}s are stored in this application.
         */
        MAX("max"),
        /**
         * Minimum query will return a {@link Product} with the minimal value of field {@code price}, or nothing in case no {@link Product}s are stored in this application.
         */
        MIN("min"),
        /**
         * Count query will return a total sum of field {@code price} values of each {@link Product}s stored in this application, counting all possible duplicates.
         * If no {@link Product}s are stored in this application, sum query result will equal to zero.
         */
        SUM("sum"),
        /**
         * Count query will return a total number of {@link Product}s stored in this application, counting all possible duplicates.
         * If no {@link Product}s are stored in this application, count query result will equal to zero.
         */
        COUNT("count");

        /**
         * Describes a 'command' HTTP GET parameter name that corresponds to each type of query.
         */
        @Getter
        public final @NotNull @NonNull String queryCommandName;

        /**
         * Constructs a new type of query. All queries must have distinct values of {@code commandName} field values.
         *
         * @param commandName A value of 'command' HTTP GET parameter that corresponds to this new query type.
         */
        QueryType(final @NotNull String commandName) {
            this.queryCommandName = commandName;
        }
    }

}
