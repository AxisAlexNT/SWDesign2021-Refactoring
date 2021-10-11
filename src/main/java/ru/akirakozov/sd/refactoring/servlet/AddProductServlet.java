package ru.akirakozov.sd.refactoring.servlet;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.db.repository.ProductRepository;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.view.ResponsePage;
import ru.akirakozov.sd.refactoring.view.SimpleTextPage;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class AddProductServlet extends AbstractProductServlet {
    private static final @NotNull @NonNull String PRODUCT_NAME_PARAMETER_NAME = "name";
    private static final @NotNull @NonNull String PRODUCT_PRICE_PARAMETER_NAME = "price";
    private static final @NotNull @NonNull String CONFIRMATION_PAGE_TEXT = "OK";

    public AddProductServlet(final @NotNull @NonNull ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    protected @NotNull @NonNull ResponsePage generateResponsePage(final @NotNull @NonNull HttpServletRequest request) throws IOException {
        final @NotNull @NonNull String name = request.getParameter(PRODUCT_NAME_PARAMETER_NAME);
        final long longPrice = Long.parseLong(request.getParameter(PRODUCT_PRICE_PARAMETER_NAME));
        final int price = Math.toIntExact(longPrice);

        final Product newProduct = new Product(name, price);
        this.getProductRepository().addProduct(newProduct);

        return SimpleTextPage.builder().pageText(CONFIRMATION_PAGE_TEXT).build();
    }
}
