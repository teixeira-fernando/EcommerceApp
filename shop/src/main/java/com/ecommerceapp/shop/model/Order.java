package com.ecommerceapp.shop.model;


import com.ecommerceapp.inventory.model.Product;
import lombok.Getter;
import lombok.Setter;
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
    @Getter
    private String id;

    @Getter
    @Setter
    private List<Product> products;

    @Getter
    @Setter
    private OrderStatus status;

    @CreatedDate
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    private Date createDate = new Date(); // initialize created date;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    private Date updateDate = new Date(); // initialize updated date;

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
