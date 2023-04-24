package gn.boulet.service;

import gn.boulet.model.Product;
import gn.boulet.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product, int id) {
        Product product1 = productRepository.findById(id).orElse(null);
        if(product1 != null) {
            product1.setName(product.getName());
            product1.setQty(product.getQty());
            product1.setPrice(product.getPrice());

            return productRepository.save(product1);
        } else {
            return null;
        }
    }

    public String deleteProduct(int id) {
        productRepository.deleteById(id);

        return "Product deleted " + id + " successfully";
    }

    public Product showProduct(int id) {
        return productRepository.findById(id).orElse(null);
    }
}
