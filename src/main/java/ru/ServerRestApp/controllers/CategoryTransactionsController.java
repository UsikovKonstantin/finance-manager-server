package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.ServerRestApp.models.*;
import ru.ServerRestApp.services.CategoriesService;
import ru.ServerRestApp.services.CategoryTransactionsService;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;
import ru.ServerRestApp.validators.CategoryTransactionValidator;

import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/categoryTransactions")
public class CategoryTransactionsController {

    private final CategoryTransactionsService categoryTransactionsService;
    private final PeopleService peopleService;
    private final CategoriesService categoriesService;
    private final CategoryTransactionValidator categoryTransactionValidator;
    @Autowired
    public CategoryTransactionsController(CategoryTransactionsService categoryTransactionsService, PeopleService peopleService, CategoriesService categoriesService, CategoryTransactionValidator categoryTransactionValidator) {
        this.categoryTransactionsService = categoryTransactionsService;
        this.peopleService = peopleService;
        this.categoriesService = categoriesService;
        this.categoryTransactionValidator = categoryTransactionValidator;
    }


    @GetMapping()
    public ResponseEntity<List<CategoryTransaction>> getAllCategoryTransactions() {
        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findAll();
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }


    @GetMapping("/person/{id}")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByPersonId(@PathVariable("id") int id) {
        Optional<Person> person = peopleService.findById(id);
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByPersonId(id);
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByCategoryId(@PathVariable("id") int id) {
        Optional<Category> category = categoriesService.findById(id);
        if (category.isEmpty())
            throw new NotFoundException("Category with this id wasn't found!");

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByCategoryId(id);
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryTransaction> getCategoryTransaction(@PathVariable("id") int id) {
        Optional<CategoryTransaction> categoryTransaction = categoryTransactionsService.findById(id);
        if (categoryTransaction.isEmpty())
            throw new NotFoundException("CategoryTransaction with this id wasn't found!");
        return new ResponseEntity<>(categoryTransaction.get(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryTransaction> addCategoryTransaction(@RequestBody @Valid CategoryTransaction categoryTransaction, BindingResult bindingResult) {

        categoryTransactionValidator.validate(categoryTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        categoryTransaction.setId(0);
        categoryTransactionsService.save(categoryTransaction);

        return new ResponseEntity<>(categoryTransaction, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CategoryTransaction> updateCategoryTransaction(@PathVariable("id") int id, @RequestBody @Valid CategoryTransaction categoryTransaction, BindingResult bindingResult) {

        categoryTransaction.setId(id);
        if (categoryTransactionsService.findById(id).isEmpty())
            bindingResult.rejectValue("id", "", "CategoryTransaction with this id wasn't found!");

        categoryTransactionValidator.validate(categoryTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        categoryTransactionsService.update(categoryTransaction);

        return new ResponseEntity<>(categoryTransaction, HttpStatus.OK);
    }


    @PostMapping("/delete/{id}")
    public ResponseEntity<CategoryTransaction> deleteCategoryTransaction(@PathVariable("id") int id) {

        Optional<CategoryTransaction> foundCategoryTransaction = categoryTransactionsService.findById(id);
        if (foundCategoryTransaction.isEmpty())
            throw new NotFoundException("CategoryTransaction with this id wasn't found!");

        categoryTransactionsService.delete(id);

        return new ResponseEntity<>(foundCategoryTransaction.get(), HttpStatus.OK);
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
    private ResponseEntity<ErrorResponse> handleException(DataException e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(HttpClientErrorException.Unauthorized e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

}
