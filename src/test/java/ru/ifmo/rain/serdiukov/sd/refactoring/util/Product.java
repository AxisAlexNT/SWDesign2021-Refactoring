package ru.ifmo.rain.serdiukov.sd.refactoring.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@Getter
@ToString
public class Product {
    @NotNull
    private static final Comparator<Product> BY_PRICE_COMPARATOR = Comparator.comparingLong(Product::getPrice).thenComparing(Product::getName);
    @NotNull
    @NonNull
    private final String name;
    private final int price;

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

        if (name.matches("\\s+")) {
            throw new IllegalArgumentException("Product name must not contain whitespace");
        }
    }

    public static void validatePrice(final int price) {
        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
    }
}
