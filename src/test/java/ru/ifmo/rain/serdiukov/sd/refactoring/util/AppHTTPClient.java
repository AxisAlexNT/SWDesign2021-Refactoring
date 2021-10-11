package ru.ifmo.rain.serdiukov.sd.refactoring.util;

import lombok.NonNull;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.domain.Product;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

        return getProductsFromResponseLines(
                Arrays.stream(lines).
                        skip(1).
                        limit(lineCount - 2).toList()
        );
    }


    public void addProduct(final @NotNull @NonNull Product product) throws APIRequestException {
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

    private List<Product> getTitledProductListQuery(final @NotNull @NonNull String command) throws APIRequestException {
        final String servletUrl = String.format("%s/query?command=%s", serverUrl, command);

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
            throw new APIRequestException("HTTP Status is not 200 OK after requesting " + command);
        }

        final String[] lines = responseHTML.split("\r\n");
        final int lineCount = lines.length;

        return getProductsFromResponseLines(
                Arrays.stream(lines).
                        skip(2).
                        limit(lineCount - 3).
                        toList());
    }


    public Optional<Product> getMostExpensiveProduct() throws APIRequestException {
        final List<Product> expensiveProducts = getTitledProductListQuery("max");
        assert (expensiveProducts.size() <= 1) : "Max query should return no more than one product";
        if (expensiveProducts.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(expensiveProducts.get(0));
        }
    }

    public Optional<Product> getCheapestProduct() throws APIRequestException {
        final List<Product> cheapestProducts = getTitledProductListQuery("min");
        assert (cheapestProducts.size() <= 1) : "Min query should return no more than one product";
        if (cheapestProducts.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(cheapestProducts.get(0));
        }
    }

    private int getNumericQuery(final @NotNull @NonNull String command) throws APIRequestException {
        final String servletUrl = String.format("%s/query?command=%s", serverUrl, command);

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
            throw new APIRequestException("HTTP Status is not 200 OK after requesting " + command);
        }

        final String[] lines = responseHTML.split("\r\n");
        final int lineCount = lines.length;

        final String responseLine = lines[2];

        try {
            return Integer.parseInt(responseLine);
        } catch (final NumberFormatException nfe) {
            throw new APIRequestException("Inconsistent API response: number cannot be parsed", nfe);
        }
    }

    public int getProductCount() throws APIRequestException {
        return getNumericQuery("count");
    }

    public int getTotalPrice() throws APIRequestException {
        return getNumericQuery("sum");
    }

    private List<Product> getProductsFromResponseLines(final @NotNull @NonNull List<String> lines) throws APIRequestException {
        final List<Product> products = new ArrayList<>();

        for (final String line : lines) {
            final String[] parts = line.replaceAll("</br>", "").split("\\s");
            if (parts.length != 2) {
                throw new APIRequestException("Malformed product record: contains more than two parts");
            }
            final String name = parts[0];
            final String priceString = parts[1];
            final int price;
            try {
                price = Integer.parseInt(priceString);
            } catch (final NumberFormatException e) {
                throw new APIRequestException("Malformed product record: price cannot be parsed", e);
            }
            products.add(new Product(name, price));
        }

        return products;
    }
}
