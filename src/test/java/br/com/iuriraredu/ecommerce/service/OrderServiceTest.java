package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Address;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.entity.Order;
import br.com.iuriraredu.ecommerce.entity.OrderItem;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.repository.ClientRepository;
import br.com.iuriraredu.ecommerce.repository.OrderRepository;
import br.com.iuriraredu.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static br.com.iuriraredu.ecommerce.entity.enums.OrderStatus.PAID;
import static br.com.iuriraredu.ecommerce.entity.enums.OrderStatus.WAITING_FOR_PAYMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should create order successfully with snapshot and item prices")
    void createOrderSuccess() {
        // Arrange
        Long clientId = 1L;
        Long addressId = 10L;
        Long productId = 100L;

        Client client = new Client();
        client.setId(clientId);
        client.setCpf("123.456.789-00");

        Address address = new Address();
        address.setId(addressId);
        address.setStreet("Av. Paulista");
        address.setNumber("1000");
        address.setNeighborhood("Bela Vista");
        address.setCep("01310-100");
        client.setAddresses(List.of(address));

        Product product = new Product();
        product.setId(productId);
        product.setPrice(BigDecimal.valueOf(150.00));

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setClient(client);
        order.setDeliveryAddressId(addressId);
        order.setItems(List.of(item));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order createdOrder = orderService.create(order);

        // Assert
        assertNotNull(createdOrder);
        assertEquals("123.456.789-00", createdOrder.getClientDocumentSnapshot());
        assertTrue(createdOrder.getDeliveryAddressSnapshot().contains("Av. Paulista, 1000"));
        assertEquals(BigDecimal.valueOf(150.00), item.getSoldPrice());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when client is not found during order creation")
    void createOrderClientNotFound() {
        // Arrange
        Long clientId = 99L;
        Client client = new Client();
        client.setId(clientId);

        Order order = new Order();
        order.setClient(client);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.create(order);
        });

        assertEquals("Client not found!", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return all orders")
    void getAllOrdersSuccess() {
        // Arrange
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> result = orderService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update order status successfully when order exists")
    void updateOrderStatusSuccess() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(WAITING_FOR_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderService.updateStatus(orderId, PAID);

        // Assert
        assertNotNull(result);
        assertEquals(PAID, result.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to update status of non-existent order")
    void updateOrderStatusNotFound() {
        // Arrange
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateStatus(orderId, PAID)
        );

        assertEquals("Order not found with id: 99", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }
}