package com.mycafe.mycafe_backend.model;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "bill")
public class Bill implements Serializable {
   
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String uuid;  //bill unique number

    private String name;

    private String email;

    private String contactNumber;

    private String paymentMethod;

    private int totalAmount;

    @Column(columnDefinition = "json")
    private String productDetail;

    private String createdBy;

}
