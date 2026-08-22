package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.OrderRequestDTO;
import br.com.iuriraredu.ecommerce.dto.OrderResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Address;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.entity.Order;
import br.com.iuriraredu.ecommerce.entity.OrderItem;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.entity.enums.OrderStatus;
import br.com.iuriraredu.ecommerce.exception.BusinessException;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.mapper.OrderMapper;
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
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO create(final OrderRequestDTO dto) {
        final Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found!"));

        final Address deliveryAddress = client.getAddresses().stream()
                .filter(addr -> addr.getId().equals(dto.deliveryAddressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address not found for this client!"));

        final Order order = new Order();
        order.setClient(client);
        order.setClientDocumentSnapshot(client.getCpf());
        order.setDeliveryAddressSnapshot(buildAddressSnapshot(deliveryAddress));
        order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);

        final List<OrderItem> items = dto.items().stream().map(itemDto -> {
            final Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.productId()));

            if (product.getStockQuantity() < itemDto.quantity()) {
                throw new BusinessException(
                        "Insufficient stock for product '" + product.getName() + "'. Available: "
                                + product.getStockQuantity() + ", requested: " + itemDto.quantity()
                );
            }

            // Decrement stock right here, inside the order's transaction: if any later item
            // fails (product not found or out of stock), the method's @Transactional rolls
            // everything back, including the decrements already made on earlier items.
            product.setStockQuantity(product.getStockQuantity() - itemDto.quantity());
            productRepository.save(product);

            final OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDto.quantity());
            item.setSoldPrice(product.getPrice());
            item.setOrder(order);
            return item;
        }).toList();
        order.setItems(items);

        return orderMapper.toResponseDTO(orderRepository.save(order));
    }

    public List<OrderResponseDTO> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    public OrderResponseDTO findById(final Long id) {
        return orderMapper.toResponseDTO(findEntityById(id));
    }

    @Transactional
    public OrderResponseDTO updateStatus(final Long id, final OrderStatus status) {
        final Order order = findEntityById(id);
        order.setStatus(status);
        return orderMapper.toResponseDTO(orderRepository.save(order));
    }

    private Order findEntityById(final Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private String buildAddressSnapshot(final Address address) {
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
