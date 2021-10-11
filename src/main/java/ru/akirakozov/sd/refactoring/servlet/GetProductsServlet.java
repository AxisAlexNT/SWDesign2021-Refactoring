package ru.akirakozov.sd.refactoring.servlet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.db.repository.ProductRepository;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.view.ProductListPage;
import ru.akirakozov.sd.refactoring.view.ResponsePage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author akirakozov
 */
@RequiredArgsConstructor
public class GetProductsServlet extends HttpServlet {
    private final @NotNull @NonNull ProductRepository productRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final List<Product> products = productRepository.getProducts();
        final ResponsePage responsePage = ProductListPage.builder().products(products).build();

        response.getWriter().println(responsePage.getHTMLCode());

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
