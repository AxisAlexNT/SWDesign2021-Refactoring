package ru.akirakozov.sd.refactoring.db.repository;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.db.DBConnectionProvider;
import ru.akirakozov.sd.refactoring.domain.Product;
import ru.akirakozov.sd.refactoring.ex.DBLayerException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
    private final @NotNull @NonNull DBConnectionProvider dbConnectionProvider;

    public ProductRepository(@NotNull @NonNull DBConnectionProvider dbConnectionProvider) {
        this.dbConnectionProvider = dbConnectionProvider;
    }

    public List<Product> getProducts() {
        try (final Statement stmt = dbConnectionProvider.getConnection().createStatement()) {
            try (final ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCT")) {
                final List<Product> result = new ArrayList<>();
                while (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    final Product product = new Product(name, price);
                    result.add(product);
                }
                return result;
            }
        } catch (final SQLException sqlException) {
            throw new DBLayerException("Cannot execute get-products statement", sqlException);
        }
    }

    public void addProduct(final Product product) {
        final String sql = "INSERT INTO PRODUCT " +
                "(NAME, PRICE) VALUES (\"" + product.getName() + "\"," + product.getPrice() + ")";
        try (final Statement stmt = dbConnectionProvider.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
        } catch (final SQLException sqlException) {
            throw new DBLayerException("Cannot execute add-product statement", sqlException);
        }
    }

    public Optional<Product> getMaxPricedProduct() {
        try (final Statement stmt = dbConnectionProvider.getConnection().createStatement()) {
            try (final ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1")) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    final Product product = new Product(name, price);
                    return Optional.of(product);
                }
                return Optional.empty();
            }
        } catch (final SQLException sqlException) {
            throw new DBLayerException("Cannot execute max-priced product", sqlException);
        }
    }

    public Optional<Product> getMinPricedProduct() {
        try (final Statement stmt = dbConnectionProvider.getConnection().createStatement()) {
            try (final ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1")) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    final Product product = new Product(name, price);
                    return Optional.of(product);
                }
                return Optional.empty();
            }
        } catch (final SQLException sqlException) {
            throw new DBLayerException("Cannot execute min-priced product", sqlException);
        }
    }

    public int getProductCount() {
        try (final Statement stmt = dbConnectionProvider.getConnection().createStatement()) {
            try (final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM PRODUCT")) {

                if (rs.next()) {
                    return (rs.getInt(1));
                }
                throw new IllegalStateException("There is no product count in DB response");
            }
        } catch (final SQLException sqlException) {
            throw new DBLayerException("Cannot execute count query", sqlException);
        }
    }

    public int getProductsPriceSum() {
        try (final Statement stmt = dbConnectionProvider.getConnection().createStatement()) {
            try (final ResultSet rs = stmt.executeQuery("SELECT SUM(price) FROM PRODUCT")) {

                if (rs.next()) {
                    return (rs.getInt(1));
                }
                throw new IllegalStateException("There is no sum of product prices in DB response");
            }
        } catch (final SQLException sqlException) {
            throw new DBLayerException("Cannot execute sum query", sqlException);
        }
    }


}
