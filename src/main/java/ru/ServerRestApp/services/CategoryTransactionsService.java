package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.repositories.CategoriesRepository;
import ru.ServerRestApp.repositories.CategoryTransactionsRepository;
import ru.ServerRestApp.repositories.PeopleRepository;
import ru.ServerRestApp.util.CategoryTransactionGroup;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<CategoryTransaction> findByPersonTeamId(int id) {
        return categoryTransactionsRepository.findByPersonTeamId(id);
    }

    @Transactional(readOnly = true)
    public List<CategoryTransaction> findByCategoryId(int id) {
        return categoryTransactionsRepository.findByCategoryId(id);
    }

    @Transactional(readOnly = true)
    public List<CategoryTransactionGroup> getPositiveTransactionsByCategoryForPerson(int personId) {

        List<Object[]> results = categoryTransactionsRepository.getPositiveTransactionsByCategoryForPerson(personId);

        return results.stream()
                .map(obj -> new CategoryTransactionGroup((String) obj[0], (double) obj[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryTransactionGroup> getNegativeTransactionsByCategoryForPerson(int personId) {

        List<Object[]> results = categoryTransactionsRepository.getNegativeTransactionsByCategoryForPerson(personId);

        return results.stream()
                .map(obj -> new CategoryTransactionGroup((String) obj[0], (double) obj[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryTransactionGroup> getPositiveTransactionsByCategoryForGroup(int groupId) {

        List<Object[]> results = categoryTransactionsRepository.getPositiveTransactionsByCategoryForGroup(groupId);

        return results.stream()
                .map(obj -> new CategoryTransactionGroup((String) obj[0], (double) obj[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryTransactionGroup> getNegativeTransactionsByCategoryForGroup(int groupId) {

        List<Object[]> results = categoryTransactionsRepository.getNegativeTransactionsByCategoryForGroup(groupId);

        return results.stream()
                .map(obj -> new CategoryTransactionGroup((String) obj[0], (double) obj[1]))
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public List<CategoryTransaction> findByPersonIdForMonth(int id, int year, int month) {
        return categoryTransactionsRepository.findByPersonIdForMonth(id, month, year);
    }

    @Transactional(readOnly = true)
    public List<CategoryTransaction> findByPersonTeamIdForMonth(int id, int year, int month) {
        return categoryTransactionsRepository.findByPersonTeamIdForMonth(id, month, year);
    }

    @Transactional(readOnly = true)
    public List<CategoryTransactionGroup> getPositiveTransactionsByCategoryForPersonForMonth(int personId, int month, int year) {

        List<Object[]> results = categoryTransactionsRepository.getPositiveTransactionsByCategoryForPersonForMonth(personId, month, year);

        return results.stream()
                .map(obj -> new CategoryTransactionGroup((String) obj[0], (double) obj[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryTransactionGroup> getNegativeTransactionsByCategoryForPersonForMonth(int personId, int month, int year) {

        List<Object[]> results = categoryTransactionsRepository.getNegativeTransactionsByCategoryForPersonForMonth(personId, month, year);

        return results.stream()
                .map(obj -> new CategoryTransactionGroup((String) obj[0], (double) obj[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryTransactionGroup> getPositiveTransactionsByCategoryForGroupForMonth(int groupId, int month, int year) {

        List<Object[]> results = categoryTransactionsRepository.getPositiveTransactionsByCategoryForGroupForMonth(groupId, month, year);

        return results.stream()
                .map(obj -> new CategoryTransactionGroup((String) obj[0], (double) obj[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryTransactionGroup> getNegativeTransactionsByCategoryForGroupForMonth(int groupId, int month, int year) {

        List<Object[]> results = categoryTransactionsRepository.getNegativeTransactionsByCategoryForGroupForMonth(groupId, month, year);

        return results.stream()
                .map(obj -> new CategoryTransactionGroup((String) obj[0], (double) obj[1]))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<CategoryTransaction> findNLastPositiveTransactionsForPerson(int id, int n) {
        Pageable pageable = PageRequest.of(0, n);
        return categoryTransactionsRepository.findNLastPositiveTransactionsForPerson(id, pageable);
    }

    @Transactional(readOnly = true)
    public List<CategoryTransaction> findNLastNegativeTransactionsForPerson(int id, int n) {
        Pageable pageable = PageRequest.of(0, n);
        return categoryTransactionsRepository.findNLastNegativeTransactionsForPerson(id, pageable);
    }


    @Transactional
    public void save(CategoryTransaction categoryTransaction) {
        if (categoryTransaction.getPerson() != null)
            categoryTransaction.setPerson(peopleRepository.findById(categoryTransaction.getPerson().getId()).get());
        if (categoryTransaction.getCategory() != null)
            categoryTransaction.setCategory(categoriesRepository.findById(categoryTransaction.getCategory().getId()).get());

        if (categoryTransaction.getPerson().getBalance() + categoryTransaction.getAmount() < 0)
            throw new DataException("Balance must be positive!");
        categoryTransaction.getPerson().setBalance(categoryTransaction.getPerson().getBalance() + categoryTransaction.getAmount());

        int id = categoryTransactionsRepository.save(categoryTransaction).getId();
        categoryTransaction.setId(id);
    }

    @Transactional
    public void update(CategoryTransaction categoryTransaction) {
        if (categoryTransaction.getPerson() != null)
            categoryTransaction.setPerson(peopleRepository.findById(categoryTransaction.getPerson().getId()).get());
        if (categoryTransaction.getCategory() != null)
            categoryTransaction.setCategory(categoriesRepository.findById(categoryTransaction.getCategory().getId()).get());

        CategoryTransaction foundCategoryTransaction = categoryTransactionsRepository.findById(categoryTransaction.getId()).get();
        if (categoryTransaction.getPerson().getBalance() - foundCategoryTransaction.getAmount() + categoryTransaction.getAmount() < 0)
            throw new DataException("Balance must be positive!");
        categoryTransaction.getPerson().setBalance(categoryTransaction.getPerson().getBalance() - foundCategoryTransaction.getAmount() + categoryTransaction.getAmount());

        int id = categoryTransactionsRepository.save(categoryTransaction).getId();
        categoryTransaction.setId(id);
    }

    @Transactional
    public void delete(int id) {
        CategoryTransaction categoryTransaction = categoryTransactionsRepository.findById(id).get();
        if (categoryTransaction.getPerson() != null)
            categoryTransaction.setPerson(peopleRepository.findById(categoryTransaction.getPerson().getId()).get());
        if (categoryTransaction.getCategory() != null)
            categoryTransaction.setCategory(categoriesRepository.findById(categoryTransaction.getCategory().getId()).get());

        if (categoryTransaction.getPerson().getBalance() - categoryTransaction.getAmount() < 0)
            throw new DataException("Balance must be positive!");
        categoryTransaction.getPerson().setBalance(categoryTransaction.getPerson().getBalance() - categoryTransaction.getAmount());

        categoryTransactionsRepository.deleteById(id);
    }
}