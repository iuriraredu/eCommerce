package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.OrderRequestDTO;
import br.com.iuriraredu.ecommerce.dto.OrderResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Address;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.entity.Order;
import br.com.iuriraredu.ecommerce.entity.OrderItem;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.entity.enums.OrderStatus;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.repository.ClientRepository;
import br.com.iuriraredu.ecommerce.repository.OrderRepository;
import br.com.iuriraredu.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO dto) {
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found!"));

        Address deliveryAddress = client.getAddresses().stream()
                .filter(addr -> addr.getId().equals(dto.deliveryAddressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address not found for this client!"));

        Order order = new Order();
        order.setClient(client);
        order.setClientDocumentSnapshot(client.getCpf());
        order.setDeliveryAddressSnapshot(buildAddressSnapshot(deliveryAddress));
        order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);

        List<OrderItem> items = dto.items().stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.productId()));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDto.quantity());
            item.setSoldPrice(product.getPrice());
            item.setOrder(order);
            return item;
        }).toList();
        order.setItems(items);

        return OrderResponseDTO.fromEntity(orderRepository.save(order));
    }

    public List<OrderResponseDTO> getAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDTO::fromEntity)
                .toList();
    }

    public OrderResponseDTO findById(Long id) {
        return OrderResponseDTO.fromEntity(findEntityById(id));
    }

    @Transactional
    public OrderResponseDTO updateStatus(Long id, OrderStatus status) {
        Order order = findEntityById(id);
        order.setStatus(status);
        return OrderResponseDTO.fromEntity(orderRepository.save(order));
    }

    private Order findEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private String buildAddressSnapshot(Address address) {
        return String.format(
                "%s, %s - %s, CEP: %s%s",
                address.getStreet(),
                address.getNumber(),
                address.getNeighborhood(),
                address.getCep(),
                address.getComplement() != null
                        ? String.format(" (%s)", address.getComplement())
                        : ""
        );
    }
}