package ru.ifmo.rain.serdiukov.sd.refactoring.util;

import ru.akirakozov.sd.refactoring.Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingletonServerStarter {
    private static final ExecutorService exService = Executors.newSingleThreadExecutor();
    private static final AppHTTPClient appClient = new AppHTTPClient("http://localhost:8081");

    static {
        System.out.println("Starting up server");
        exService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Server thread started");
                System.out.flush();
                try {
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
            // Take a break to complete jetty-servlet bootstrap process (as it is a 'black-box' at the current stage):
            Thread.sleep(5000);
        } catch (final InterruptedException ignored) {
            // Ok
        }
    }


    public static AppHTTPClient getAppClient() {
        return appClient;
    }
}
