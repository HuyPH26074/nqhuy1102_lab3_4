package fpoly.huynqph26074.executerservice;

public class Document {
    private int id;

    private String name;
    private String price;
    private String brand;

    public Document(String name, String price, String brand) {
        this.name = name;
        this.price = price;
        this.brand = brand;
    }

    public Document() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
