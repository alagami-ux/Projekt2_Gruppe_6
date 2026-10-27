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
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void opensCreateWishlistForm() throws Exception {
        mockMvc.perform(get("/getCreateWishList"))
                .andExpect(status().isOk())
                .andExpect(view().name("createWishList"))
                .andExpect(model().attributeExists("wishList"));
    }

    @Test
    void createsWishlistAndRedirects() throws Exception {
        when(repository.createWishList(any(WishList.class)))
                .thenReturn(42);

        mockMvc.perform(post("/saveCreateWishList")
                        .param("title", "Min fødselsdag"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/showWishList?id=42"));

        verify(repository).createWishList(any(WishList.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void rejectsInvalidWishlistTitle(String title) throws Exception {

        mockMvc.perform(post("/saveCreateWishList")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(view().name("createWishList"))
                .andExpect(model().attributeHasFieldErrors(
                        "wishList", "title"));

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsWishlistTitleOver150Characters() throws Exception {

        String title = "A".repeat(151);

        mockMvc.perform(post("/saveCreateWishList")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(view().name("createWishList"))
                .andExpect(model().attributeHasFieldErrors(
                        "wishList", "title"));

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsMissingWishlistTitle() throws Exception {

        mockMvc.perform(post("/saveCreateWishList"))
                .andExpect(status().isOk())
                .andExpect(view().name("createWishList"))
                .andExpect(model().attributeHasFieldErrors(
                        "wishList", "title"));

        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 150})
    void acceptsValidWishlistTitles(int length) throws Exception {

        String title = "A".repeat(length);

        when(repository.createWishList(any(WishList.class)))
                .thenReturn(42);

        mockMvc.perform(post("/saveCreateWishList")
                        .param("title", title))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/showWishList?id=42"));

        ArgumentCaptor<WishList> captor =
                ArgumentCaptor.forClass(WishList.class);

        verify(repository).createWishList(captor.capture());

        assertEquals(title, captor.getValue().getTitle());
    }

    @Test
    void showsDanishErrorAndPreservesTitle() throws Exception {

        String title = "   ";

        mockMvc.perform(post("/saveCreateWishList")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(view().name("createWishList"))
                .andExpect(model().attribute(
                        "wishList",
                        hasProperty("title", is(title))))
                .andExpect(model().attributeHasFieldErrors(
                        "wishList", "title"))
                .andExpect(result -> {

                    BindingResult errors = (BindingResult)
                            result.getModelAndView()
                                    .getModel()
                                    .get(BindingResult.MODEL_KEY_PREFIX
                                            + "wishList");

                    assertNotNull(errors);
                    assertNotNull(errors.getFieldError("title"));

                    assertEquals(
                            "Indtast en titel til ønskesedlen.",
                            errors.getFieldError("title")
                                    .getDefaultMessage());
                });

        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void rejectsNonPositiveWishlistId(String id) throws Exception {

        mockMvc.perform(get("/showWishList")
                        .param("id", id))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(repository);
    }

    @Test
    void returns404ForUnknownWishlistId() throws Exception {

        when(repository.getWishlistById(999))
                .thenReturn(null);

        mockMvc.perform(get("/showWishList")
                        .param("id", "999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> {

                    ResponseStatusException exception =
                            (ResponseStatusException)
                                    result.getResolvedException();

                    assertNotNull(exception);

                    assertEquals(
                            "Ønskesedlen findes ikke.",
                            exception.getReason());
                });

        verify(repository).getWishlistById(999);
        verify(repository, never())
                .getWishesByWishListID(anyInt());
    }

    @Test
    void returns400ForMissingWishlistId() throws Exception {

        mockMvc.perform(get("/showWishList"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    ResponseStatusException exception =
                            assertInstanceOf(
                                    ResponseStatusException.class,
                                    result.getResolvedException());

                    assertEquals(
                            "Du skal angive et ønskeseddel-ID.",
                            exception.getReason());
                });

        verifyNoInteractions(repository);
    }

    @Test
    void returns400ForNonNumericWishlistId() throws Exception {

        mockMvc.perform(get("/showWishList")
                        .param("id", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    ResponseStatusException exception =
                            assertInstanceOf(
                                    ResponseStatusException.class,
                                    result.getResolvedException());

                    assertEquals(
                            "Ønskeseddel-ID skal være et positivt heltal.",
                            exception.getReason());
                });

        verifyNoInteractions(repository);
    }

    @Test
    void showsExistingWishlistWithoutWishes() {

        int id = 7;

        // Simulate an existing wishlist
        when(repository.getWishlistById(id))
                .thenReturn(new WishList(id, "Tom ønskeseddel"));

        // Simulate a wishlist containing no wishes
        when(repository.getWishesByWishListID(id))
                .thenReturn(java.util.List.of());

        // Create the controller and inject the mocked repository
        WishListController controller = new WishListController();

        ReflectionTestUtils.setField(
                controller, "wishListRepository", repository);

        // Create a model to store the controller's data
        ExtendedModelMap model = new ExtendedModelMap();

        // Call the controller method directly
        String viewName = controller.showWishList("7", model);

        // Verify the correct page is returned
        assertEquals("showWishList", viewName);

        // Verify the correct wishlist is returned
        WishList result = (WishList) model.get("wishlist");

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Tom ønskeseddel", result.getTitle());

        // Verify the list of wishes is empty
        assertEquals(
                java.util.List.of(),
                model.get("wishes"));

        // Verify the correct repository methods were called
        verify(repository).getWishlistById(id);
        verify(repository).getWishesByWishListID(id);
    }

    @Test
    void databaseFailureIsNotTreatedAsMissingWishlist() {

        int id = 7;

        // Simulate a database failure
        when(repository.getWishlistById(id))
                .thenThrow(
                        new DataAccessResourceFailureException(
                                "Testfejl i databasen"));

        WishListController controller = new WishListController();

        ReflectionTestUtils.setField(
                controller, "wishListRepository", repository);

        // Verify that the database exception is preserved
        DataAccessResourceFailureException exception =
                assertThrows(
                        DataAccessResourceFailureException.class,
                        () -> controller.showWishList(
                                "7", new ExtendedModelMap()));

        assertEquals(
                "Testfejl i databasen",
                exception.getMessage());

        verify(repository).getWishlistById(id);

        // No wishes should be retrieved after the failure
        verify(repository, never())
                .getWishesByWishListID(anyInt());
    }
}