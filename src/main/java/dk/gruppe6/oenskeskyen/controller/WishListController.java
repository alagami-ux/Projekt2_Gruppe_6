package dk.gruppe6.oenskeskyen.controller;

import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
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
public class WishListController {

    private static final Logger log = LoggerFactory.getLogger(WishListController.class);

    @Autowired
    WishListRepository wishListRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/wishlists")
    public String wishlists(Model model) {
        model.addAttribute("wishlists", wishListRepository.getAllWishLists());
        return "wishlists";
    }

    @GetMapping("/showWishList")
    public String showWishList(@RequestParam("id") int id, Model model) {
        WishList wishlist = wishListRepository.getWishlistById(id);

        if (wishlist == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ønskesedlen findes ikke.");
        }

        model.addAttribute("wishlist", wishlist);
        model.addAttribute("wishes", wishListRepository.getWishesByWishListID(id));

        return "showWishList";
    }

    @GetMapping("/confirmDeleteWishList")
    public String confirmDeleteWishList(@RequestParam("id") int id, Model model) {
        WishList wishlist = wishListRepository.getWishlistById(id);

        if (wishlist == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ønskesedlen findes ikke.");
        }

        model.addAttribute("wishlist", wishlist);
        return "confirmDeleteWishlist";
    }

    @PostMapping("/deleteWishList")
    public String deleteWishList(@RequestParam("id") int id) {
        WishList wishlist = wishListRepository.getWishlistById(id);

        if (wishlist == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ønskesedlen findes ikke.");
        }

        try {
            wishListRepository.deleteWishlist(id);
        } catch (DataAccessException e) {
            log.error("Kunne ikke slette ønskeseddel med id={}", id, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ønskesedlen kunne ikke slettes. Prøv igen senere.");
        }

        return "redirect:/wishlists";
    }

    @GetMapping("/getCreateWishList")
    public String createWishlist(Model model) {
        model.addAttribute("wishList", new WishList());
        return "createWishList";
    }

    @PostMapping("/saveCreateWishList")
    public String postCreateWishlist(
            @Valid @ModelAttribute("wishList") WishList wishList,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "createWishList";
        }

        String title = wishList.getTitle() == null ? "" : wishList.getTitle().trim();

        if (title.isBlank()) {
            model.addAttribute("errorMessage", "Du skal skrive en titel til ønskesedlen.");
            return "createWishList";
        }

        WishList savedWishList = new WishList(title);
        try {
            int id = wishListRepository.createWishList(savedWishList);
            return "redirect:/showWishList?id=" + id;
        } catch (DataAccessException e) {
            log.error("Fejl ved oprettelse af ønskeseddel med titel={}", title, e);
            model.addAttribute("wishList", wishList); // bevar titel i formularen
            model.addAttribute("errorMessage", "Ønskesedlen kunne ikke gemmes. Prøv igen senere.");
            return "createWishList";
        }
    }

    @GetMapping("/getUpdateWishList")
    public String getUpdateWishlist(@RequestParam("id") int id, Model model) {
        WishList wishList = wishListRepository.getWishlistById(id);
        model.addAttribute("wishList", wishList);
        return "updateWishlist";
    }

    @PostMapping("/saveUpdateWishList")
    public String postUpdateWishList(@ModelAttribute WishList wishList) {
        wishListRepository.updateWishList(wishList);
        return "redirect:/showWishList?id=" + wishList.getId();
    }
}