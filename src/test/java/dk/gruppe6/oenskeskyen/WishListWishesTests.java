package dk.gruppe6.oenskeskyen;

import dk.gruppe6.oenskeskyen.model.Wish;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import org.junit.jupiter.api.AfterEach;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WishListWishesTests {

    @Autowired
    private WishListRepository repository;

    @Autowired
    private DataSource dataSource;

    // Gem kun ID'erne på de lister, den aktuelle test selv opretter.
    private final ArrayList<Integer> testListIds = new ArrayList<>();

    @Test
    void findsWishesForSelectedWishlist() throws SQLException {
        // Forbered to lister og ønsker med forskellige priser.
        int selectedId = createList();
        int otherId = createList();
        insertWish(selectedId, "Bog", new BigDecimal("199.95"));
        insertWish(otherId, "Anden listes ønske", new BigDecimal("12.50"));
        insertWish(selectedId, "Uden pris", null);
        insertWish(selectedId, "Gratis", BigDecimal.ZERO);

        // Hent ønskerne fra den valgte liste.
        List<Wish> wishes = repository.getWishesByWishListID(selectedId);

        // Kontrollér antal, tilhørsforhold og rækkefølge.
        assertEquals(3, wishes.size());
        for (Wish wish : wishes) {
            assertEquals(selectedId, wish.getWishlistId());
        }
        assertTrue(wishes.get(0).getId() < wishes.get(1).getId());
        assertTrue(wishes.get(1).getId() < wishes.get(2).getId());

        // Kontrollér, at oplysningerne er læst korrekt fra databasen.
        Wish book = wishes.get(0);
        assertEquals("Bog", book.getName());
        assertEquals("Testbeskrivelse", book.getDescription());
        assertEquals(2, book.getQuantity());
        assertEquals(new BigDecimal("199.95"), book.getPrice());
        assertEquals("https://example.com/product", book.getProductUrl());
        assertEquals("https://example.com/image.png", book.getImageUrl());

        assertEquals("Uden pris", wishes.get(1).getName());
        assertNull(wishes.get(1).getPrice());
        assertEquals("Gratis", wishes.get(2).getName());
        assertEquals(new BigDecimal("0.00"), wishes.get(2).getPrice());
    }

    @Test
    void returnsEmptyListWhenNoWishesAreAttached() throws SQLException {
        int id = createList();

        List<Wish> wishes = repository.getWishesByWishListID(id);

        assertNotNull(wishes);
        assertTrue(wishes.isEmpty());
    }

    private int createList() throws SQLException {
        String sql = "INSERT INTO wishlist (title) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Repositorytest");
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                assertTrue(keys.next());
                int id = keys.getInt(1);
                testListIds.add(id);
                return id;
            }
        }
    }

    private void insertWish(int listId, String name, BigDecimal price)
            throws SQLException {
        String sql = "INSERT INTO wish "
                + "(wishlist_id, name, description, quantity, price, product_url, image_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
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

    @AfterEach
    void deleteTestLists() throws SQLException {
        if (testListIds.isEmpty()) {
            return;
        }

        // JUnit kører denne metode efter hver test, også hvis en assertion fejler.
        // ON DELETE CASCADE sletter også ønskerne på testlisterne.
        String sql = "DELETE FROM wishlist WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int id : testListIds) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        }
    }
}
