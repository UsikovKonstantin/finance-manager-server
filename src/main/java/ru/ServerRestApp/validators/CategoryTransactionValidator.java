package ru.ServerRestApp.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.services.CategoriesService;
import ru.ServerRestApp.services.CategoryTransactionsService;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.util.DataException;

@Component
public class CategoryTransactionValidator implements Validator {

    private final CategoryTransactionsService categoryTransactionsService;
    private final PeopleService peopleService;
    private final CategoriesService categoriesService;

    @Autowired
    public CategoryTransactionValidator(CategoryTransactionsService categoryTransactionsService, PeopleService peopleService, CategoriesService categoriesService) {
        this.categoryTransactionsService = categoryTransactionsService;
        this.peopleService = peopleService;
        this.categoriesService = categoriesService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CategoryTransaction.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        CategoryTransaction categoryTransaction = (CategoryTransaction)target;

        if (categoryTransaction.getPerson() == null)
            errors.rejectValue("person", "", "Person must not be null!");
        else if (peopleService.findById(categoryTransaction.getPerson().getId()).isEmpty())
            errors.rejectValue("person", "", "Person with this id wasn't found!");

        if (categoryTransaction.getCategory() == null)
            errors.rejectValue("category", "", "Category must not be null!");
        else if (categoriesService.findById(categoryTransaction.getCategory().getId()).isEmpty())
            errors.rejectValue("category", "", "Category with this id wasn't found!");

        if (categoryTransaction.getCreated_at() == null)
            errors.rejectValue("created_at", "", "Created_at must not be null!");
    }
}
