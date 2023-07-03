package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.*;
import ru.ServerRestApp.services.CategoryTransactionsService;
import ru.ServerRestApp.util.*;
import ru.ServerRestApp.validators.CategoryTransactionValidator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/categoryTransactions")
public class CategoryTransactionsController {

    private final CategoryTransactionsService categoryTransactionsService;
    private final CategoryTransactionValidator categoryTransactionValidator;
    private final PersonUtil personUtil;
    @Autowired
    public CategoryTransactionsController(CategoryTransactionsService categoryTransactionsService, CategoryTransactionValidator categoryTransactionValidator, PersonUtil personUtil) {
        this.categoryTransactionsService = categoryTransactionsService;
        this.categoryTransactionValidator = categoryTransactionValidator;
        this.personUtil = personUtil;
    }

    /*
    // Получить все транзакции
    @GetMapping()
    public ResponseEntity<List<CategoryTransaction>> getAllCategoryTransactions() {
        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findAll();
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }
    */


    // Получить транзакции пользователя за все время
    @GetMapping("/person")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByPersonId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByPersonId(person.getId());
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    // Получить транзакции команды за все время
    @GetMapping("/team")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByTeamId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByPersonTeamId(person.getTeam().getId());
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    /*
    // Получить все транзакции по определенной категории
    @GetMapping("/category/byId")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByCategoryId(@RequestBody Category bodyCategory) {
        Optional<Category> category = categoriesService.findById(bodyCategory.getId());
        if (category.isEmpty())
            throw new NotFoundException("Category with this id wasn't found!");

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByCategoryId(bodyCategory.getId());
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }
    */

    /*
    // Получить транзакцию по id
    @GetMapping("/byId")
    public ResponseEntity<CategoryTransaction> getCategoryTransaction(@RequestBody CategoryTransaction bodyCategoryTransaction) {
        Optional<CategoryTransaction> categoryTransaction = categoryTransactionsService.findById(bodyCategoryTransaction.getId());
        if (categoryTransaction.isEmpty())
            throw new NotFoundException("CategoryTransaction with this id wasn't found!");
        return new ResponseEntity<>(categoryTransaction.get(), HttpStatus.OK);
    }
    */

    // Получить доходы человека за все время, сгруппированные по категории
    @GetMapping("/person/income")
    public ResponseEntity<List<CategoryTransactionGroup>> getPositiveTransactionsByCategoryForPerson(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.getPositiveTransactionsByCategoryForPerson(person.getId()), HttpStatus.OK);
    }

    // Получить расходы человека за все время, сгруппированные по категории
    @GetMapping("/person/expenses")
    public ResponseEntity<List<CategoryTransactionGroup>> getNegativeTransactionsByCategoryForPerson(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.getNegativeTransactionsByCategoryForPerson(person.getId()), HttpStatus.OK);
    }

    // Получить доходы группы за все время, сгруппированные по категории
    @GetMapping("/team/income")
    public ResponseEntity<List<CategoryTransactionGroup>> getPositiveTransactionsByCategoryForGroup(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.getPositiveTransactionsByCategoryForGroup(person.getTeam().getId()), HttpStatus.OK);
    }

    // Получить расходы группы за все время, сгруппированные по категории
    @GetMapping("/team/expenses")
    public ResponseEntity<List<CategoryTransactionGroup>> getNegativeTransactionsByCategoryForGroup(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.getNegativeTransactionsByCategoryForGroup(person.getTeam().getId()), HttpStatus.OK);
    }



    // Получить транзакции пользователя за месяц
    @GetMapping("/person/month")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByPersonIdForMonth(@RequestHeader("Authorization") String token,
                                                                                               @RequestParam("timestamp") long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByPersonIdForMonth(
                person.getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    // Получить транзакции группы за месяц
    @GetMapping("/team/month")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByTeamId(@RequestHeader("Authorization") String token,
                                                                                     @RequestParam("timestamp") long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByPersonTeamIdForMonth(
                person.getTeam().getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    // Получить доходы пользователя за месяц
    @GetMapping("/person/income/month")
    public ResponseEntity<List<CategoryTransactionGroup>> getPositiveTransactionsByCategoryForPersonForMonth(@RequestHeader("Authorization") String token,
                                                                                                             @RequestParam("timestamp") long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.getPositiveTransactionsByCategoryForPersonForMonth(
                person.getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)), HttpStatus.OK);
    }

    // Получить расходы пользователя за месяц
    @GetMapping("/person/expenses/month")
    public ResponseEntity<List<CategoryTransactionGroup>> getNegativeTransactionsByCategoryForPersonForMonth(@RequestHeader("Authorization") String token,
                                                                                                             @RequestParam("timestamp") long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.getNegativeTransactionsByCategoryForPersonForMonth(
                person.getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)), HttpStatus.OK);
    }

    // Получить доходы группы за месяц
    @GetMapping("/team/income/month")
    public ResponseEntity<List<CategoryTransactionGroup>> getPositiveTransactionsByCategoryForGroupForMonth(@RequestHeader("Authorization") String token,
                                                                                                            @RequestParam("timestamp") long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.getPositiveTransactionsByCategoryForGroupForMonth(
                person.getTeam().getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)), HttpStatus.OK);
    }

    // Получить расходы группы за месяц
    @GetMapping("/team/expenses/month")
    public ResponseEntity<List<CategoryTransactionGroup>> getNegativeTransactionsByCategoryForGroupForMonth(@RequestHeader("Authorization") String token,
                                                                                                            @RequestParam("timestamp") long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.getNegativeTransactionsByCategoryForGroupForMonth(
                person.getTeam().getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)), HttpStatus.OK);
    }



    // Получить n последних доходов пользователя
    @GetMapping("/person/income/last")
    public ResponseEntity<List<CategoryTransaction>> findNLastPositiveTransactionsForPerson(@RequestHeader("Authorization") String token,
                                                                                            @RequestParam("limit") int limit) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.findNLastPositiveTransactionsForPerson(person.getId(), limit), HttpStatus.OK);
    }

    // Получить n последних расходов пользователя
    @GetMapping("/person/expenses/last")
    public ResponseEntity<List<CategoryTransaction>> findNLastNegativeTransactionsForPerson(@RequestHeader("Authorization") String token,
                                                                                                     @RequestParam("limit") int limit) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(categoryTransactionsService.findNLastNegativeTransactionsForPerson(person.getId(), limit), HttpStatus.OK);
    }



    // Добавить транзакцию
    @PostMapping("/add")
    public ResponseEntity<CategoryTransaction> addCategoryTransaction(@RequestHeader("Authorization") String token,
                                                                      @RequestBody @Valid CategoryTransaction categoryTransaction,
                                                                      BindingResult bindingResult) {

        Person person = personUtil.getPersonByToken(token);

        categoryTransaction.setPerson(person);

        categoryTransactionValidator.validate(categoryTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        categoryTransaction.setId(0);
        categoryTransactionsService.save(categoryTransaction);

        return new ResponseEntity<>(categoryTransaction, HttpStatus.OK);
    }

    // Обновить транзакцию
    @PostMapping("/update")
    public ResponseEntity<CategoryTransaction> updateCategoryTransaction(@RequestHeader("Authorization") String token,
                                                                         @RequestBody @Valid CategoryTransaction categoryTransaction,
                                                                         BindingResult bindingResult) {

        if (categoryTransactionsService.findById(categoryTransaction.getId()).isEmpty())
            bindingResult.rejectValue("id", "", "CategoryTransaction with this id wasn't found!");

        Person person = personUtil.getPersonByToken(token);

        categoryTransaction.setPerson(person);

        categoryTransactionValidator.validate(categoryTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        categoryTransactionsService.update(categoryTransaction);

        return new ResponseEntity<>(categoryTransaction, HttpStatus.OK);
    }

    // Удалить транзакцию
    @PostMapping("/delete")
    public ResponseEntity<CategoryTransaction> deleteCategoryTransaction(@RequestHeader("Authorization") String token,
                                                                         @RequestBody CategoryTransaction bodyCategoryTransaction) {

        Optional<CategoryTransaction> foundCategoryTransaction = categoryTransactionsService.findById(bodyCategoryTransaction.getId());
        if (foundCategoryTransaction.isEmpty())
            throw new NotFoundException("CategoryTransaction with this id wasn't found!");

        Person person = personUtil.getPersonByToken(token);

        if (person.getId() != foundCategoryTransaction.get().getPerson().getId())
            throw new DataException("Attempt to change another person's data");

        categoryTransactionsService.delete(bodyCategoryTransaction.getId());

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
}
