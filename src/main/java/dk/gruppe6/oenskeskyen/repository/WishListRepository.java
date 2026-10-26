package dk.gruppe6.oenskeskyen.repository;

import dk.gruppe6.oenskeskyen.model.Wish;
import dk.gruppe6.oenskeskyen.model.WishList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WishListRepository {

    @Autowired
    private DataSource dataSource;

    public List<WishList> getAllWishLists() {
        List<WishList> wishlists = new ArrayList<>();
        String sql = "SELECT * FROM wishlist";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                WishList wishlist = new WishList(
                        resultSet.getInt("id"),
                        resultSet.getString("title")
                );
                wishlists.add(wishlist);
            }

        } catch (SQLException e) {
            throw new DataAccessResourceFailureException(
                    "Kunne ikke hente ønskesedler fra databasen.", e);
        }

        return wishlists;
    }

    public WishList getWishlistById(int id) {
        String sql = "SELECT * FROM wishlist WHERE id = ?";

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

    public List<Wish> getWishesByWishListID(int wishListID) {
        List<Wish> wishesByWishListId = new ArrayList<>();
        String sql = "SELECT * FROM wish WHERE wishlist_id = ? ORDER BY id";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, wishListID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Wish wish = new Wish(
                            resultSet.getInt("id"),
                            resultSet.getInt("wishlist_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getInt("quantity"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getString("product_url"),
                            resultSet.getString("image_url")
                    );
                    wishesByWishListId.add(wish);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessResourceFailureException(
                    "Kunne ikke hente ønskerne fra databasen.", e);
        }

        return wishesByWishListId;
    }

    public int createWishList(WishList wishList) {
        String sql = "INSERT INTO wishlist (title) VALUES(?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, wishList.getTitle());
            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new DataAccessResourceFailureException(
                        "Kunne ikke oprette ønskesedlen i databasen.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            return -1;

        } catch (SQLException e) {
            throw new DataAccessResourceFailureException(
                    "Kunne ikke oprette ønskesedlen i databasen.", e);
        }
    }
    public void deleteWishlist(int id) {
        String sql = "DELETE FROM wishlist WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessResourceFailureException(
                    "Kunne ikke slette ønskesedlen fra databasen.", e);
        }
    }

    public void updateWishList(WishList updatedWishList) {
        String sql = "UPDATE wishlist SET title = ? WHERE id = ?";

        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, updatedWishList.getTitle());
            statement.setInt(2, updatedWishList.getId());

            statement.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessResourceFailureException("Kunne ikke redigere i ønskesedlen", e);
        }
    }
}