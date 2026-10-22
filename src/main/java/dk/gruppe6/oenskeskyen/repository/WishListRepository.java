package dk.gruppe6.oenskeskyen.repository;

import dk.gruppe6.oenskeskyen.model.WishList;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class WishListRepository {

    private final DataSource dataSource;

    public WishListRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<WishList> getWishlistById(int id) {
        String sql = "SELECT id, title FROM wishlist WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new WishList(
                            resultSet.getInt("id"),
                            resultSet.getString("title")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException(
                    "Kunne ikke hente ønskesedlen fra databasen.", e);
        }
    }
}
