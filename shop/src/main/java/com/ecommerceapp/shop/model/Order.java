package com.ecommerceapp.shop.model;


import com.ecommerceapp.inventory.model.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Order")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private List<Product> products;

    @CreatedDate
    private Date createDate;

    public String getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Order(String id, ArrayList<Product> products) {
        this.id = id;
        this.products = products;
    }

    public Order(List<Product> products) {
        this.products = products;
    }

    public Order() {
        this.products = new ArrayList<>();
    }
}
