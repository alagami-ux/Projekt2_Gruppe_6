package dk.gruppe6.oenskeskyen;

import dk.gruppe6.oenskeskyen.model.Wish;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WishListWishesTests {

    @Autowired
    private WishListRepository repository;

    @Autowired
    private DataSource dataSource;

    @Test
    void retrievesOnlySelectedListsWishesWithAllFieldsAndNullablePrices()
            throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            int selectedId = createList(connection);
            try {
                int otherId = createList(connection);
                try {
                    insertWish(connection, selectedId, "Bog", new BigDecimal("199.95"));
                    insertWish(connection, otherId, "Anden listes ønske", new BigDecimal("12.50"));
                    insertWish(connection, selectedId, "Uden pris", null);
                    insertWish(connection, selectedId, "Gratis", BigDecimal.ZERO);

                    List<Wish> wishes = repository.getWishesByWishListID(selectedId);

                    assertEquals(3, wishes.size());
                    assertTrue(wishes.stream().allMatch(w -> w.getWishlistId() == selectedId));
                    assertTrue(wishes.get(0).getId() < wishes.get(1).getId());
                    assertTrue(wishes.get(1).getId() < wishes.get(2).getId());
                    Wish first = wishes.get(0);
                    assertEquals("Bog", first.getName());
                    assertEquals("Testbeskrivelse", first.getDescription());
                    assertEquals(Integer.valueOf(2), first.getQuantity());
                    assertEquals(new BigDecimal("199.95"), first.getPrice());
                    assertEquals("https://example.com/product", first.getProductUrl());
                    assertEquals("https://example.com/image.png", first.getImageUrl());
                    assertEquals("Uden pris", wishes.get(1).getName());
                    assertNull(wishes.get(1).getPrice());
                    assertEquals("Gratis", wishes.get(2).getName());
                    assertEquals(new BigDecimal("0.00"), wishes.get(2).getPrice());
                } finally {
                    deleteList(connection, otherId);
                }
            } finally {
                deleteList(connection, selectedId);
            }
        }
    }

    @Test
    void returnsEmptyListWhenNoWishesAreAttached() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            int id = createList(connection);
            try {
                assertNotNull(repository.getWishesByWishListID(id));
                assertTrue(repository.getWishesByWishListID(id).isEmpty());
            } finally {
                deleteList(connection, id);
            }
        }
    }

    private int createList(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO wishlist (title) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Repositorytest");
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                assertTrue(keys.next());
                return keys.getInt(1);
            }
        }
    }

    private void insertWish(Connection connection, int listId, String name,
                            BigDecimal price) throws SQLException {
        String sql = """
                INSERT INTO wish
                (wishlist_id, name, description, quantity, price, product_url, image_url)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, listId);
            statement.setString(2, name);
            statement.setString(3, "Testbeskrivelse");
            statement.setInt(4, 2);
            statement.setBigDecimal(5, price);
            statement.setString(6, "https://example.com/product");
            statement.setString(7, "https://example.com/image.png");
            statement.executeUpdate();
        }
    }

    private void deleteList(Connection connection, int id) throws SQLException {
        // ON DELETE CASCADE fjerner også testlistens ønsker.
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM wishlist WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
