package ru.akirakozov.sd.refactoring.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Describes a Product entity that's stored in this application.
 */
@Getter
@ToString
@EqualsAndHashCode
public class Product {
    @NotNull
    private static final Comparator<Product> BY_PRICE_COMPARATOR = Comparator.comparingLong(Product::getPrice).thenComparing(Product::getName);
    /**
     * Product name should only contain latin letters, digits, dashes, underscores, periods and brackets.
     * It should also have a non-zero length.
     */
    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-_()\\[\\]{}.]+$");
    @NotNull
    @NonNull
    private final String name;
    private final int price;

    /**
     * Constructs a new Product with given name and price. Arguments are validated prior to construction.
     *
     * @param name  A string, representing name of this product. Should only contain latin letters, digits, dashes, underscores, periods and brackets. It should also have a non-zero length.
     * @param price A price of this product, must be a non-negative integer.
     * @throws IllegalArgumentException in case arguments were not validated successfully.
     */
    public Product(final @NotNull @NonNull String name, final int price) throws IllegalArgumentException {
        validateName(name);
        validatePrice(price);
        this.name = name;
        this.price = price;
    }

    /**
     * Takes a {@link String} and checks whether it could be used as a name of {@link Product}.
     * This method guarantees that if it hasn't risen an {@link IllegalArgumentException}, this parameter could be used in {@link Product}'s constructor.
     *
     * @param name A {@link String} to be validated.
     * @throws IllegalArgumentException In case given string failed validation.
     */
    public static void validateName(final String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (!PRODUCT_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Product name must not contain whitespace");
        }
    }

    /**
     * Takes an {@code int} and checks whether it could be used as a price of {@link Product}.
     * This method guarantees that if it hasn't risen an {@link IllegalArgumentException}, this parameter could be used in {@link Product}'s constructor.
     *
     * @param price An {@code int} to be validated.
     * @throws IllegalArgumentException In case given string failed validation.
     */
    public static void validatePrice(final int price) throws IllegalArgumentException {
        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
    }

    /**
     * Returns a comparator that defines the price-name ordering over {@link Product}s.
     *
     * @return A comparator that defines the price-name ordering over {@link Product}s.
     */
    public static @NotNull Comparator<Product> getByPriceComparator() {
        return BY_PRICE_COMPARATOR;
    }
}
