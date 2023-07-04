package ru.ServerRestApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "Category_transaction")
public class CategoryTransaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "amount")
    @Min(value = -100000)
    @Max(value = 100000)
    private double amount;

    @Column(name = "created_at")
    private Date createdAt;

    @Size(max = 50, message = "Описание транзакции должно быть до 50 символов длиной")
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;


    public CategoryTransaction() {
    }

    public CategoryTransaction(double amount, Date created_at, String description, Person person, Category category) {
        this.amount = amount;
        this.createdAt = created_at;
        this.description = description;
        this.person = person;
        this.category = category;
    }

    public CategoryTransaction(int id, double amount, Date created_at, String description, Person person, Category category) {
        this.id = id;
        this.amount = amount;
        this.createdAt = created_at;
        this.description = description;
        this.person = person;
        this.category = category;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "CategoryTransaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", created_at=" + createdAt +
                ", description='" + description + '\'' +
                ", person=" + person +
                ", category=" + category +
                '}';
    }
}
