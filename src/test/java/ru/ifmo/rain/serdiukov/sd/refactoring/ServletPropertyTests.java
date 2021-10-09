package ru.ifmo.rain.serdiukov.sd.refactoring;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.APIRequestException;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.AppHTTPClient;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.Product;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.SingletonServerStarter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

@RunWith(JUnitQuickcheck.class)
public class ServletPropertyTests {
    private final AppHTTPClient appClient;

    public ServletPropertyTests() {
        appClient = SingletonServerStarter.getAppClient();
    }

    @Theory
    @Property(trials = 4)
    public synchronized void testProductsAreAddedAndSaved(final @NotNull @Size(min = 1, max = 16) Map<Long, Integer> preTestProductRecords) {
        System.out.println("Test called in property tests");
        System.out.flush();
        final Map<String, Integer> testProductRecords = preTestProductRecords.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
        try {
            final List<Product> existingProducts = appClient.getProducts();
            assertThat("Product list should not be null", existingProducts, is(not(equalTo(null))));
            final Map<String, Integer> existingProductRecords = existingProducts.stream().collect(Collectors.toMap(Product::getName, Product::getPrice,(v1, v2) -> v1));
            assertThat("All stored products should have distinct names", existingProductRecords.size(), is(equalTo(existingProducts.size())));

            final Map<String, Integer> newProductRecords = testProductRecords.entrySet().stream().
                    filter(e -> {
                        try {
                            Product.validateName(e.getKey());
                            return true;
                        } catch (final IllegalArgumentException ignored) {
                            return false;
                        }
                    }).filter(e -> {
                        try {
                            Product.validatePrice(e.getValue());
                            return true;
                        } catch (final IllegalArgumentException ignored) {
                            return false;
                        }
                    }).
                    filter(e -> !existingProductRecords.containsKey(e.getKey())).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            final List<Product> newProducts = newProductRecords.entrySet().stream().
                    map(e -> new Product(e.getKey(), e.getValue())).
                    collect(Collectors.toList());

            appClient.addProducts(newProducts);

            final List<Product> modifiedProducts = appClient.getProducts();
            assertThat("Product list should not be null", modifiedProducts, is(not(equalTo(null))));
            final Map<String, Integer> modifiedProductRecords = modifiedProducts.stream().collect(Collectors.toMap(Product::getName, Product::getPrice));
            assertThat("All stored products should have distinct names", modifiedProductRecords.size(), is(equalTo(modifiedProducts.size())));
            final Set<Product> modifiedProductsSet = new HashSet<>(modifiedProducts);

            for (final Product existingProduct : existingProducts) {
                assertThat("Old products should still remain in place", modifiedProductsSet.contains(existingProduct), is(equalTo(true)));
            }

            for (final Product newProduct : newProducts) {
                assertThat("New products should have been added", modifiedProductsSet.contains(newProduct), is(equalTo(true)));
            }

        } catch (final APIRequestException e) {
            throw new RuntimeException(e);
            //fail("No exception was expected");
        }
        System.out.println("Test exited in property tests");
        System.out.flush();
    }
}
