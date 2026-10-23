package dk.gruppe6.oenskeskyen.controller;

import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class WishListController {

    @Autowired
    WishListRepository wishListRepository;

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
    public String createWishlist() {
        return "createWishList";
    }

    @PostMapping("/saveCreateWishList")
    public String postCreateWishlist(@RequestParam("title") String title) {
        WishList wishList = new WishList(title);

        if (bindingResult.hasErrors()) {
            return "createWishList";
        }

        return"redirect:/wishlists";

        return "redirect: wishlists";
    }


}
