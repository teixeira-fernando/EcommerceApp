package service;

import com.ecommerceapp.shop.repository.OrderRepository;
import model.OrderShipment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.OrderShipmentRepository;

@Service
public class ShipmentService {

    private static final Logger logger = LogManager.getLogger(ShipmentService.class);

    @Autowired private OrderShipmentRepository repository;

    public OrderShipment createOrderShipment(OrderShipment orderShipment){


        return repository.save(orderShipment);
    }
}
