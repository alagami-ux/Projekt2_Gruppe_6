package dk.gruppe6.oenskeskyen.repository;

import dk.gruppe6.oenskeskyen.model.Wish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class WishRepository {

    @Autowired
    private DataSource dataSource;

    public void createWish(Wish wish) {
        String sql = "INSERT INTO wish (wishlist_id, name, description, " +
                "quantity, price, product_url, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, wish.getWishlistId());
            statement.setString(2, wish.getName());
            statement.setString(3, wish.getDescription());
            statement.setInt(4, wish.getQuantity());
            statement.setBigDecimal(5, wish.getPrice());
            statement.setString(6, wish.getProductUrl());
            statement.setString(7, wish.getImageUrl());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessResourceFailureException(
                    "Kunne ikke oprette ønsket i databasen.", e);
        }
    }

    public void deleteWish(int id, int wishlistId) {
        String sql = "DELETE FROM wish WHERE id = ? AND wishlist_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setInt(2, wishlistId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessResourceFailureException(
                    "Kunne ikke slette ønsket fra databasen.", e);
        }
    }

    public Wish getWishById(int id) {
        Wish wish = new Wish();
        String sql = "SELECT * FROM WHERE id = ?";

        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try(ResultSet resultSet = statement.executeQuery()) {
                wish.setId(resultSet.getInt("id"));
                wish.setId(resultSet.getInt("wishlist_id"));
                wish.setWishlistId(resultSet.getInt("wishlist_id"));
                wish.setName(resultSet.getString("name"));
                wish.setDescription(resultSet.getString("desciption"));
                wish.setQuantity(resultSet.getInt("quantity"));
                wish.setPrice(resultSet.getBigDecimal("price"));
                wish.setProductUrl(resultSet.getString("product_url"));
                wish.setImageUrl(resultSet.getString("image_url"));

            }
        }catch (SQLException e) {
            throw new DataAccessResourceFailureException("Kunne ikke finde det pågældende ønske", e);
        }

        return wish;
    }

    public void editWish(Wish updatedWish) {
        String sql = "UPDATE wish SET name = ?, description = ?, quantity = ?, price = ?" +
                "product_url = ?, image_url = ? WHERE id = ?";

        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, updatedWish.getName());
            statement.setString(2, updatedWish.getDescription());
            statement.setInt(3, updatedWish.getQuantity());
            statement.setBigDecimal(4, updatedWish.getPrice());
            statement.setString(5, updatedWish.getProductUrl());
            statement.setString(6, updatedWish.getImageUrl());
            statement.setInt(7, updatedWish.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("Kunne ikke redigere i ønsket.", e);
        }
    }
}