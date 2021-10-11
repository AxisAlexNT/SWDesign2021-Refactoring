package ru.akirakozov.sd.refactoring.domain;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.regex.Pattern;

@Getter
@ToString
@EqualsAndHashCode
public class Product {
    @NotNull
    private static final Comparator<Product> BY_PRICE_COMPARATOR = Comparator.comparingLong(Product::getPrice).thenComparing(Product::getName);
    @NotNull
    @NonNull
    private final String name;
    private final int price;


    /**
     * Product name should only contain latin letters, digits, dashes, underscores, periods and brackets.
     * It should also have a non-zero length.
     */
    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-_()\\[\\]{}.]+$");

    public Product(@NotNull @NonNull String name, int price) {
        validateName(name);
        validatePrice(price);
        this.name = name;
        this.price = price;
    }

    public static void validateName(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (!PRODUCT_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Product name must not contain whitespace");
        }
    }

    public static void validatePrice(final int price) {
        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
    }

    public static @NotNull Comparator<Product> getByPriceComparator(){
        return BY_PRICE_COMPARATOR;
    }
}
