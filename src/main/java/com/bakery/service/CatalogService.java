package com.bakery.service;

import com.bakery.data.ProductionRepository;
import com.bakery.data.TypeRepository;
import com.bakery.models.Product;
import com.bakery.models.Type;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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

    public Model setModeltWithTypes(Model model, long selectedType) {
        List<Type> typeList = new ArrayList<>();
        List<Product> prodList = new ArrayList<>();

        productRepo.findAll().forEach(i -> prodList.add(i));
        typeRepo.findAll().forEach(i -> typeList.add(i));

        for (Type type : typeList) {
            if (selectedType == -1) {
                model.addAttribute("Attr" + String.valueOf(type.getId()),
                        prodList.stream().filter(x -> x.getType() == type.getId()).collect(Collectors.toList()));
            } else {
                model.addAttribute("Attr" + String.valueOf(selectedType),
                        prodList.stream().filter(x -> x.getType() == selectedType).collect(Collectors.toList()));
            }
        }
        model.addAttribute("types", typeList);
        return model;
    }

    public Iterable<Type> findAllTypes() {
        return typeRepo.findAll();
    }

    public Iterable<Product> findAllProducts() {
        return productRepo.findAll();
    }

    public void deleteProduct(Product product) {

        File uploadDir = new File(uploadsPath);
        File fl = new File(uploadDir.getAbsolutePath() + "/" + product.getImageUrl());
        fl.delete();

        productRepo.delete(product);
    }

    public void deleteType(Type type) {
        typeRepo.delete(type);

        boolean flag = true;
        Type noneType = new Type();
        noneType.setName("Неизвестный");

        for (Product prod : productRepo.findAll()) {
            if (prod.getType() == type.getId()) {
                if (flag) {
                    noneType.setId(typeRepo.save(noneType).getId());
                    prod.setType(noneType.getId());
                    productRepo.save(prod);
                    flag = false;
                } else {
                    prod.setType(noneType.getId());
                }
            }
        }
    }

    public void saveType(Type type) {
        typeRepo.save(type);
    }

    public void saveProduct(Product product, MultipartFile file) throws IOException {
        
        

        if (!file.isEmpty()) {
            File uploadDir = new File(uploadsPath);

            if (!uploadDir.exists()) {
                System.out.println(uploadDir.mkdir());
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + '.' + product.getId() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            File fl = new File(uploadDir.getAbsolutePath() + "/" + product.getImageUrl());
            if(fl.exists())
                fl.delete();
            product.setImageUrl(resultFileName);
            file.transferTo(new File(uploadDir.getAbsolutePath() + "/" + resultFileName));
        }
        productRepo.save(product);
    }
}
