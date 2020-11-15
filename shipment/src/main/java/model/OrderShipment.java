package model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import com.ecommerceapp.shop.model.Order;

@Document(collection = "OrderShipment")
@Entity
public class OrderShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @ApiModelProperty(required = false, notes = "Auto generated id")
    private String id;

    @ApiModelProperty(required = true, notes = "Order related to this shipment")
    @Getter
    @Setter
    private Order order;

    @CreatedDate
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @ApiModelProperty(required = false, notes = "Auto generated date for the shipment")
    private Date createDate = new Date(); // initialize created date;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @ApiModelProperty(required = false, notes = "Auto generated date for the shipment")
    private Date updateDate = new Date(); // initialize updated date;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @ApiModelProperty(required = false, notes = "Auto generated date for the shipment")
    private Date estimatedDeliveryDate = new Date();

    @Getter
    @Setter
    @ApiModelProperty(required = false, notes = "Delivery location for this shipment")
    private Location deliveryLocation;

    public OrderShipment(String id, Order order, Date estimatedDeliveryDate, Location deliveryLocation) {
        this.id = id;
        this.order = order;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.deliveryLocation = deliveryLocation;
    }

    public OrderShipment(Order order, Date estimatedDeliveryDate, Location deliveryLocation) {
        this.order = order;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.deliveryLocation = deliveryLocation;
    }
}
