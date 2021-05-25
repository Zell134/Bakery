package com.bakery.controllers;

import com.bakery.models.Order;
import com.bakery.models.Product;
import com.bakery.models.Type;
import com.bakery.service.CatalogService;
import java.io.IOException;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/catalog")
@SessionAttributes({"selectedTypeofProduction", "currentOrder"})
public class CatalogController {

    CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping()
    public String viewAllCatalog(Model model) {
        model.addAttribute("currentType", "Весь каталог");
        model = service.setModeltWithTypes(model, -1, true);
        model.addAttribute("selectedTypeofProduction", -1);
        if (model.getAttribute("currentOrder") == null) {
            model.addAttribute("currentOrder", new Order());
        }
        return "catalog/catalog";
    }

    @GetMapping("/info/{id}")
    public String showProductInfo(@PathVariable("id") Product product, Model model) {
        model.addAttribute(product);
        if (model.getAttribute("currentOrder") == null) {
            model.addAttribute("currentOrder", new Order());
        }
        return "order/info";
    }

    @GetMapping("{id}")
    public String viewSortedCatalog(@PathVariable("id") Type type, Model model) {
        model.addAttribute("currentType", type.getName());
        model = service.setModeltWithTypes(model, type.getId(), true);
        model.addAttribute("selectedTypeofProduction", type.getId());
        if (model.getAttribute("currentOrder") == null) {
            model.addAttribute("currentOrder", new Order());
        }
        return "catalog/catalog";
    }

    @GetMapping("/admin/edit")
    public String catalogEditList(Model model) {
        model = service.setModeltWithTypes(model, -1, false);
        return "catalog/catalogEditList";
    }

    @GetMapping("/admin/edit/{id}")
    public String productEdit(@PathVariable("id") Product product, Model model) {
        model.addAttribute("product", product);
        model.addAttribute("types", service.findAllTypes());
        return "catalog/productEdit";
    }

    @GetMapping("/admin/activate/{id}")
    public String productActivate(@PathVariable("id") Product product, Model model) {
        System.out.println(product);
        service.activate(product);
        model = service.setModeltWithTypes(model, -1, false);
        return "catalog/catalogEditList";
    }

    @GetMapping("/admin/types/deletType/{id}")
    public String deleteType(@PathVariable("id") Type type) {

        service.deleteType(type);

        return "redirect:/catalog/admin/types";
    }

    @GetMapping("/admin/types")
    public String typeList(Model model) {
        model.addAttribute("types", service.findAllTypes());
        return "catalog/typeList";
    }

    @GetMapping("/admin/types/addType")
    public String newType(@ModelAttribute("product") Product product, Model model) {
        Type type = new Type();
        model.addAttribute("type", type);
        return "catalog/addNewType";
    }

    @PostMapping("/admin/types/addType")
    public String addNewType(@ModelAttribute("type") @Valid Type type, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "catalog/addNewType";
        }
        service.saveType(type);
        return "redirect:/catalog/admin/types";
    }

    @GetMapping("/admin/edit/new")
    public String productAdd(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("types", service.findAllTypes());
        return "catalog/productEdit";
    }

    @PostMapping("/admin/edit")
    public String saveProduct(@ModelAttribute("product") @Valid Product product,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", service.findAllTypes());
            return "catalog/productEdit";
        }
        if (product.getType() == 0) {
            bindingResult.addError(new FieldError("type", "type", "Выберите тип!"));
            model.addAttribute("types", service.findAllTypes());
            return "catalog/productEdit";
        }
        service.saveProduct(product, file);

        return "redirect:/catalog/admin/edit";
    }

}
