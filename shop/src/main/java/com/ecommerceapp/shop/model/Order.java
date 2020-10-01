package com.ecommerceapp.shop.model;


import com.ecommerceapp.inventory.model.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
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

    private OrderStatus status;

    @CreatedDate
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date(); // initialize created date;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate = new Date(); // initialize updated date;

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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Order(String id, ArrayList<Product> products) {
        this.id = id;
        this.products = products;
        this.status = OrderStatus.CREATED;
    }

    public Order(List<Product> products) {
        this.products = products;
        this.status = OrderStatus.CREATED;
    }

    public Order() {
        this.products = new ArrayList<>();
        this.status = OrderStatus.CREATED;
    }

}
