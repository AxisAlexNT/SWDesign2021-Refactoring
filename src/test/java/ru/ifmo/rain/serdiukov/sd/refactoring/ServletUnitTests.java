package ru.ifmo.rain.serdiukov.sd.refactoring;

import org.junit.Test;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.APIRequestException;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.AppHTTPClient;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.SingletonServerStarter;

import java.util.List;

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
        } catch (APIRequestException e) {
            fail("No exception was expected");
        }
    }
}
