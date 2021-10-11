package ru.akirakozov.sd.refactoring.view;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.domain.Product;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public class ProductListPage extends HTMLBodyPage {
    private static final @NotNull String listItemsDelimiter = String.format("</br>%n");
    private final @NotNull List<Product> products;

    @Override
    public @NotNull String getBodyHTMLCode() {
        return products.parallelStream().
                map(p ->
                        String.format("%s\t%d", p.getName(), p.getPrice())
                ).collect(Collectors.joining(listItemsDelimiter));
    }
}
