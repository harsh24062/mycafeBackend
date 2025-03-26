package com.mycafe.mycafe_backend.model;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "product")
public class Product implements Serializable {
        
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    // What @ManyToOne Does
    // Purpose: Establishes a many-to-one relationship between entities.
    // It means that many instances of the current entity (e.g., Product) can reference one shared instance of another entity (e.g., Category).
    // Example: Many Product entities can belong to one Category.

    // What Does fetch = FetchType.LAZY Do in @ManyToOne?
    // When you use @ManyToOne(fetch = FetchType.LAZY), you're telling JPA/Hibernate:
    // "Don’t load the related entity (Category) immediately—only load it when I actually use it."
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;

    private String description;

    private int price;

    private String status;

}
