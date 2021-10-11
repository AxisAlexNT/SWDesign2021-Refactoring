package ru.akirakozov.sd.refactoring.db.repository;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import ru.akirakozov.sd.refactoring.db.DBConnectionProvider;
import ru.akirakozov.sd.refactoring.db.ex.DBLayerException;
import ru.akirakozov.sd.refactoring.domain.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Repository that takes management of {@link Product}s that are stored and processed in current applications.
 */
public class ProductRepository {
    private final @NotNull @NonNull DBConnectionProvider dbConnectionProvider;

    /**
     * Constructs new product repository using given connection provider.
     *
     * @param dbConnectionProvider A database connection provider to use.
     */
    public ProductRepository(final @NotNull @NonNull DBConnectionProvider dbConnectionProvider) {
        this.dbConnectionProvider = dbConnectionProvider;
    }

    /**
     * Returns all products that are stored in this application's database. Products are allowed to have duplicates.
     *
     * @return All products that are stored in this application's database.
     */
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

    /**
     * Adds a new product to application's database. Products are allowed to have duplicates.
     *
     * @param product A product to be added.
     */
    public void addProduct(final Product product) {
        final String sql = "INSERT INTO PRODUCT " +
                "(NAME, PRICE) VALUES (\"" + product.getName() + "\"," + product.getPrice() + ")";
        try (final Statement stmt = dbConnectionProvider.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
        } catch (final SQLException sqlException) {
            throw new DBLayerException("Cannot execute add-product statement", sqlException);
        }
    }

    /**
     * Returns some product with the maximal price alongside others.
     * If there are multiple products having exactly the same maximal price, there is no guarantee about which one would be returned.
     * If there are no products stored in this application, returns an empty optional.
     *
     * @return Some product with the maximal price.
     */
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

    /**
     * Returns some product with the minimal price alongside others.
     * If there are multiple products having exactly the same minimal price, there is no guarantee about which one would be returned.
     * If there are no products stored in this application, returns an empty optional.
     *
     * @return Some product with the minimal price.
     */
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

    /**
     * Returns a total product count in application's database (including possible duplicates).
     *
     * @return A total product count in application's database (including possible duplicates).
     */
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

    /**
     * Returns a sum of prices of all products that are stored in database of this application (including possible duplicates).
     * If there are no products in database, returns zero.
     *
     * @return A sum of prices of all products that are stored in database of this application (including possible duplicates), or zero, if no products are stored.
     */
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
