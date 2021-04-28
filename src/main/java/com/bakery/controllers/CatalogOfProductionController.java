package com.bakery.controllers;

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
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/catalog")
public class CatalogOfProductionController {

    @Value("${upload.path}")
    private String uploasPath;

    private final ProductionRepository productRepo;
    private final TypeRepository typeRepo;

    public CatalogOfProductionController(ProductionRepository productRepo, TypeRepository typeRepo) {
        this.productRepo = productRepo;
        this.typeRepo = typeRepo;
    }

    @GetMapping()
    public String viewCatalog(Model model) {
        model = setModeltWithTypes(model);
        return "/catalog/catalog";
    }

    @GetMapping("/admin/edit")
    public String catalogEditList(Model model) {
        model = setModeltWithTypes(model);
        return "/catalog/catalogEditList";
    }

    @GetMapping("/admin/edit/{id}")
    public String productEdit(@PathVariable("id") Product product, Model model) {
        model.addAttribute("product", product);
        model.addAttribute("types", typeRepo.findAll());
        return "/catalog/productEdit";
    }

    @GetMapping("/admin/delete/{id}")
    public String delete(@PathVariable("id") Product product, Model model) {
        productRepo.delete(product);
        model = setModeltWithTypes(model);
        return "/catalog/catalogEditList";
    }
    
    @GetMapping("/admin/types/deletType/{id}")
    public String deleteType(@PathVariable("id") Type type){
        typeRepo.delete(type);
        
        boolean flag = true;
        Type noneType = new Type();
        noneType.setName("Неизвестный");
        
        for(Product prod : productRepo.findAll()){
            if (prod.getType()==type.getId())
                if(flag){
                    noneType.setId(typeRepo.save(noneType).getId());
                    prod.setType(noneType.getId());
                    productRepo.save(prod);
                    flag = false;
                }else{
                    prod.setType(noneType.getId());
                }
        }
        return "redirect:/catalog/admin/types";
    }
    
    @GetMapping("/admin/types")
    public String typeList(Model model){
        model.addAttribute("types", typeRepo.findAll());
        return "/catalog/typeList";
    }

    @GetMapping("/admin/types/addType")
    public String newType(@ModelAttribute("product") Product product, Model model) {
        Type type = new Type();
        System.out.println(product);
        model.addAttribute("type", type);
        return "/catalog/addNewType";
    }

    @PostMapping("/admin/types/addType")
    public String addNewType(@ModelAttribute("type") Type type) {
        typeRepo.save(type);
        return "redirect:/catalog/admin/types";
    }

    @GetMapping("/admin/edit/new")
    public String productAdd(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("types", typeRepo.findAll());
        return "/catalog/productEdit";
    }

    @PostMapping("/admin/edit")
    public String saveProduct(@ModelAttribute("product") @Valid Product product,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/catalog/productEdit";
        }

        if (!file.isEmpty()) {
            File uploadDir = new File(uploasPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + '.' + product.getId() + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            File fl = new File(uploadDir.getAbsolutePath() + "/" + product.getImageUrl());
            fl.delete();

            product.setImageUrl(resultFileName);
            file.transferTo(new File(uploadDir.getAbsolutePath() + "/" + resultFileName));
        }
        productRepo.save(product);
        return "redirect:/catalog/admin/edit";
    }

    private Model setModeltWithTypes(Model model) {
        List<Type> typeList = new ArrayList<>();
        List<Product> prodList = new ArrayList<>();

        productRepo.findAll().forEach(i -> prodList.add(i));
        typeRepo.findAll().forEach(i -> typeList.add(i));

        for (Type type : typeList) {
            model.addAttribute("Attr" + String.valueOf(type.getId()),
                    prodList.stream().filter(x -> x.getType() == type.getId()).collect(Collectors.toList()));
        }
        
        model.addAttribute("types", typeList);
        return model;

    }
}
