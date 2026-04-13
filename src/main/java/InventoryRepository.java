import java.util.*;

public class InventoryRepository {
    private final Map<Integer, Product> store = new HashMap<>();

    public void save(Product product) {
        store.put(product.getId(), product);
    }

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean delete(int id) {
        return store.remove(id) != null;
    }

    public boolean exists(int id) {
        return store.containsKey(id);
    }
}