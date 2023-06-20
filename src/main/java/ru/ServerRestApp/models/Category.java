package ru.ServerRestApp.models;

import javax.persistence.*;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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

/*
    @OneToMany(mappedBy = "category")
    private List<CategoryTransaction> categoryTransactions;
*/

    public Category() {
    }

    public Category(String name, List<CategoryTransaction> categoryTransactions) {
        this.name = name;
        //this.categoryTransactions = categoryTransactions;
    }

    public Category(int id, String name, List<CategoryTransaction> categoryTransactions) {
        this.id = id;
        this.name = name;
        //this.categoryTransactions = categoryTransactions;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
/*
    public List<CategoryTransaction> getCategoryTransactions() {
        return categoryTransactions;
    }

    public void setCategoryTransactions(List<CategoryTransaction> categoryTransactions) {
        this.categoryTransactions = categoryTransactions;
    }*/
}
