package ru.ifmo.rain.serdiukov.sd.refactoring.util;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@Builder
@RequiredArgsConstructor
@Getter
@Setter
public class Product {
    @NotNull
    private static final Comparator<Product> BY_PRICE_COMPARATOR = Comparator.comparingLong(Product::getPrice).thenComparing(Product::getName);
    @NotNull @NonNull
    private final String name;
    private final long price;
}
