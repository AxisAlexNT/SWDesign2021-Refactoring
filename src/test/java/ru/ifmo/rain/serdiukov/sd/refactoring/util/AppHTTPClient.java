package ru.ifmo.rain.serdiukov.sd.refactoring.util;

import lombok.NonNull;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
            final int price;
            try {
                price = Integer.parseInt(priceString);
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


    public void addProduct(final @NotNull @NonNull Product product) throws APIRequestException {
        if (product.getPrice() < 0){
            System.out.println("????"); System.out.flush();
            System.out.flush();
        }

        final String servletUrl = String.format("%s/add-product?name=%s&price=%d", serverUrl, product.getName(), product.getPrice());
        final String responseHTML;
        final int responseStatus;
        try {
            final ContentResponse response = httpClient.newRequest(servletUrl)
                    .timeout(5, TimeUnit.SECONDS)
                    .send();
            responseHTML = response.getContentAsString();
            responseStatus = response.getStatus();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new APIRequestException("Cannot add product due to the network communication issues", e);
        }

        if (responseStatus != 200) {
            throw new APIRequestException("HTTP Status is not 200 OK after requesting add-product");
        }

        if (!responseHTML.trim().equalsIgnoreCase("OK")) {
            throw new APIRequestException("Malformed response for adding product");
        }
    }

    public void addProducts(final @NotNull @NonNull Collection<Product> products) throws APIRequestException {
        for (final Product product : products) {
            addProduct(product);
        }
    }

}
