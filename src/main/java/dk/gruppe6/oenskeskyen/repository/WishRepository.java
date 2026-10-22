package dk.gruppe6.oenskeskyen.repository;

import dk.gruppe6.oenskeskyen.model.Wish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class WishRepository {

    @Autowired
    private DataSource dataSource;

    public void createWish(Wish wish) {
        String sql = "INSERT INTO wish (wishlist_id, name, description, " +
                "quantity, price, product_url, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = dataSource.getConnection();
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
}
