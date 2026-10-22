package dk.gruppe6.oenskeskyen;

import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WishListRepositoryTests {

    @Autowired
    private WishListRepository repository;

    @Autowired
    private DataSource dataSource;

    @Test
    void findsWishlistById() throws SQLException {
        // Direkte JDBC bruger egne forbindelser, så testen rydder selv op.
        try (Connection connection = dataSource.getConnection()) {
            int id;
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO wishlist (title) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, "Testliste");
                assertEquals(1, statement.executeUpdate());
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    assertTrue(keys.next());
                    id = keys.getInt(1);
                }
            }

            try {
                WishList result = repository.getWishlistById(id).orElseThrow();
                assertEquals(id, result.getId());
                assertEquals("Testliste", result.getTitle());
            } finally {
                // Slet kun den række, testen selv oprettede.
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM wishlist WHERE id = ?")) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }
            }
        }
    }

    @Test
    void returnsEmptyWhenWishlistDoesNotExist() {
        assertTrue(repository.getWishlistById(-1).isEmpty());
    }
}
