// ProductStore.java
package src;

import java.util.ArrayList;
import java.util.List;

public class ProductStore {
    private static ProductStore instance;
    private List<ViewProductDialog.ProductData> products = new ArrayList<>();
    
    private ProductStore() {}
    
    public static ProductStore getInstance() {
        if (instance == null) {
            instance = new ProductStore();
        }
        return instance;
    }
    
    public List<ViewProductDialog.ProductData> getProducts() {
        return new ArrayList<>(products);
    }
    
    public void setProducts(List<ViewProductDialog.ProductData> products) {
        this.products = new ArrayList<>(products);
    }
}