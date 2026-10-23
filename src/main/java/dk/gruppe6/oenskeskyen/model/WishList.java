package dk.gruppe6.oenskeskyen.model;

public class WishList {

    private int id;
    private String title;

    public WishList() {
    }

    public WishList(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public WishList(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}