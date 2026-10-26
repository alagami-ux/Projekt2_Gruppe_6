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

@Controller
public class WishController {

    private static final Logger log = LoggerFactory.getLogger(WishController.class);

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
            WishList wishList = wishListRepository.getWishlistById(wishlistId);
            if (wishList == null) {
                model.addAttribute("errorMessage", "Ønskesedlen findes ikke. Gå tilbage til Mine ønskesedler.");
                return "createWish";
            }
            model.addAttribute("wishlist", wishList);

            if (bindingResult.hasErrors()) {
                model.addAttribute("errorMessage", "Der er fejl i formularen. Ret venligst markerede felter.");
                return "createWish";
            }

            wishRepository.createWish(wish);

        } catch (DataAccessException e) {
            log.error("Fejl ved oprettelse af ønske for wishlistId={}", wishlistId, e);
            model.addAttribute("wish", wish); // bevar felterne i formularen
            model.addAttribute("errorMessage", "Ønsket kunne ikke gemmes. Prøv igen senere.");
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
                model.addAttribute("errorMessage", "Ønskesedlen findes ikke.");
                return "showWishList";
            }

            boolean found = false;

            for (Wish wish : wishListRepository.getWishesByWishListID(wishlistId)) {
                if (wish.getId() == id) {
                    model.addAttribute("wish", wish);
                    model.addAttribute("wishlist", wishlist);
                    model.addAttribute("wishlistId", wishlistId);
                    found = true;
                    break;
                }
            }

            if (!found) {
                model.addAttribute("wishlist", wishlist);
                model.addAttribute("wishes", wishListRepository.getWishesByWishListID(wishlistId));
                model.addAttribute("errorMessage", "Ønsket findes ikke på denne ønskeseddel.");
                return "showWishList";
            }

            return "deleteWish";

        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", "Ønsket kunne ikke hentes. Prøv igen senere.");
            return "showWishList";
        }
    }

    @PostMapping("/deleteWish")
    public String deleteWish(
            @RequestParam("id") int id,
            @RequestParam("wishlistId") int wishlistId,
            @RequestParam(value = "action", defaultValue = "cancel") String action,
            Model model) {

        if ("cancel".equalsIgnoreCase(action)) {
            return "redirect:/showWishList?id=" + wishlistId;
        }

        try {
            WishList wishlist = wishListRepository.getWishlistById(wishlistId);
            if (wishlist == null) {
                model.addAttribute("errorMessage", "Ønskesedlen findes ikke.");
                return "showWishList";
            }

            boolean existsInWishlist = false;
            for (Wish wish : wishListRepository.getWishesByWishListID(wishlistId)) {
                if (wish.getId() == id) {
                    existsInWishlist = true;
                    break;
                }
            }

            if (!existsInWishlist) {
                model.addAttribute("wishlist", wishlist);
                model.addAttribute("wishes", wishListRepository.getWishesByWishListID(wishlistId));
                model.addAttribute("errorMessage", "Ønsket findes ikke på denne ønskeseddel. Intet blev slettet.");
                return "showWishList";
            }

            int deletedRows = wishRepository.deleteWish(id, wishlistId);

            if (deletedRows == 0) {
                model.addAttribute("wishlist", wishlist);
                model.addAttribute("wishes", wishListRepository.getWishesByWishListID(wishlistId));
                model.addAttribute("errorMessage", "Ønsket findes ikke på denne ønskeseddel. Intet blev slettet.");
                return "showWishList";
            }

            return "redirect:/showWishList?id=" + wishlistId;

        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", "Der opstod en databasefejl. Ønsket blev ikke slettet.");
            return "showWishList";
        }
    }

    @GetMapping("/getUpdateWish")
    public String getUpdateWish(@RequestParam("id") int id, Model model) {
        Wish wish = wishRepository.getWishById(id);

        if (wish == null) {
            model.addAttribute("errorMessage", "Ønsket findes ikke.");
            return "showWishList";
        }

        model.addAttribute("wish", wish);
        model.addAttribute("wishlistId", wish.getWishlistId());

        return "updateWish";
    }

    @PostMapping("/saveUpdateWish")
    public String postUpdateWish(@ModelAttribute Wish wish, @RequestParam("wishlistId") int wishListId) {
        wishRepository.updateWish(wish);

        return "redirect:showWishList?id=" + wishListId;
    }
}