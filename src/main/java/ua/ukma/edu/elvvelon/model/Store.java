package ua.ukma.edu.elvvelon.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Store {
    private final Map<String, Map<String, Product>> productGroups = new ConcurrentHashMap<>();

    public void addProductGroup(String groupName) {
        productGroups.putIfAbsent(groupName, new ConcurrentHashMap<>());
    }

    public Product addProductToGroup(String groupName, String productName) {
        productGroups.putIfAbsent(groupName, new ConcurrentHashMap<>());
        return productGroups.get(groupName).computeIfAbsent(productName, Product::new);
    }

    public int addProductQuantity(String groupName, String productName, int quantity) {
        Product product = getProduct(groupName, productName);
        return product.addQuantity(quantity);
    }

    public int writeOffProductQuantity(String groupName, String productName, int quantity) {
        Product product = getProduct(groupName, productName);
        return product.writeOffQuantity(quantity);
    }

    public void setProductPrice(String groupName, String productName, double price) {
        Product product = getProduct(groupName, productName);
        product.setPrice(price);
    }

    public int getProductQuantity(String groupName, String productName) {
        return getProduct(groupName, productName).getQuantity();
    }

    private Product getProduct(String groupName, String productName) {
        Map<String, Product> group = productGroups.get(groupName);
        if (group == null) {
            throw new IllegalArgumentException("Product group '" + groupName + "' not found.");
        }
        Product product = group.get(productName);
        if (product == null) {
            throw new IllegalArgumentException("Product '" + productName + "' not found in group '" + groupName + "'.");
        }
        return product;
    }

    // Helper method for printing store contents
    public void printStoreState() {
        System.out.println("--- Current Store State ---");
        productGroups.forEach((groupName, products) -> {
            System.out.println("Group: " + groupName);
            products.forEach((productName, product) -> System.out.println("  - " + product));
        });
        System.out.println("--------------------------");
    }
}