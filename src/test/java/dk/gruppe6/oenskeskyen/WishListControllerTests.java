package dk.gruppe6.oenskeskyen;

import dk.gruppe6.oenskeskyen.controller.WishListController;
import dk.gruppe6.oenskeskyen.model.Wish;
import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WishListControllerTests {

    private WishListRepository repository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        repository = mock(WishListRepository.class);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(new WishListController(repository))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void showsSelectedWishlistAndItsWishes() throws Exception {
        WishList wishlist = new WishList(7, "Fødselsdag");
        ArrayList<Wish> wishes = new ArrayList<>(List.of(
                new Wish(12, 7, "Bog", "En gyser", 1,
                        new BigDecimal("199.95"), null, null)));
        when(repository.getWishlistById(7)).thenReturn(wishlist);
        when(repository.getWishesByWishListID(7)).thenReturn(wishes);

        mockMvc.perform(get("/showWishList").param("id", "7"))
                .andExpect(status().isOk())
                .andExpect(view().name("showWishList"))
                .andExpect(model().attribute("wishlist", wishlist))
                .andExpect(model().attribute("wishes", wishes));
        verify(repository).getWishesByWishListID(7);
    }

    @Test
    void showsExistingWishlistWithNoWishes() throws Exception {
        WishList wishlist = new WishList(8, "Tom liste");
        ArrayList<Wish> wishes = new ArrayList<>();
        when(repository.getWishlistById(8)).thenReturn(wishlist);
        when(repository.getWishesByWishListID(8)).thenReturn(wishes);

        mockMvc.perform(get("/showWishList").param("id", "8"))
                .andExpect(status().isOk())
                .andExpect(view().name("showWishList"))
                .andExpect(model().attribute("wishlist", wishlist))
                .andExpect(model().attribute("wishes", wishes));
    }

    @Test
    void unknownWishlistReturnsNotFoundWithoutLoadingWishes() throws Exception {
        when(repository.getWishlistById(99)).thenReturn(null);

        mockMvc.perform(get("/showWishList").param("id", "99"))
                .andExpect(status().isNotFound());
        verify(repository, never()).getWishesByWishListID(anyInt());
    }

}
