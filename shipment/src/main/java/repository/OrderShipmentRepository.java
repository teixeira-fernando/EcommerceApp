package repository;

import model.OrderShipment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderShipmentRepository extends MongoRepository<OrderShipment, String> {
}
