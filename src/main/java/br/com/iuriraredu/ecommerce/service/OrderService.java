package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.*;
import br.com.iuriraredu.ecommerce.entity.enums.OrderStatus;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.repository.ClientRepository;
import br.com.iuriraredu.ecommerce.repository.OrderRepository;
import br.com.iuriraredu.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    public Order create(Order order) {
        Client client = clientRepository.findById(order.getClient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found!"));

        order.setClient(client);
        order.setClientDocumentSnapshot(client.getCpf());

        Address deliveryAddress = client.getAddresses().stream()
                .filter(addr -> addr.getId().equals(order.getDeliveryAddressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address not found for this client!"));

        String addressSnapshot = String.format(
                "%s, %s - %s, CEP: %s%s",
                deliveryAddress.getStreet(),
                deliveryAddress.getNumber(),
                deliveryAddress.getNeighborhood(),
                deliveryAddress.getCep(),
                deliveryAddress.getComplement() != null
                        ? String.format(" (%s)", deliveryAddress.getComplement())
                        : ""
        );
        order.setDeliveryAddressSnapshot(addressSnapshot);

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProduct().getId()));
            item.setProduct(product);
            item.setSoldPrice(product.getPrice());
            item.setOrder(order);
        }

        order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        return orderRepository.save(order);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public Order updateStatus(Long id, OrderStatus status) throws ResourceNotFoundException {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}