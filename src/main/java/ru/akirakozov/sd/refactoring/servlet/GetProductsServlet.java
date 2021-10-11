package ru.akirakozov.sd.refactoring.servlet;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.db.repository.ProductRepository;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.view.ProductListPage;
import ru.akirakozov.sd.refactoring.view.ResponsePage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * A servlet that's used to get a list of {@link Product}s that are withheld by this application.
 *
 * @author akirakozov
 */
public class GetProductsServlet extends AbstractProductServlet {
    /**
     * Constructs a new {@link GetProductsServlet} using a {@link ProductRepository} as a repository.
     * @param productRepository A repository to take {@link Product}s from.
     */
    public GetProductsServlet(final @NotNull @NonNull ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    protected @NotNull @NonNull ResponsePage generateResponsePage(final @NotNull @NonNull HttpServletRequest request) {
        final List<Product> products = this.getProductRepository().getProducts();
        return ProductListPage.builder().products(products).build();
    }
}
