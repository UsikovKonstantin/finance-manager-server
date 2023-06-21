package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ServerRestApp.models.Category;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.CategoriesService;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;
    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }


    @GetMapping()
    public List<Category> getAllCategories() {
        return categoriesService.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable("id") int id) {
        return categoriesService.findById(id);
    }

}
