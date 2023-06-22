package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.repositories.CategoriesRepository;
import ru.ServerRestApp.repositories.CategoryTransactionsRepository;
import ru.ServerRestApp.repositories.PeopleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryTransactionsService {

    private final CategoryTransactionsRepository categoryTransactionsRepository;
    private final CategoriesRepository categoriesRepository;
    private final PeopleRepository peopleRepository;
    @Autowired
    public CategoryTransactionsService(CategoryTransactionsRepository categoryTransactionsRepository, CategoriesRepository categoriesRepository, PeopleRepository peopleRepository) {
        this.categoryTransactionsRepository = categoryTransactionsRepository;
        this.categoriesRepository = categoriesRepository;
        this.peopleRepository = peopleRepository;
    }


    @Transactional(readOnly = true)
    public Optional<CategoryTransaction> findById(int id) {
        return categoryTransactionsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CategoryTransaction> findAll() {
        return categoryTransactionsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CategoryTransaction> findByPersonId(int id) {
        return categoryTransactionsRepository.findByPersonId(id);
    }

    @Transactional(readOnly = true)
    public List<CategoryTransaction> findByCategoryId(int id) {
        return categoryTransactionsRepository.findByCategoryId(id);
    }

    @Transactional
    public void save(CategoryTransaction categoryTransaction) {
        if (categoryTransaction.getPerson() != null)
            categoryTransaction.setPerson(peopleRepository.findById(categoryTransaction.getPerson().getId()).get());
        if (categoryTransaction.getCategory() != null)
            categoryTransaction.setCategory(categoriesRepository.findById(categoryTransaction.getCategory().getId()).get());

        int id = categoryTransactionsRepository.save(categoryTransaction).getId();
        categoryTransaction.setId(id);
    }

    @Transactional
    public void update(CategoryTransaction categoryTransaction) {
        if (categoryTransaction.getPerson() != null)
            categoryTransaction.setPerson(peopleRepository.findById(categoryTransaction.getPerson().getId()).get());
        if (categoryTransaction.getCategory() != null)
            categoryTransaction.setCategory(categoriesRepository.findById(categoryTransaction.getCategory().getId()).get());

        int id = categoryTransactionsRepository.save(categoryTransaction).getId();
        categoryTransaction.setId(id);
    }

    @Transactional
    public void delete(int id) {
        categoryTransactionsRepository.deleteById(id);
    }
}
