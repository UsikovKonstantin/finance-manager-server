package ru.ServerRestApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "Category")
public class Category {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Название категории не должно быть пустым")
    @Size(min = 1, max = 50, message = "Название категории должно быть от 1 до 50 символов длиной")
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<CategoryTransaction> categoryTransactions;
}
