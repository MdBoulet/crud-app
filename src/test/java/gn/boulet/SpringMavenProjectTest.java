package gn.boulet;

import gn.boulet.model.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringMavenProjectTest {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://127.0.0.1";

    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository h2Repository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(String.valueOf(port)).concat("/products");
    }

    @Test
    public void testAddProduct() {
        Product product = new Product("Iphone 12 pro", 1, 1200);
        Product response = restTemplate.postForObject(baseUrl, product, Product.class);

        assertEquals(response.getName(), product.getName());
    }

    @Test
    @Sql(statements = "INSERT INTO product(id, name, qty, price) VALUES (4, 'Mac Book Pro 21', 1, 2400)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM product WHERE id = 4", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllProducts() {
        List<Product> products = restTemplate.getForObject(baseUrl, List.class);

        assertEquals(products.size(), h2Repository.findAll().size());
    }

    @Test
    @Sql(statements = "INSERT INTO product(id, name, qty, price) VALUES (5, 'Bloc Notes', 4, 2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM product WHERE id = 5", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testShowOneProduct() {
        Product response = restTemplate.getForObject(baseUrl + "/{id}", Product.class, 5);

        Product productFromDB = h2Repository.findById(5).get();

        assertAll(() -> {
            assertNotNull(response);
            assertEquals(productFromDB.getName(), response.getName());
            assertEquals(productFromDB.getPrice(), response.getPrice());
        });
    }

    @Test
    @Sql(statements = "INSERT INTO product(id, name, qty, price) VALUES (6, 'Tesla Model S', 1, 2600)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM product WHERE id = 6", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateProduct() {
        Product product = new Product("Tesla Model X", 1,35000);
        restTemplate.put(baseUrl + "/update/{id}", product, 6);

        Product response = h2Repository.findById(6).get();

        assertAll(() -> {
            assertNotNull(response);
            assertEquals(product.getName(), response.getName());
            assertEquals(product.getPrice(), response.getPrice());
            assertEquals(product.getQty(), response.getQty());
        });
    }

    @Test
    @Sql(statements = "INSERT INTO product(id, name, qty, price) VALUES (8, 'Tesla Model S', 1, 2600)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteProduct() {
        int size = h2Repository.findAll().size();
        restTemplate.delete(baseUrl + "/delete/{id}", 8);
        Product deleteProduct = h2Repository.findAll().stream().filter(product -> product.getId() == 8).findFirst().orElse(null);
        assertFalse(deleteProduct != null);
    }
}