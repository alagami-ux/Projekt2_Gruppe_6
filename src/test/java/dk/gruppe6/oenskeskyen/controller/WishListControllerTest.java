package dk.gruppe6.oenskeskyen.controller;

import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class WishListControllerTest {

    private WishListRepository repository;
    private MockMvc mockMvc;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setup() {
        repository = mock(WishListRepository.class);

        WishListController controller = new WishListController();
        ReflectionTestUtils.setField(
                controller, "wishListRepository", repository);

        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void cleanup() {
        validator.close();
    }

    @Test
    void displaysCurrentTitle() throws Exception {
        when(repository.getWishlistById(1))
                .thenReturn(new WishList(1, "Fødselsdag"));

        mockMvc.perform(get("/getUpdateWishList")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateWishlist"))
                .andExpect(model().attribute("canEdit", true))
                .andExpect(model().attribute(
                        "wishList",
                        hasProperty("title", is("Fødselsdag"))));

        verify(repository, never()).updateWishList(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void rejectsEmptyOrWhitespaceTitle(String title) throws Exception {
        mockMvc.perform(post("/saveUpdateWishList")
                        .param("id", "1")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(view().name("updateWishlist"))
                .andExpect(model().attributeHasFieldErrors(
                        "wishList", "title"))
                .andExpect(model().attribute(
                        "wishList", hasProperty("title", is(title))));

        verifyNoInteractions(repository);
    }

    @Test
    void rejects151CharactersWithoutDatabaseCalls() throws Exception {
        String title = "A".repeat(151);

        mockMvc.perform(post("/saveUpdateWishList")
                        .param("id", "1")
                        .param("title", title))
                .andExpect(view().name("updateWishlist"))
                .andExpect(model().attributeHasFieldErrors(
                        "wishList", "title"))
                .andExpect(model().attribute(
                        "wishList", hasProperty("title", is(title))));

        verifyNoInteractions(repository);
    }

    @Test
    void accepts150CharactersAndPreservesId() throws Exception {
        String title = "A".repeat(150);

        when(repository.getWishlistById(1))
                .thenReturn(new WishList(1, "Gammel titel"));

        mockMvc.perform(post("/saveUpdateWishList")
                        .param("id", "1")
                        .param("title", title))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/showWishList?id=1"));

        ArgumentCaptor<WishList> saved =
                ArgumentCaptor.forClass(WishList.class);

        verify(repository).updateWishList(saved.capture());

        assertEquals(1, saved.getValue().getId());
        assertEquals(title, saved.getValue().getTitle());
    }

    @Test
    void unchangedTitleRedirectsWithoutUpdate() throws Exception {
        when(repository.getWishlistById(1))
                .thenReturn(new WishList(1, "Fødselsdag"));

        mockMvc.perform(post("/saveUpdateWishList")
                        .param("id", "1")
                        .param("title", "Fødselsdag"))
                .andExpect(redirectedUrl("/showWishList?id=1"));

        verify(repository, never()).updateWishList(any());
    }

    @Test
    void unknownWishlistCannotBeEdited() throws Exception {
        when(repository.getWishlistById(999)).thenReturn(null);

        mockMvc.perform(get("/getUpdateWishList")
                        .param("id", "999"))
                .andExpect(view().name("updateWishlist"))
                .andExpect(model().attribute("canEdit", false))
                .andExpect(model().attribute(
                        "errorMessage", "Ønskesedlen findes ikke."));

        mockMvc.perform(post("/saveUpdateWishList")
                        .param("id", "999")
                        .param("title", "Ny titel"))
                .andExpect(view().name("updateWishlist"))
                .andExpect(model().attribute("canEdit", false))
                .andExpect(model().attribute(
                        "errorMessage",
                        "Ønskesedlen findes ikke. Intet blev ændret."));

        verify(repository, never()).updateWishList(any());
    }

    @Test
    void databaseFailurePreservesInput() throws Exception {
        when(repository.getWishlistById(1))
                .thenReturn(new WishList(1, "Gammel titel"));

        doThrow(new DataAccessResourceFailureException("Testfejl"))
                .when(repository).updateWishList(any(WishList.class));

        mockMvc.perform(post("/saveUpdateWishList")
                        .param("id", "1")
                        .param("title", "Ny titel"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateWishlist"))
                .andExpect(model().attribute(
                        "wishList", hasProperty("title", is("Ny titel"))))
                .andExpect(model().attribute(
                        "errorMessage",
                        "Ændringen kunne ikke bekræftes. "
                                + "Kontrollér ønskesedlen, før du prøver igen."));
    }

    @Test
    void databaseReadFailureDisplaysDanishMessage() throws Exception {
        when(repository.getWishlistById(1))
                .thenThrow(
                        new DataAccessResourceFailureException("Testfejl"));

        mockMvc.perform(get("/getUpdateWishList")
                        .param("id", "1"))
                .andExpect(view().name("updateWishlist"))
                .andExpect(model().attribute("canEdit", false))
                .andExpect(model().attribute(
                        "errorMessage",
                        "Ønskesedlen kunne ikke hentes. Prøv igen senere."));

        verify(repository, never()).updateWishList(any());
    }
}