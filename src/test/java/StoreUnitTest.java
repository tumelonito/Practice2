import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.ukma.edu.elvvelon.model.Product;
import ua.ukma.edu.elvvelon.model.Store;

import static org.junit.jupiter.api.Assertions.*;

public class StoreUnitTest {

    private Store store;
    private final String groupName = "Electronics";
    private final String productName = "Laptop";

    @BeforeEach
    void setUp() {
        store = new Store();
    }

    @Test
    @DisplayName("Should add a new product group")
    void testAddProductGroup() {
        store.addProductGroup(groupName);
        // Should not throw an exception when trying to add a product to it
        assertDoesNotThrow(() -> store.addProductToGroup(groupName, "Mouse"));
    }

    @Test
    @DisplayName("Should add a product to a new group")
    void testAddProductToNewGroup() {
        Product product = store.addProductToGroup(groupName, productName);
        assertNotNull(product);
        assertEquals(productName, product.getName());
        assertEquals(0, product.getQuantity());
    }

    @Test
    @DisplayName("Should return existing product if added again")
    void testAddExistingProduct() {
        Product firstProduct = store.addProductToGroup(groupName, productName);
        Product secondProduct = store.addProductToGroup(groupName, productName);
        assertSame(firstProduct, secondProduct, "Adding an existing product should return the same instance.");
    }

    @Test
    @DisplayName("Should not allow quantity to become negative")
    void testWriteOffQuantity_MoreThanAvailable() {
        store.addProductToGroup(groupName, productName);
        store.addProductQuantity(groupName, productName, 50);
        int finalQuantity = store.writeOffProductQuantity(groupName, productName, 100);
        assertEquals(0, finalQuantity, "Quantity should be 0, not negative.");
    }

    @Test
    @DisplayName("Should throw exception when product group not found")
    void testOperateOnProduct_GroupNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.getProductQuantity("NonExistentGroup", productName);
        });
        assertEquals("Product group 'NonExistentGroup' not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when product not found in group")
    void testOperateOnProduct_ProductNotFound() {
        store.addProductGroup(groupName);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.getProductQuantity(groupName, "NonExistentProduct");
        });
        assertEquals("Product 'NonExistentProduct' not found in group '" + groupName + "'.", exception.getMessage());
    }

    @Test
    @DisplayName("Should correctly set and get product price")
    void testSetAndGetPrice() {
        double price = 1299.99;
        store.addProductToGroup(groupName, productName);
        store.setProductPrice(groupName, productName, price);
        Product product = store.addProductToGroup(groupName, productName); // get the product instance
        assertEquals(price, product.getPrice());
    }
}