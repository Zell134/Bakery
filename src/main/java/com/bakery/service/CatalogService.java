package com.bakery.service;

import com.bakery.data.ProductionRepository;
import com.bakery.data.TypeRepository;
import com.bakery.models.Product;
import com.bakery.models.Type;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CatalogService {

    @Value("${upload.path}")
    private String uploadsPath;

    private final ProductionRepository productRepo;
    private final TypeRepository typeRepo;

    @Autowired
    public CatalogService(ProductionRepository productRepo, TypeRepository typeRepo) {
        this.productRepo = productRepo;
        this.typeRepo = typeRepo;
    }

    public Model setModeltWithTypes(Model model, long selectedType, boolean onlyActive) {
        List<Type> typeList;
        List<Product> prodList;

        if (onlyActive) {
            prodList = productRepo.findByActive(true);
        } else {
            prodList = findAllProducts();
        }
        typeList = findAllTypes();

        if (selectedType == -1) {
            Map<Long, List<Product>> productWithTypes;

            productWithTypes = prodList.stream()
                    .collect(Collectors.groupingBy(Product::getType));

            productWithTypes.forEach((type, products) -> model.addAttribute("Attr" + type, products));
        } else {
            prodList = prodList.stream()
                            .filter(product -> product.getType() == selectedType)
                            .collect(Collectors.toList());
            model.addAttribute("Attr" + String.valueOf(selectedType),prodList);
        }
        model.addAttribute("types", typeList);
        return model;
    }

    public List<Type> findAllTypes() {
        return StreamSupport.stream(typeRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Product> findAllProducts() {
        return StreamSupport.stream(productRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void deleteType(Type type) {

        List<Product> products = productRepo.findByType(type.getId());

        if (products.isEmpty()) {
            typeRepo.delete(type);
        } else {
            type.setName("Неизвестный");
            typeRepo.save(type);
            
            products.forEach(product -> {
                product.setActive(false);
                productRepo.save(product);
            });
        }
    }

    public void saveType(Type type) {
        typeRepo.save(type);
    }

    public void saveProduct(Product product, MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            File uploadDir = new File(uploadsPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + '.' + product.getId() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            File fl = new File(uploadDir.getAbsolutePath() + "/" + product.getImageUrl());
            if (fl.exists() && fl.isFile()) {
                fl.delete();
            }
            product.setImageUrl(resultFileName);
            file.transferTo(new File(uploadDir.getAbsolutePath() + "/" + resultFileName));
        }
        productRepo.save(product);
    }

    public void activate(Product product) {
        product.setActive(!product.isActive());
        productRepo.save(product);
    }
}
