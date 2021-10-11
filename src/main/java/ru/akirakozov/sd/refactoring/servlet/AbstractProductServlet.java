package ru.akirakozov.sd.refactoring.servlet;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.db.repository.ProductRepository;
import ru.akirakozov.sd.refactoring.view.ResponsePage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * An abstract servlet that is specifically designed to withhold a reference on {@link ProductRepository} and takes servlet-aware response processing on itself.
 * Implementations must generate response pages using data from HTTP request and repository.
 */
public abstract class AbstractProductServlet extends HttpServlet {
    @Getter
    protected final @NotNull @NonNull ProductRepository productRepository;

    protected AbstractProductServlet(final @NotNull @NonNull ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    protected final void doGet(final @NotNull @NonNull HttpServletRequest request, final @NotNull @NonNull HttpServletResponse response) throws IOException {
        final ResponsePage responsePage = generateResponsePage(request);

        response.setContentType("text/html");
        response.getWriter().println(responsePage.getHTMLCode());
        response.setStatus(HttpServletResponse.SC_OK);
    }

    protected abstract @NotNull @NonNull ResponsePage generateResponsePage(final @NotNull @NonNull HttpServletRequest request) throws IOException;
}
