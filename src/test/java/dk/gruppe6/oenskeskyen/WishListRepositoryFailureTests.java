package dk.gruppe6.oenskeskyen;

import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WishListRepositoryFailureTests {

    @Test
    void connectionFailureIsNotReportedAsMissingWishlist() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        SQLException failure = new SQLException("Connection unavailable");
        when(dataSource.getConnection()).thenThrow(failure);

        DataAccessException result = assertThrows(DataAccessException.class,
                () -> new WishListRepository(dataSource).getWishlistById(1));

        assertSame(failure, result.getCause());
    }

    @Test
    void queryFailureClosesResourcesAndPreservesCause() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        SQLException failure = new SQLException("Query failed");
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenThrow(failure);

        DataAccessException result = assertThrows(DataAccessException.class,
                () -> new WishListRepository(dataSource).getWishlistById(1));

        assertSame(failure, result.getCause());
        verify(statement).close();
        verify(connection).close();
    }
}
