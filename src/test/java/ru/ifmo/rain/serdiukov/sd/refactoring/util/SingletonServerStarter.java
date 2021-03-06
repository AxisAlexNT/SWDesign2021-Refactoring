package ru.ifmo.rain.serdiukov.sd.refactoring.util;

import ru.akirakozov.sd.refactoring.Main;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("Convert2Lambda")
public class SingletonServerStarter {
    private static final ExecutorService exService = Executors.newSingleThreadExecutor();
    private static final String TEST_SERVER_URL = "http://localhost:8081";
    private static final AppHTTPClient appClient = new AppHTTPClient(TEST_SERVER_URL);

    static {
        System.out.println("Starting up server");
        final CountDownLatch serverThreadReadiness = new CountDownLatch(1);
        exService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Server thread started");
                System.out.flush();
                try {
                    serverThreadReadiness.countDown();
                    Main.main(new String[0]);
                } catch (final InterruptedException ignored) {
                    // That's ok, we are shutting down server after tests
                } catch (final Exception e) {
                    e.printStackTrace();
                    System.out.flush();
                    throw new RuntimeException("Server thread has encountered an exception", e);
                }
            }
        });
        try {
            System.out.println("Waiting for server thread to start");
            // Wait for the server thread to start:
            serverThreadReadiness.await();
            // Take a break to complete jetty-servlet bootstrap process (as it is a 'black-box' at the current stage):
            Thread.sleep(5000);
        } catch (final InterruptedException ignored) {
            // Ok, we are shutting down
        }
        System.out.println("Singleton server starter initialization completed");
    }

    public static AppHTTPClient getAppClient() {
        return appClient;
    }
}
