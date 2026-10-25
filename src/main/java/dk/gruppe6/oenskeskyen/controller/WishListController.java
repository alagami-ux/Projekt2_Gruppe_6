package dk.gruppe6.oenskeskyen.controller;

import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    WishListRepository wishListRepository;

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
            model.addAttribute("errorMessage", "Du skal skrive en titel til ønskesedlen.");
            return "createWishList";
        }

        String title = wishList.getTitle() == null ? "" : wishList.getTitle().trim();

        if (title.isBlank()) {
            model.addAttribute("errorMessage", "Du skal skrive en titel til ønskesedlen.");
            return "createWishList";
        }

        WishList savedWishList = new WishList(title);
        int id = wishListRepository.createWishList(savedWishList);

        return "redirect:/showWishList?id=" + id;
    }
}