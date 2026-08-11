package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.*;
import br.com.iuriraredu.ecommerce.entity.enums.OrderStatus;
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
                .orElseThrow(() -> new RuntimeException("Client not found!"));

        order.setClient(client);
        order.setClientDocumentSnapshot(client.getCpf());

        if (client.getAddresses() != null && !client.getAddresses().isEmpty()) {
            Address chosenAddress = client.getAddresses().stream()
                    .filter(addr -> addr.getId().equals(order.getDeliveryAddressId()))
                    .findFirst()
                    .orElse(client.getAddresses().getFirst());

            String snapshot = String.format(
                    "%s, %s - %s, CEP: %s%s",
                    chosenAddress.getStreet(),
                    chosenAddress.getNumber(),
                    chosenAddress.getNeighborhood(),
                    chosenAddress.getCep(),
                    chosenAddress.getComplement() != null
                            ? String.format(" (%s)", chosenAddress.getComplement())
                            : ""
            );

            order.setDeliveryAddressSnapshot(snapshot);
        } else {
            order.setDeliveryAddressSnapshot("Client without registered address");
        }

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found!"));

            item.setSoldPrice(product.getPrice());
            item.setProduct(product);
            item.setOrder(order);
        }

        return orderRepository.save(order);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> updateStatus(Long id, OrderStatus newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(id);

        if (orderOpt.isEmpty()) return Optional.empty();

        Order order = orderOpt.get();
        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);
        return Optional.of(savedOrder);
    }
}