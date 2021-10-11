package ru.ifmo.rain.serdiukov.sd.refactoring;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.APIRequestException;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.AppHTTPClient;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.SingletonServerStarter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
            for (final Product existingProduct : existingProducts) {
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
    @Property(trials = 250)
    public synchronized void testMaximumPricedProductQueryReturnsProductWithMaximalPrice() {
        try {
            final List<Product> existingProducts = appClient.getProducts();
            final Optional<Product> queriedMostExpensiveProduct = appClient.getMostExpensiveProduct();

            assertThat("Product list should not be null", existingProducts, is(not(equalTo(null))));
            final NavigableSet<Product> existingProductsSet = new TreeSet<>(Product.getByPriceComparator());
            existingProductsSet.addAll(existingProducts);
            final Map<Integer, Set<Product>> productByPrice = new HashMap<>();
            for (final Product existingProduct : existingProducts) {
                productByPrice.computeIfAbsent(existingProduct.getPrice(), s -> new HashSet<>());
                productByPrice.get(existingProduct.getPrice()).add(existingProduct);
            }

            if (existingProductsSet.isEmpty()) {
                assertThat("No product with maximal price was expected in empty DB",
                        queriedMostExpensiveProduct.isEmpty(),
                        is(equalTo(true))
                );
            } else {
                assertThat("There should be a product with maximal price in non-empty DB",
                        queriedMostExpensiveProduct.isPresent(),
                        is(equalTo(true))
                );
                final int maximalProductPrice = existingProductsSet.last().getPrice();
                final Set<Product> productsWithMaximalPrice = productByPrice.get(maximalProductPrice);
                final Product queriedMaximalProduct = queriedMostExpensiveProduct.get();

                assertThat("Maximal product price cannot be less than a maximum of all products' prices", queriedMaximalProduct.getPrice(), is(not(lessThan(maximalProductPrice))));
                assertThat("Maximal product price cannot be greater than a maximum of all products' prices", queriedMaximalProduct.getPrice(), is(not(greaterThan(maximalProductPrice))));
                assertThat("Maximal product price should be equal to the maximum of all products' prices", queriedMaximalProduct.getPrice(), is(equalTo(maximalProductPrice)));
                assertThat("Maximal product is one of the products with maximal prices", productsWithMaximalPrice.contains(queriedMaximalProduct), is(equalTo(true)));
            }
        } catch (final APIRequestException e) {
            fail("No exception was expected");
        }
    }


    @Theory
    @Property(trials = 250)
    public synchronized void testMinimumPricedProductQueryReturnsProductWithMinimalPrice() {
        try {
            final List<Product> existingProducts = appClient.getProducts();
            final Optional<Product> queriedMostExpensiveProduct = appClient.getCheapestProduct();

            assertThat("Product list should not be null", existingProducts, is(not(equalTo(null))));
            final NavigableSet<Product> existingProductsSet = new TreeSet<>(Product.getByPriceComparator());
            existingProductsSet.addAll(existingProducts);
            final Map<Integer, Set<Product>> productByPrice = new HashMap<>();
            for (final Product existingProduct : existingProducts) {
                productByPrice.computeIfAbsent(existingProduct.getPrice(), s -> new HashSet<>());
                productByPrice.get(existingProduct.getPrice()).add(existingProduct);
            }

            if (existingProductsSet.isEmpty()) {
                assertThat("No product with minimal price was expected in empty DB",
                        queriedMostExpensiveProduct.isEmpty(),
                        is(equalTo(true))
                );
            } else {
                assertThat("There should be a product with minimal price in non-empty DB",
                        queriedMostExpensiveProduct.isPresent(),
                        is(equalTo(true))
                );
                final int minimalProductPrice = existingProductsSet.first().getPrice();
                final Set<Product> productsWithMinimalPrice = productByPrice.get(minimalProductPrice);
                final Product queriedMinimalProduct = queriedMostExpensiveProduct.get();

                assertThat("Minimal product price cannot be less than a minimum of all products' prices", queriedMinimalProduct.getPrice(), is(not(lessThan(minimalProductPrice))));
                assertThat("Minimal product price cannot be greater than a minimum of all products' prices", queriedMinimalProduct.getPrice(), is(not(greaterThan(minimalProductPrice))));
                assertThat("Minimal product price should be equal to the minimum of all products' prices", queriedMinimalProduct.getPrice(), is(equalTo(minimalProductPrice)));
                assertThat("Minimal product is one of the products with minimal prices", productsWithMinimalPrice.contains(queriedMinimalProduct), is(equalTo(true)));
            }
        } catch (final APIRequestException e) {
            fail("No exception was expected");
        }
    }

    @Theory
    @Property(trials = 250)
    public synchronized void testSumQuery() {
        try {
            final List<Product> existingProducts = appClient.getProducts();
            final int queriedTotalPrice = appClient.getTotalPrice();

            assertThat("Product list should not be null", existingProducts, is(not(equalTo(null))));
            final AtomicInteger totalPrice = new AtomicInteger();
            existingProducts.parallelStream().forEach(p -> {
                final int price = p.getPrice();
                totalPrice.addAndGet(price);
            });

            assertThat("Total price should be equal to the sum of prices of each product", queriedTotalPrice, is(equalTo(totalPrice.get())));
        } catch (final APIRequestException e) {
            fail("No exception was expected");
        }
    }

    @Theory
    @Property(trials = 250)
    public synchronized void testCountQuery() {
        try {
            final List<Product> existingProducts = appClient.getProducts();
            final int queriedCount = appClient.getProductCount();
            assertThat("Product list should not be null", existingProducts, is(not(equalTo(null))));

            assertThat("Total product count should be equal to the length of the product list", queriedCount, is(equalTo(existingProducts.size())));
        } catch (final APIRequestException e) {
            fail("No exception was expected");
        }
    }

}
