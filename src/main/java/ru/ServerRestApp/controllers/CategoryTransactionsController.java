package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.services.CategoryTransactionsService;

import java.util.List;

@RestController
@RequestMapping("/categoryTransactions")
public class CategoryTransactionsController {

    private final CategoryTransactionsService categoryTransactionsService;
    @Autowired
    public CategoryTransactionsController(CategoryTransactionsService categoryTransactionsService) {
        this.categoryTransactionsService = categoryTransactionsService;
    }


    @GetMapping()
    public List<CategoryTransaction> getAllCategoryTransactions() {
        return categoryTransactionsService.findAll();
    }

    @GetMapping("/{id}")
    public CategoryTransaction getCategoryTransaction(@PathVariable("id") int id) {
        return categoryTransactionsService.findById(id);
    }

    @PostMapping("/add")
    public CategoryTransaction addCategoryTransaction(@RequestBody CategoryTransaction categoryTransaction) {
        categoryTransactionsService.save(categoryTransaction);
        return categoryTransaction;
    }

    @PostMapping("/update")
    public CategoryTransaction updateCategoryTransaction(@RequestBody CategoryTransaction categoryTransaction) {
        categoryTransactionsService.update(categoryTransaction);
        return categoryTransactionsService.findById(categoryTransaction.getId());
    }

    @PostMapping("/delete/{id}")
    public void deleteCategoryTransaction(@PathVariable("id") int id) {
        categoryTransactionsService.delete(id);
    }

}
