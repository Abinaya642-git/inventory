import java.util.*;

public class InventoryService {
    private final InventoryRepository repo;
    private int idCounter = 1;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }

    // ADD new product to inventory
    public Product addProduct(String name, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        Product product = new Product(idCounter++, name, quantity);
        repo.save(product);
        return product;
    }

    // GET product
    public Product getProduct(int id) {
        return repo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
    }

    // GET all products
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    // STOCK IN - increase stock
    public Product stockIn(int id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock-in quantity must be positive");
        }
        Product product = getProduct(id);
        product.setQuantity(product.getQuantity() + quantity);
        repo.save(product);
        return product;
    }

    // STOCK OUT - decrease stock
    public Product stockOut(int id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock-out quantity must be positive");
        }
        Product product = getProduct(id);
        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + id);
        }
        product.setQuantity(product.getQuantity() - quantity);
        repo.save(product);
        return product;
    }

    // UPDATE stock directly
    public Product updateStock(int id, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        Product product = getProduct(id);
        product.setQuantity(newQuantity);
        repo.save(product);
        return product;
    }

    // DELETE product
    public boolean deleteProduct(int id) {
        if (!repo.exists(id)) {
            throw new NoSuchElementException("Product not found: " + id);
        }
        return repo.delete(id);
    }
}