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
 * @author akirakozov
 */
public class GetProductsServlet extends AbstractProductServlet {
    public GetProductsServlet(final @NotNull @NonNull ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    protected @NotNull @NonNull ResponsePage generateResponsePage(final @NotNull @NonNull HttpServletRequest request) {
        final List<Product> products = this.getProductRepository().getProducts();
        return ProductListPage.builder().products(products).build();
    }
}
