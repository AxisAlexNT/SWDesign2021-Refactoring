package ru.ifmo.rain.serdiukov.sd.refactoring.util;

import lombok.NonNull;
import org.eclipse.jetty.client.HttpClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class AppHTTPClient implements AutoCloseable {
    private final HttpClient httpClient;

    public AppHTTPClient() {
        httpClient = new HttpClient();
        try {
            httpClient.start();
        } catch (final Exception e) {
            throw new RuntimeException("Error during httpClient start", e);
        }
    }


    @Override
    public void close() throws Exception {
        httpClient.stop();
    }

    final List<Product> getProducts(final @NonNull String server_url) throws APIRequestException {
        try {
            httpClient.GET(String.format("%s/get-products", server_url));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new APIRequestException("Cannot get products", e);
        }
        throw new UnsupportedOperationException("TODO: Not yet implemented");
    }
}
