import org.junit.jupiter.api.*;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryService(new InventoryRepository());
    }

    // ── ADD PRODUCT ─────────────────────────────────────

    @Test
    void addProduct_shouldCreateWithCorrectData() {
        Product p = service.addProduct("Rice", 100);
        assertEquals("Rice", p.getName());
        assertEquals(100, p.getQuantity());
        assertTrue(p.getId() > 0);
    }

    @Test
    void addProduct_emptyName_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> service.addProduct("", 100));
    }

    @Test
    void addProduct_negativeQuantity_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> service.addProduct("Rice", -10));
    }

    @Test
    void addProduct_shouldAssignUniqueIds() {
        Product p1 = service.addProduct("Rice", 100);
        Product p2 = service.addProduct("Wheat", 200);
        assertNotEquals(p1.getId(), p2.getId());
    }

    // ── STOCK IN ────────────────────────────────────────

    @Test
    void stockIn_shouldIncreaseQuantity() {
        Product p = service.addProduct("Rice", 100);
        service.stockIn(p.getId(), 50);
        assertEquals(150, service.getProduct(p.getId()).getQuantity());
    }

    @Test
    void stockIn_zeroQuantity_shouldThrow() {
        Product p = service.addProduct("Rice", 100);
        assertThrows(IllegalArgumentException.class,
            () -> service.stockIn(p.getId(), 0));
    }

    @Test
    void stockIn_nonExistentProduct_shouldThrow() {
        assertThrows(NoSuchElementException.class,
            () -> service.stockIn(999, 50));
    }

    // ── STOCK OUT ───────────────────────────────────────

    @Test
    void stockOut_shouldDecreaseQuantity() {
        Product p = service.addProduct("Rice", 100);
        service.stockOut(p.getId(), 30);
        assertEquals(70, service.getProduct(p.getId()).getQuantity());
    }

    @Test
    void stockOut_insufficientStock_shouldThrow() {
        Product p = service.addProduct("Rice", 20);
        assertThrows(IllegalStateException.class,
            () -> service.stockOut(p.getId(), 50));
    }

    @Test
    void stockOut_exactQuantity_shouldResultInZero() {
        Product p = service.addProduct("Rice", 50);
        service.stockOut(p.getId(), 50);
        assertEquals(0, service.getProduct(p.getId()).getQuantity());
    }

    @Test
    void stockOut_nonExistentProduct_shouldThrow() {
        assertThrows(NoSuchElementException.class,
            () -> service.stockOut(999, 10));
    }

    // ── UPDATE STOCK ────────────────────────────────────

    @Test
    void updateStock_shouldSetExactQuantity() {
        Product p = service.addProduct("Rice", 100);
        service.updateStock(p.getId(), 200);
        assertEquals(200, service.getProduct(p.getId()).getQuantity());
    }

    @Test
    void updateStock_negativeQuantity_shouldThrow() {
        Product p = service.addProduct("Rice", 100);
        assertThrows(IllegalArgumentException.class,
            () -> service.updateStock(p.getId(), -5));
    }

    @Test
    void updateStock_shouldNotAffectOtherProducts() {
        Product p1 = service.addProduct("Rice", 100);
        Product p2 = service.addProduct("Wheat", 200);
        service.updateStock(p1.getId(), 500);
        assertEquals(200, service.getProduct(p2.getId()).getQuantity());
    }

    // ── DELETE PRODUCT ──────────────────────────────────

    @Test
    void deleteProduct_shouldRemoveFromInventory() {
        Product p = service.addProduct("Rice", 100);
        boolean result = service.deleteProduct(p.getId());
        assertTrue(result);
        assertThrows(NoSuchElementException.class,
            () -> service.getProduct(p.getId()));
    }

    @Test
    void deleteProduct_nonExistentProduct_shouldThrow() {
        assertThrows(NoSuchElementException.class,
            () -> service.deleteProduct(999));
    }

    // ── DATA CONSISTENCY ────────────────────────────────

    @Test
    void stockIn_multipleTimes_shouldAccumulate() {
        Product p = service.addProduct("Rice", 100);
        service.stockIn(p.getId(), 50);
        service.stockIn(p.getId(), 50);
        assertEquals(200, service.getProduct(p.getId()).getQuantity());
    }

    @Test
    void stockOut_shouldNotAffectOtherProducts() {
        Product p1 = service.addProduct("Rice", 100);
        Product p2 = service.addProduct("Wheat", 200);
        service.stockOut(p1.getId(), 50);
        assertEquals(200, service.getProduct(p2.getId()).getQuantity());
    }
}
