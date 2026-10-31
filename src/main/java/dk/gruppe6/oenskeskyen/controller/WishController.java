package dk.gruppe6.oenskeskyen.controller;

import dk.gruppe6.oenskeskyen.model.Wish;
import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import dk.gruppe6.oenskeskyen.repository.WishRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Objects;

@Controller
public class WishController {

    private static final Logger log =
            LoggerFactory.getLogger(WishController.class);

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private WishRepository wishRepository;

    @GetMapping("/getCreateWish")
    public String getCreateWish(@RequestParam("id") int id, Model model) {
        WishList wishList = wishListRepository.getWishlistById(id);

        if (wishList == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Ønskesedlen findes ikke."
            );
        }

        model.addAttribute("wishlist", wishList);

        Wish wish = new Wish();
        wish.setWishlistId(id);
        wish.setQuantity(1);

        model.addAttribute("wish", wish);
        model.addAttribute("wishlistId", id);

        return "createWish";
    }

    @PostMapping("/saveCreateWish")
    public String saveCreateWish(
            @Valid @ModelAttribute("wish") Wish wish,
            BindingResult bindingResult,
            @RequestParam("wishlistId") int wishlistId,
            Model model) {

        model.addAttribute("wishlistId", wishlistId);
        wish.setWishlistId(wishlistId);

        try {
            WishList wishList =
                    wishListRepository.getWishlistById(wishlistId);

            if (wishList == null) {
                model.addAttribute(
                        "errorMessage",
                        "Ønskesedlen findes ikke. "
                                + "Gå tilbage til Mine ønskesedler."
                );
                return "createWish";
            }

            model.addAttribute("wishlist", wishList);

            if (bindingResult.hasErrors()) {
                model.addAttribute(
                        "errorMessage",
                        "Der er fejl i formularen. "
                                + "Ret venligst markerede felter."
                );
                return "createWish";
            }

            wishRepository.createWish(wish);

        } catch (DataAccessException e) {
            log.error(
                    "Fejl ved oprettelse af ønske for wishlistId={}",
                    wishlistId,
                    e
            );

            model.addAttribute("wish", wish);
            model.addAttribute(
                    "errorMessage",
                    "Ønsket kunne ikke gemmes. Prøv igen senere."
            );
            return "createWish";
        }

        return "redirect:/showWishList?id=" + wishlistId;
    }

    @GetMapping("/confirmDeleteWish")
    public String confirmDeleteWish(
            @RequestParam("id") int id,
            @RequestParam("wishlistId") int wishlistId,
            Model model) {

        try {
            WishList wishlist = wishListRepository.getWishlistById(wishlistId);
            if (wishlist == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ønskesedlen findes ikke.");
            }

            // Hent kun ønsker én gang og brug samme data ved fejlvisning.
            var wishes = wishListRepository.getWishesByWishListID(wishlistId);
            for (Wish wish : wishes) {
                if (wish.getId() == id) {
                    model.addAttribute("wish", wish);
                    model.addAttribute("wishlist", wishlist);
                    model.addAttribute("wishlistId", wishlistId);
                    return "deleteWish";
                }
            }

            model.addAttribute("wishlist", wishlist);
            model.addAttribute("wishes", wishes);
            model.addAttribute("errorMessage",
                    "Ønsket findes ikke på denne ønskeseddel.");
            return "showWishList";

        } catch (DataAccessException e) {
            log.error("Kunne ikke hente ønske id={} på liste id={}",
                    id, wishlistId, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ønsket kunne ikke hentes. Prøv igen senere.", e);
        }
    }

    @PostMapping("/deleteWish")
    public String deleteWish(
            @RequestParam("id") int id,
            @RequestParam("wishlistId") int wishlistId,
            @RequestParam(value = "action", defaultValue = "cancel")
            String action,
            Model model) {

        if (!"delete".equals(action)) {
            return "redirect:/showWishList?id=" + wishlistId;
        }

        try {
            WishList wishlist = wishListRepository.getWishlistById(wishlistId);
            if (wishlist == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ønskesedlen findes ikke.");
            }

            var wishes = wishListRepository.getWishesByWishListID(wishlistId);
            boolean existsInWishlist = wishes.stream()
                    .anyMatch(wish -> wish.getId() == id);

            if (!existsInWishlist) {
                model.addAttribute("wishlist", wishlist);
                model.addAttribute("wishes", wishes);
                model.addAttribute("errorMessage",
                        "Ønsket findes ikke på denne ønskeseddel. Intet blev slettet.");
                return "showWishList";
            }

            int deletedRows = wishRepository.deleteWish(id, wishlistId);
            if (deletedRows == 0) {
                // Ønsket kan være slettet af et andet request imens.
                model.addAttribute("wishlist", wishlist);
                model.addAttribute("wishes",
                        wishListRepository.getWishesByWishListID(wishlistId));
                model.addAttribute("errorMessage",
                        "Ønsket findes ikke længere. Kontrollér ønskesedlen.");
                return "showWishList";
            }

            return "redirect:/showWishList?id=" + wishlistId;

        } catch (DataAccessException e) {
            log.error("Fejl ved sletning af ønske id={} på liste id={}",
                    id, wishlistId, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Sletningen kunne ikke bekræftes. Kontrollér ønskesedlen, før du prøver igen.",
                    e);
        }
    }

    @GetMapping("/getUpdateWish")
    public String getUpdateWish(
            @RequestParam("id") int id,
            Model model) {

        model.addAttribute("canEdit", false);

        try {
            Wish wish = wishRepository.getWishById(id);

            if (wish == null) {
                model.addAttribute(
                        "errorMessage",
                        "Ønsket findes ikke."
                );
                return "updateWish";
            }

            model.addAttribute("wish", wish);
            model.addAttribute("wishlistId", wish.getWishlistId());
            model.addAttribute("canEdit", true);

        } catch (DataAccessException e) {
            log.error("Failed to load wish with id={}", id, e);
            model.addAttribute(
                    "errorMessage",
                    "Ønsket kunne ikke hentes. Prøv igen senere."
            );
        }

        return "updateWish";
    }

    @PostMapping("/saveUpdateWish")
    public String postUpdateWish(
            @Valid @ModelAttribute("wish") Wish wish,
            BindingResult bindingResult,
            Model model) {

        model.addAttribute("wishlistId", wish.getWishlistId());
        model.addAttribute("canEdit", false);

        try {
            Wish existingWish =
                    wishRepository.getWishById(wish.getId());

            if (existingWish == null
                    || existingWish.getWishlistId() != wish.getWishlistId()) {
                model.addAttribute(
                        "errorMessage",
                        "Ønsket findes ikke på denne ønskeseddel."
                );
                return "updateWish";
            }

            model.addAttribute("canEdit", true);

            if (bindingResult.hasErrors()) {
                model.addAttribute(
                        "errorMessage",
                        "Der er fejl i formularen. Ret de markerede felter."
                );
                return "updateWish";
            }

            if (hasSameEditableValues(existingWish, wish)) {
                return "redirect:/showWishList?id="
                        + existingWish.getWishlistId();
            }

            int updatedRows = wishRepository.updateWish(wish);

            if (updatedRows == 0) {
                Wish currentWish =
                        wishRepository.getWishById(wish.getId());

                if (currentWish == null
                        || currentWish.getWishlistId() != wish.getWishlistId()) {
                    model.addAttribute("canEdit", false);
                    model.addAttribute(
                            "errorMessage",
                            "Ønsket findes ikke længere på denne ønskeseddel."
                    );
                    return "updateWish";
                }

                // Some MySQL settings report zero for unchanged values.
                if (hasSameEditableValues(currentWish, wish)) {
                    return "redirect:/showWishList?id="
                            + currentWish.getWishlistId();
                }

                model.addAttribute(
                        "errorMessage",
                        "Ændringen kunne ikke bekræftes. "
                                + "Kontrollér ønskesedlen, før du prøver igen."
                );
                return "updateWish";
            }

            return "redirect:/showWishList?id=" + wish.getWishlistId();

        } catch (DataAccessException e) {
            log.error(
                    "Failed to update wish with id={}",
                    wish.getId(),
                    e
            );
            model.addAttribute(
                    "errorMessage",
                    "Ændringen kunne ikke bekræftes. "
                            + "Kontrollér ønskesedlen, før du prøver igen."
            );
            return "updateWish";
        }
    }

    private boolean hasSameEditableValues(Wish first, Wish second) {
        return Objects.equals(first.getName(), second.getName())
                && Objects.equals(
                emptyIfNull(first.getDescription()),
                emptyIfNull(second.getDescription()))
                && first.getQuantity() == second.getQuantity()
                && hasSamePrice(first.getPrice(), second.getPrice())
                && Objects.equals(
                emptyIfNull(first.getProductUrl()),
                emptyIfNull(second.getProductUrl()))
                && Objects.equals(
                emptyIfNull(first.getImageUrl()),
                emptyIfNull(second.getImageUrl()));
    }

    private boolean hasSamePrice(BigDecimal first, BigDecimal second) {
        if (first == null || second == null) {
            return first == null && second == null;
        }

        return first.compareTo(second) == 0;
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }
}