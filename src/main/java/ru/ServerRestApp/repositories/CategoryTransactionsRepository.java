package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.CategoryTransaction;

public interface CategoryTransactionsRepository extends JpaRepository<CategoryTransaction, Integer> {
}
