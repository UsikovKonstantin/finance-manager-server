package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Category;

public interface CategoriesRepository extends JpaRepository<Category, Integer> {
}
