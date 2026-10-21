package dk.gruppe6.oenskeskyen.repository;

import dk.gruppe6.oenskeskyen.model.WishList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WishListRepository {

    private final JdbcTemplate jdbcTemplate;

    public WishListRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<WishList> getWishlistById(int id) {
        String sql = "SELECT id, title FROM wishlist WHERE id = ?";

        List<WishList> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new WishList(
                        rs.getInt("id"),
                        rs.getString("title")
                ),
                id
        );

        return results.stream().findFirst();
    }
}