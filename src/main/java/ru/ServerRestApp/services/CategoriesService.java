package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.Category;
import ru.ServerRestApp.repositories.CategoriesRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }


    @Transactional(readOnly = true)
    public Optional<Category> findById(int id) {
        return categoriesRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoriesRepository.findAll();
    }
}
