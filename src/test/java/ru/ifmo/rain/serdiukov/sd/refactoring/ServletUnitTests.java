package ru.ifmo.rain.serdiukov.sd.refactoring;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.akirakozov.sd.refactoring.Main;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.APIRequestException;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.AppHTTPClient;
import ru.ifmo.rain.serdiukov.sd.refactoring.util.Product;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.fail;

public class ServletUnitTests {
    private AppHTTPClient appClient;
    private ExecutorService exService;

    @Before
    public void startServerAndAttachClient() throws Exception {
        exService = Executors.newSingleThreadExecutor();
        exService.execute(new Runnable() { // or use submit to get a Future (a result of computation, you'll need a Callable, rather than runnable then)
            @Override
            public void run() {
                try {
                    Main.main(new String[0]);
                } catch (final Exception e) {
                    throw new RuntimeException("Server thread has encountered an exception", e);
                }
            }
        });
        exService.shutdown();
        appClient = new AppHTTPClient("http://localhost:8081");
    }

    @Test
    public void someTest() {
        try {
            final List<Product> products = appClient.getProducts();
            products.size();
        } catch (APIRequestException e) {
            fail("No exception was expected");
        }
    }

    @After
    public void closeServer() {
        exService.shutdownNow();
    }
}
