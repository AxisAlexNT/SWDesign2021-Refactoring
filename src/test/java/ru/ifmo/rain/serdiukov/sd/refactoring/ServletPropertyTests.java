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

import java.util.*;
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
    @Property(trials = 64)
    public synchronized void testProductsAreAddedAndSaved(final @NotNull @Size(min = 1, max = 16) Map<Long, Integer> preTestProductRecords) {
        final Map<String, Integer> testProductRecords = preTestProductRecords.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
        try {
            final List<Product> existingProducts = appClient.getProducts();
            assertThat("Product list should not be null", existingProducts, is(not(equalTo(null))));
            final Map<String, Set<Integer>> existingProductRecords = new HashMap<>();
            for (final Product existingProduct : existingProducts){
                existingProductRecords.computeIfAbsent(existingProduct.getName(), s -> new HashSet<>());
                existingProductRecords.get(existingProduct.getName()).add(existingProduct.getPrice());
            }

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
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            final List<Product> newProducts = newProductRecords.entrySet().stream().
                    map(e -> new Product(e.getKey(), e.getValue())).
                    collect(Collectors.toList());

            appClient.addProducts(newProducts);

            final List<Product> modifiedProducts = appClient.getProducts();
            assertThat("Product list should not be null", modifiedProducts, is(not(equalTo(null))));
            final Map<String, Set<Integer>> modifiedProductRecords = new HashMap<>();
            for (final Product modifiedProduct : modifiedProducts) {
                modifiedProductRecords.computeIfAbsent(modifiedProduct.getName(), s -> new HashSet<>());
                modifiedProductRecords.get(modifiedProduct.getName()).add(modifiedProduct.getPrice());
            }

            for (final Product existingProduct : existingProducts) {
                assertThat("Old product name should still remain in place",
                        modifiedProductRecords.containsKey(existingProduct.getName()),
                        is(equalTo(true)));
                assertThat("Old product with given name and price should still remain in place",
                        modifiedProductRecords.get(existingProduct.getName()).contains(existingProduct.getPrice()),
                        is(equalTo(true)));
            }

            for (final Product newProduct : newProducts) {
                assertThat("New product name should have been added",
                        modifiedProductRecords.containsKey(newProduct.getName()),
                        is(equalTo(true)));
                assertThat("New product with its name and price should have been added",
                        modifiedProductRecords.get(newProduct.getName()).contains(newProduct.getPrice()),
                        is(equalTo(true)));
            }

        } catch (final APIRequestException e) {
            fail("No exception was expected");
        }
    }

    @Theory
    @Property(trials = 64)
    public synchronized void testMaximumPricedProductQueryReturnsProductWithMaximalPrice(){

    }

}
