package ru.ServerRestApp.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Invitation")
public class Invitation {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_from_id", referencedColumnName = "id")
    private Person person_from;

    @ManyToOne
    @JoinColumn(name = "person_to_id", referencedColumnName = "id")
    private Person person_to;
}
