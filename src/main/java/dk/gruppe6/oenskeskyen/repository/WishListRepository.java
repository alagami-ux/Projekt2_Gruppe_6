package dk.gruppe6.oenskeskyen.repository;

import dk.gruppe6.oenskeskyen.model.WishList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class WishListRepository {

    @Autowired
    DataSource dataSource;

    public WishList getWishlistById(int id) {
        WishList wishlistByID = new WishList();

        String sql = "SELECT * FROM wishlist WHERE id = ?";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    wishlistByID.setTitle(resultSet.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return wishlistByID;
    }

}