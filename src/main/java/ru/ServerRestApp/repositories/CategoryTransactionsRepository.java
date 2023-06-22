package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.PersonTransaction;

import java.util.List;

public interface CategoryTransactionsRepository extends JpaRepository<CategoryTransaction, Integer> {
    List<CategoryTransaction> findByPersonId(int personId);
    List<CategoryTransaction> findByCategoryId(int categoryId);
}
