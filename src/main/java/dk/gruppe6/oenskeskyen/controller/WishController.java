package dk.gruppe6.oenskeskyen.controller;

import dk.gruppe6.oenskeskyen.model.Wish;
import dk.gruppe6.oenskeskyen.model.WishList;
import dk.gruppe6.oenskeskyen.repository.WishListRepository;
import dk.gruppe6.oenskeskyen.repository.WishRepository;
import jakarta.validation.Valid;
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

        WishList wishList = wishListRepository.getWishlistById(wishlistId);

        if (wishList == null) {
            model.addAttribute("wishlistId", wishlistId);
            model.addAttribute("errorMessage", "Ønskesedlen findes ikke.");
            return "createWish";
        }

        model.addAttribute("wishlist", wishList);
        model.addAttribute("wishlistId", wishlistId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Der er fejl i formularen. Ret venligst markerede felter.");
            return "createWish";
        }

        wish.setWishlistId(wishlistId);

        try {
            wishRepository.createWish(wish);
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage",
                    "Der opstod en fejl ved oprettelsen af ønsket. Prøv igen.");
            return "createWish";
        }

        return "redirect:/showWishList?id=" + wishlistId;
    }
}