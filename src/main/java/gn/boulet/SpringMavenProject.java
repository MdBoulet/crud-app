package gn.boulet;

import gn.boulet.model.Product;
import gn.boulet.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.Stream;

@SpringBootApplication
public class SpringMavenProject {

    @Autowired
    private ProductRepository repository;

    @PostConstruct
    public void init() {
        repository.saveAll(Stream.of(
                        new Product("Iphone 12 pro", 1, 1200),
                        new Product("Airpods", 1, 999)
                ).toList()
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringMavenProject.class, args);
    }
}