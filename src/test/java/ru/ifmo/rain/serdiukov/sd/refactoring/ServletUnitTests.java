package ru.ifmo.rain.serdiukov.sd.refactoring;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.akirakozov.sd.refactoring.Main;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.APIRequestException;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.AppHTTPClient;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.Product;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.SingletonServerStarter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class ServletUnitTests {
    private final AppHTTPClient appClient;

    public ServletUnitTests() {
        appClient = SingletonServerStarter.getAppClient();
    }

    @Test
    public synchronized void productFetchTest() {
        try {
            final List<Product> products = appClient.getProducts();
            assertThat("Product list should not be null", products, is(not(equalTo(null))));
            final Map<String, Integer> existingProducts = products.parallelStream().collect(Collectors.toMap(Product::getName, Product::getPrice));
            assertThat("All stored products should have distinct names", existingProducts.size(), is(equalTo(products.size())));
        } catch (APIRequestException e) {
            fail("No exception was expected");
        }
    }
}
