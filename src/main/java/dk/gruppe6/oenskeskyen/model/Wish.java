package dk.gruppe6.oenskeskyen.model;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Wish {

    private int id;
    private int wishlistId;
    @NotBlank(message = "Indtast et ønske til ønskesedlen.")
    @Size(max = 150, message = "Ønsket må højst være 100 tegn.")
    private String name;
    private String description;
    @jakarta.validation.constraints.Min(value = 1, message = "Antallet skal mindst være 1.")
    private int quantity = 1;
    @jakarta.validation.constraints.DecimalMin(value ="0.00", message = "Prisen må ikke være negativ.")
    @jakarta.validation.constraints.Digits(
            integer = 8,
            fraction = 2,
            message = "Prisen må højst have 8 cifre før kommaet og 2 decimaler."
    )
    private BigDecimal price;
    @jakarta.validation.constraints.Size(max = 2048, message = "Linket må højst være 2048 tegn.")
    private String productUrl;
    @jakarta.validation.constraints.Size(max = 2048, message = "Linket må højst være 2048 tegn.")
    private String imageUrl;

    public Wish() {
    }

    public Wish(int id, int wishlistId, String name,
                String description, int quantity,
                BigDecimal price, String productUrl,
                String imageUrl) {
        this.id = id;
        this.wishlistId = wishlistId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.productUrl = productUrl;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

