package dk.gruppe6.oenskeskyen.controller;

import dk.gruppe6.oenskeskyen.model.Wish;
import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import dk.gruppe6.oenskeskyen.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishControllerDeleteTest {
    private WishController controller;
    private WishListRepository wishLists;
    private WishRepository wishes;

    @BeforeEach
    void setUp() {
        controller = new WishController();
        wishLists = mock(WishListRepository.class);
        wishes = mock(WishRepository.class);
        ReflectionTestUtils.setField(controller, "wishListRepository", wishLists);
        ReflectionTestUtils.setField(controller, "wishRepository", wishes);
    }

    @Test
    void missingWishShowsMessageWithCompleteModel() {
        WishList list = new WishList(5, "Fødselsdag");
        when(wishLists.getWishlistById(5)).thenReturn(list);
        when(wishLists.getWishesByWishListID(5)).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("showWishList", controller.confirmDeleteWish(999, 5, model));
        assertSame(list, model.get("wishlist"));
        assertNotNull(model.get("wishes"));
        assertNotNull(model.get("errorMessage"));
    }

    @Test
    void missingWishlistGives404InsteadOfBrokenTemplate() {
        when(wishLists.getWishlistById(5)).thenReturn(null);
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> controller.confirmDeleteWish(999, 5, new ExtendedModelMap()));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }

    @Test
    void deleteDatabaseFailureGives500AndDoesNotClaimSuccess() {
        WishList list = new WishList(5, "Fødselsdag");
        Wish wish = new Wish();
        wish.setId(7);
        when(wishLists.getWishlistById(5)).thenReturn(list);
        when(wishLists.getWishesByWishListID(5)).thenReturn(List.of(wish));
        when(wishes.deleteWish(7, 5))
                .thenThrow(new DataAccessResourceFailureException("MySQL utilgængelig"));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> controller.deleteWish(7, 5, "delete", new ExtendedModelMap()));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusCode());
        assertTrue(e.getReason().contains("kunne ikke bekræftes"));
    }

    @Test
    void cancelNeverCallsDatabase() {
        assertEquals("redirect:/showWishList?id=5",
                controller.deleteWish(7, 5, "cancel", new ExtendedModelMap()));
        verifyNoInteractions(wishes, wishLists);
    }

    @Test
    void unknownWishIsNotDeleted() {
        WishList list = new WishList(5, "Fødselsdag");
        when(wishLists.getWishlistById(5)).thenReturn(list);
        when(wishLists.getWishesByWishListID(5)).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("showWishList", controller.deleteWish(999, 5, "delete", model));
        assertSame(list, model.get("wishlist"));
        assertNotNull(model.get("wishes"));
        verifyNoInteractions(wishes);
    }
}
