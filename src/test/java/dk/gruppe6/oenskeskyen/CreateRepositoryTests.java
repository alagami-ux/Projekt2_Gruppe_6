package dk.gruppe6.oenskeskyen;

import dk.gruppe6.oenskeskyen.model.Wish;
import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import dk.gruppe6.oenskeskyen.repository.WishRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreateRepositoryTests {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private DataSource dataSource;

    private WishList testList;

    @Test
    void createsWishlistWithIdAndSavesItsWishes() throws SQLException {
        // Opret listen gennem den rigtige repository-metode.
        createTestList();
        assertTrue(testList.getId() > 0);

        WishList savedList = wishListRepository.getWishlistById(testList.getId());
        assertNotNull(savedList);
        assertEquals("Oprettelsestest", savedList.getTitle());

        // Brug listens nye ID til at gemme ønsker med og uden pris.
        Wish book = new Wish(0, testList.getId(), "Bog", "En gyser", 1,
                new BigDecimal("199.95"), null, null);
        Wish cup = new Wish(0, testList.getId(), "Kop", null, 2,
                null, null, null);
        wishRepository.createWish(book);
        wishRepository.createWish(cup);

        List<Wish> wishes = wishListRepository.getWishesByWishListID(testList.getId());
        assertEquals(2, wishes.size());
        assertEquals("Bog", wishes.get(0).getName());
        assertEquals(new BigDecimal("199.95"), wishes.get(0).getPrice());
        assertEquals("Kop", wishes.get(1).getName());
        assertNull(wishes.get(1).getPrice());
    }

    @Test
    void invalidWishlistThrowsDatabaseException() {
        WishList invalidList = new WishList(0, "   ");

        DataAccessException failure = assertThrows(DataAccessException.class,
                () -> wishListRepository.createWishList(invalidList));

        assertTrue(failure.getCause() instanceof SQLException);
        assertEquals(0, invalidList.getId());
    }

    @Test
    void invalidWishThrowsDatabaseException() {
        createTestList();
        Wish invalidWish = new Wish(0, testList.getId(), "Bog", null, 0,
                null, null, null);

        DataAccessException failure = assertThrows(DataAccessException.class,
                () -> wishRepository.createWish(invalidWish));

        assertTrue(failure.getCause() instanceof SQLException);
        assertTrue(wishListRepository.getWishesByWishListID(testList.getId()).isEmpty());
    }

    private void createTestList() {
        testList = new WishList(0, "Oprettelsestest");
        wishListRepository.createWishList(testList);
    }

    @AfterEach
    void deleteTestList() throws SQLException {
        if (testList == null || testList.getId() == 0) {
            return;
        }

        // Slet kun denne tests liste. Databasen sletter dens ønsker med CASCADE.
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM wishlist WHERE id = ?")) {
            statement.setInt(1, testList.getId());
            statement.executeUpdate();
        }
    }
}
