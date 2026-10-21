package dk.gruppe6.oenskeskyen;

import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WishListRepositoryTests {

    @Autowired
    private WishListRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findsWishlistById() {
        jdbcTemplate.update(
                "INSERT INTO wishlist (title) VALUES (?)",
                "Testliste"
        );

        Integer id = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );

        WishList result = repository.getWishlistById(id)
                .orElseThrow();

        assertEquals(id.intValue(), result.getId());
        assertEquals("Testliste", result.getTitle());
    }

    @Test
    void returnsEmptyWhenWishlistDoesNotExist() {
        assertTrue(repository.getWishlistById(-1).isEmpty());
    }
}