package dk.gruppe6.oenskeskyen.repository;

import dk.gruppe6.oenskeskyen.model.WishList;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class WishListRepository {

    private final DataSource dataSource;

    public WishListRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public WishList getWishlistById(int id) {
        String sql = "SELECT id, title FROM wishlist WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    WishList wishlist = new WishList();
                    wishlist.setId(resultSet.getInt("id"));
                    wishlist.setTitle(resultSet.getString("title"));

                    return wishlist;
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException(
                    "Kunne ikke hente ønskesedlen fra databasen.", e);
        }
    }
}