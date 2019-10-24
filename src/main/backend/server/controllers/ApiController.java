package server.controllers;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.domain.Product;
import server.domain.User;
import server.dto.FiltersList;
import server.dto.ProductGroup;
import server.services.ProductBuilder;
import server.services.ProductService;
import server.services.UserService;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ApiController {
    /*!!!ВМЕСТО ОБРАБОТКИ ЗАПРОСВ КАЖДЫЙ РАЗ ЧЕРЕЗ API, СОЗДАНИЕ JSON C ДАННЫМИ ДЛЯ КАЖДОГО СЕРВИСА!!!*/
    private final ProductService productService;
    private final ProductBuilder productBuilder;
    private final UserService userService;


    /*Users*/
    @PostMapping("/user/registration")
    private boolean registration(@RequestBody Map<String, String> userDetails) {
        log.info(userDetails.toString());
        return userService.registerUser(userDetails);
    }

    @PostMapping("/user/login")
    private void login(@AuthenticationPrincipal User user, Authentication auth) {//@RequestBody Map<String, String> userDetails) {
        /*log.info(userDetails.toString());
        UserDetails userDetails1 = userService.loadUserByUsername(userDetails.get("username"));
        log.info(userDetails1.getUsername());*/
        log.info(auth.toString());
        /*try {
            log.info(auth.toString());
            log.info(auth.getPrincipal().toString());
            log.info(user.toString());
        }
        catch (NullPointerException e) {
            e.getStackTrace();
        }*/

        //log.info(userDetails1.getPassword());
        //log.info(userDetails1.toString());
    }

    /*Filters*/
    @PostMapping("/filters/filterProducts/{group}")
    private Page<Product> filterProducts(@RequestBody Map<String, String[]> filters, @PathVariable String group) {
        return productService.filterProducts(filters, group);
    }

    /*Products*/
    @GetMapping("/products/{group}/{page}")
    private Page<Product> listProductsByGroupPage(@PathVariable String group, @PathVariable(required = false) int page) {
        return productService.getProductsByGroup(group, PageRequest.of(page, 15, Sort.Direction.ASC, "pic"));
    }
    @GetMapping("/products/product/{productID}")
    private Product listProductByID(@PathVariable String productID) {
        return productService.getProductByID(productID);
    }

    /*Catalog*/
    /*@PostMapping("/all/catalog")
    private LinkedHashMap<String, List<ProductGroup>> listFullCatalog(@RequestBody String[] categories) {
        return productService.mapCatalogJSON(categories);
    }*/
    @PostMapping("/all/catalog")
    private LinkedHashMap<String, List<ProductGroup>> listFullCatalog(@RequestBody String[] categories) {
        return productService.getAllCategories(categories);
    }
    /*Page*/
    @GetMapping("/page/filters/{group}")
    private FiltersList createFiltersLists(@PathVariable String group) {
        return productService.createProductsFilterLists(group);
    }

    /*Admin*/
    @PostMapping("/admin/uploadFileDB")
    private void uploadProductsDBFile(@RequestParam("file") MultipartFile file) {
        try {
            productBuilder.updateProductsDB(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/admin/uploadFileBrands")
    private void uploadBrandsPrice(@RequestParam("fileBrands") MultipartFile file) {
        productBuilder.updateBrandsPrice(file);
    }
    @PostMapping("/admin/test")
    private void uploadProductsDBFile() {
        productBuilder.test();
    }
}
