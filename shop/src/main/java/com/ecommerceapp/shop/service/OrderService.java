package com.ecommerceapp.shop.service;

import com.ecommerceapp.shop.dto.request.ChangeStockDto;
import com.ecommerceapp.shop.dto.request.StockOperation;
import com.ecommerceapp.shop.exceptions.EmptyOrderException;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.repository.OrderRepository;
import com.ecommerceapp.shop.service.kafka.MessageProducer;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private static final Logger logger = LogManager.getLogger(OrderService.class);

  @Autowired private OrderRepository repository;

  @Autowired private InventoryClient inventoryClient;

  @Autowired private MessageProducer messageProducer;

  public Order findById(String id) {
    Optional<Order> order = this.repository.findById(id);
    if (order.isEmpty()) {
      throw new NoSuchElementException();
    }

    return order.get();
  }

  public List<Order> findAll() {
    return this.repository.findAll();
  }

  public Order createOrder(Order order) throws EmptyOrderException {
    if (order.getProducts().isEmpty()) {
      throw new EmptyOrderException();
    }

    order
        .getProducts()
        .forEach(
            product -> {
              try {
                if (!inventoryClient.checkIfProductExists(product.getId())) {
                  throw new InvalidParameterException(
                      "Failed to search for the product in the inventory");
                }
                if (!inventoryClient.checkIfProductHaveEnoughStock(
                    product.getId(), product.getQuantity())) {
                  throw new InvalidParameterException(
                      "There is not enough stock for this product: " + product.getName());
                }
                inventoryClient.updateStock(
                    product.getId(),
                    new ChangeStockDto(product.getQuantity(), StockOperation.DECREMENT));
              } catch (URISyntaxException e) {
                e.printStackTrace();
                logger.error(e.getStackTrace());
              }
            });

    Order savedOrder = this.repository.save(order);

    // send this to shipment module
    messageProducer.sendOrderToShipment(savedOrder);
    messageProducer.sendString("Sending a message with Kafka");

    return savedOrder;
  }

  public Order updateOrder(Order order) {
    return this.repository.save(order);
  }
}
