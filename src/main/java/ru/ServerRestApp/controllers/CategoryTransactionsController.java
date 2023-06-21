package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Category;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.CategoryTransactionsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.NotCreatedException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

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
        Optional<CategoryTransaction> categoryTransaction = categoryTransactionsService.findById(id);
        if (categoryTransaction.isPresent())
            return categoryTransaction.get();
        else
            throw new NotFoundException("CategoryTransaction with this id wasn't found!");
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryTransaction> addCategoryTransaction(@RequestBody @Valid CategoryTransaction categoryTransaction, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            throw new NotCreatedException(errorMsg.toString());
        }

        categoryTransactionsService.save(categoryTransaction);

        return new ResponseEntity<>(categoryTransaction, HttpStatus.OK);
    }

    @PostMapping("/update")
    public CategoryTransaction updateCategoryTransaction(@RequestBody CategoryTransaction categoryTransaction) {
        categoryTransactionsService.update(categoryTransaction);
        return categoryTransactionsService.findById(categoryTransaction.getId()).get();
    }

    @PostMapping("/delete/{id}")
    public void deleteCategoryTransaction(@PathVariable("id") int id) {
        categoryTransactionsService.delete(id);
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
