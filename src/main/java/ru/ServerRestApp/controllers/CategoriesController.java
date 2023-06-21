package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Category;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.CategoriesService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.NotCreatedException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

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
        Optional<Category> category = categoriesService.findById(id);
        if (category.isPresent())
            return category.get();
        else
            throw new NotFoundException("Category with this id wasn't found!");
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotFoundException e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус (404)
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotCreatedException e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
