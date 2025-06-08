package ua.ukma.edu.elvvelon.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Product {
    private final String name;
    private final AtomicInteger quantity = new AtomicInteger(0);
    private final AtomicReference<Double> price = new AtomicReference<>(0.0);

    public Product(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public int addQuantity(int amount) {
        return quantity.addAndGet(amount);
    }

    public int writeOffQuantity(int amount) {
        // Prevents quantity from going below zero
        return quantity.updateAndGet(current -> Math.max(0, current - amount));
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double newPrice) {
        price.set(newPrice);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
