package dk.gruppe6.oenskeskyen.model;

import java.math.BigDecimal;

public class Wish {

    private int id;
    private int wishlistId;
    private String name;
    private String description;
    private Integer quantity = 1;
    private double price;
    private String productUrl;
    private String imageUrl;

    public Wish() {
    }

    public Wish(int id, int wishlistId, String name,
                String description, Integer quantity,
                double price, String productUrl,
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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
