package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.JWT.repository.TokensRepository;
import ru.ServerRestApp.models.*;
import ru.ServerRestApp.services.CategoriesService;
import ru.ServerRestApp.services.CategoryTransactionsService;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.TeamsService;
import ru.ServerRestApp.util.CategoryTransactionGroup;
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
    private final TokensRepository tokensRepository;
    private final TeamsService teamsService;
    @Autowired
    public CategoryTransactionsController(CategoryTransactionsService categoryTransactionsService, PeopleService peopleService, CategoriesService categoriesService, CategoryTransactionValidator categoryTransactionValidator, TokensRepository tokensRepository, TeamsService teamsService) {
        this.categoryTransactionsService = categoryTransactionsService;
        this.peopleService = peopleService;
        this.categoriesService = categoriesService;
        this.categoryTransactionValidator = categoryTransactionValidator;
        this.tokensRepository = tokensRepository;
        this.teamsService = teamsService;
    }


    @GetMapping()
    public ResponseEntity<List<CategoryTransaction>> getAllCategoryTransactions() {
        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findAll();
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }


    @GetMapping("/person")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByPersonId(@RequestHeader("Authorization") String token) {

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        Optional<Person> person = peopleService.findById(found_person.get().getId());
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByPersonId(found_person.get().getId());
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    @GetMapping("/team")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByTeamId(@RequestHeader("Authorization") String token) {

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        Optional<Person> person = peopleService.findById(found_person.get().getId());
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        Optional<Team> team = teamsService.findById(person.get().getTeam().getId());
        if (team.isEmpty())
            throw new NotFoundException("Team with this id wasn't found!");

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByPersonTeamId(person.get().getTeam().getId());
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    @GetMapping("/category/byId")
    public ResponseEntity<List<CategoryTransaction>> getCategoryTransactionsByCategoryId(@RequestBody Category bodyCategory) {
        Optional<Category> category = categoriesService.findById(bodyCategory.getId());
        if (category.isEmpty())
            throw new NotFoundException("Category with this id wasn't found!");

        List<CategoryTransaction> categoryTransactions = categoryTransactionsService.findByCategoryId(bodyCategory.getId());
        return new ResponseEntity<>(categoryTransactions, HttpStatus.OK);
    }

    @GetMapping("/byId")
    public ResponseEntity<CategoryTransaction> getCategoryTransaction(@RequestBody CategoryTransaction bodyCategoryTransaction) {
        Optional<CategoryTransaction> categoryTransaction = categoryTransactionsService.findById(bodyCategoryTransaction.getId());
        if (categoryTransaction.isEmpty())
            throw new NotFoundException("CategoryTransaction with this id wasn't found!");
        return new ResponseEntity<>(categoryTransaction.get(), HttpStatus.OK);
    }

    @GetMapping("/person/income")
    public ResponseEntity<List<CategoryTransactionGroup>> getPositiveTransactionsByCategoryForPerson(@RequestHeader("Authorization") String token) {

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        Optional<Person> person = peopleService.findById(found_person.get().getId());
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");
        return new ResponseEntity<>(categoryTransactionsService.getPositiveTransactionsByCategoryForPerson(found_person.get().getId()), HttpStatus.OK);
    }

    @GetMapping("/person/expenses")
    public ResponseEntity<List<CategoryTransactionGroup>> getNegativeTransactionsByCategoryForPerson(@RequestHeader("Authorization") String token) {

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        Optional<Person> person = peopleService.findById(found_person.get().getId());
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");
        return new ResponseEntity<>(categoryTransactionsService.getNegativeTransactionsByCategoryForPerson(found_person.get().getId()), HttpStatus.OK);
    }

    @GetMapping("/team/income")
    public ResponseEntity<List<CategoryTransactionGroup>> getPositiveTransactionsByCategoryForGroup(@RequestHeader("Authorization") String token) {

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        Optional<Team> team = teamsService.findById(found_person.get().getTeam().getId());
        if (team.isEmpty())
            throw new NotFoundException("Team with this id wasn't found!");
        return new ResponseEntity<>(categoryTransactionsService.getPositiveTransactionsByCategoryForGroup(found_person.get().getTeam().getId()), HttpStatus.OK);
    }

    @GetMapping("/team/expenses")
    public ResponseEntity<List<CategoryTransactionGroup>> getNegativeTransactionsByCategoryForGroup(@RequestHeader("Authorization") String token) {

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        Optional<Team> team = teamsService.findById(found_person.get().getTeam().getId());
        if (team.isEmpty())
            throw new NotFoundException("Team with this id wasn't found!");
        return new ResponseEntity<>(categoryTransactionsService.getNegativeTransactionsByCategoryForGroup(found_person.get().getTeam().getId()), HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<CategoryTransaction> addCategoryTransaction(@RequestHeader("Authorization") String token,
                                                                      @RequestBody @Valid CategoryTransaction categoryTransaction,
                                                                      BindingResult bindingResult) {

        categoryTransactionValidator.validate(categoryTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        if (found_person.get().getId() != categoryTransaction.getPerson().getId())
            throw new DataException("Attempt to change another person's data");

        categoryTransaction.setId(0);
        categoryTransactionsService.save(categoryTransaction);

        return new ResponseEntity<>(categoryTransaction, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<CategoryTransaction> updateCategoryTransaction(@RequestHeader("Authorization") String token,
                                                                         @RequestBody @Valid CategoryTransaction categoryTransaction,
                                                                         BindingResult bindingResult) {

        if (categoryTransactionsService.findById(categoryTransaction.getId()).isEmpty())
            bindingResult.rejectValue("id", "", "CategoryTransaction with this id wasn't found!");

        categoryTransactionValidator.validate(categoryTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        if (found_person.get().getId() != categoryTransaction.getPerson().getId())
            throw new DataException("Attempt to change another person's data");

        categoryTransactionsService.update(categoryTransaction);

        return new ResponseEntity<>(categoryTransaction, HttpStatus.OK);
    }


    @PostMapping("/delete")
    public ResponseEntity<CategoryTransaction> deleteCategoryTransaction(@RequestHeader("Authorization") String token,
                                                                         @RequestBody CategoryTransaction bodyCategoryTransaction) {

        Optional<CategoryTransaction> foundCategoryTransaction = categoryTransactionsService.findById(bodyCategoryTransaction.getId());
        if (foundCategoryTransaction.isEmpty())
            throw new NotFoundException("CategoryTransaction with this id wasn't found!");

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        if (found_person.get().getId() != categoryTransactionsService.findById(bodyCategoryTransaction.getId()).get().getPerson().getId())
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
