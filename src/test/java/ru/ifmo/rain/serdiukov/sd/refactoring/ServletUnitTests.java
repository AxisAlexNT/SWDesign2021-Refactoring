package ru.ifmo.rain.serdiukov.sd.refactoring;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.akirakozov.sd.refactoring.Main;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.APIRequestException;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.AppHTTPClient;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.Product;

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
    private static final ExecutorService exService;
    private static AppHTTPClient appClient;

    static {
        System.out.println("static() called in unit tests");
        System.out.flush();
        exService = Executors.newSingleThreadExecutor();
        exService.submit(new Callable<>() { // or use submit to get a Future (a result of computation, you'll need a Callable, rather than runnable then)
            @Override
            public Object call() {
                try {
                    Main.main(new String[0]);
                } catch (final InterruptedException ignored) {
                    // That's ok, we are shutting down server after tests
                } catch (final Exception e) {
                    throw new RuntimeException("Server thread has encountered an exception", e);
                }
                return null;
            }
        });

        exService.shutdown();
        System.out.println("static() exited in unit tests");
        System.out.flush();
    }

    //@Test
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

    @After
    public void closeServer() {
        System.out.println("After called in unit tests");
        exService.shutdownNow();
        try {
            exService.awaitTermination(0, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            // Ok
        }
        System.out.println("After exited in unit tests");
        exService.shutdownNow();
    }
}
