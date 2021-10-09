package ru.ifmo.rain.serdiukov.sd.refactoring.util;

import lombok.NonNull;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class AppHTTPClient implements AutoCloseable {
    private final HttpClient httpClient;
    private final @NotNull String serverUrl;

    public AppHTTPClient(final @NonNull String serverUrl) {
        this.serverUrl = serverUrl;
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

    public List<Product> getProducts() throws APIRequestException {
        final String servletUrl = String.format("%s/get-products", serverUrl);
        final String responseHTML;
        try {
            final ContentResponse response = httpClient.newRequest(servletUrl)
                    .timeout(5, TimeUnit.SECONDS)
                    .send();
            responseHTML = response.getContentAsString();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new APIRequestException("Cannot get products due to the network communication issues", e);
        }

        final String[] lines = responseHTML.split("\r\n");
        final int lineCount = lines.length;
        final List<Optional<Product>> optionalProducts = Arrays.stream(lines).skip(1).limit(lineCount - 2).map(s -> {
            // Let's help Java infer types:
            final Optional<Product> result;
            final String[] parts = s.split("\\s");
            if (parts.length != 2) {
                result = Optional.empty();
                return result;
            }
            final String name = parts[0];
            final String priceString = parts[1].replaceAll("</br>", "");
            final long price;
            try {
                price = Long.parseLong(priceString);
            } catch (final NumberFormatException ignored) {
                result = Optional.empty();
                return result;
            }
            result = Optional.of(new Product(name, price));
            return result;
        }).collect(Collectors.toList());

        final List<Product> products = new ArrayList<>();
        for (final Optional<Product> o : optionalProducts) {
            if (!o.isPresent()) {
                throw new APIRequestException("Malformed product record presents in server response");
            }
            products.add(o.get());
        }

        return products;
    }
}
