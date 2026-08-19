package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.OrderItemRequestDTO;
import br.com.iuriraredu.ecommerce.dto.OrderRequestDTO;
import br.com.iuriraredu.ecommerce.dto.OrderResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Address;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.entity.Order;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.repository.ClientRepository;
import br.com.iuriraredu.ecommerce.repository.OrderRepository;
import br.com.iuriraredu.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

        OrderRequestDTO dto = new OrderRequestDTO(clientId, addressId, List.of(new OrderItemRequestDTO(productId, 2)));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponseDTO createdOrder = orderService.create(dto);

        // Assert
        assertNotNull(createdOrder);
        assertEquals("123.456.789-00", createdOrder.clientDocumentSnapshot());
        assertTrue(createdOrder.deliveryAddressSnapshot().contains("Av. Paulista, 1000"));
        assertEquals(BigDecimal.valueOf(150.00), createdOrder.items().get(0).soldPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when client is not found during order creation")
    void createOrderClientNotFound() {
        // Arrange
        Long clientId = 99L;
        OrderRequestDTO dto = new OrderRequestDTO(clientId, 1L, List.of(new OrderItemRequestDTO(1L, 1)));

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.create(dto);
        });

        assertEquals("Client not found!", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return all orders")
    void getAllOrdersSuccess() {
        // Arrange
        Order order1 = new Order();
        order1.setClient(new Client());
        order1.setItems(List.of());
        Order order2 = new Order();
        order2.setClient(new Client());
        order2.setItems(List.of());

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // Act
        List<OrderResponseDTO> result = orderService.getAll();

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
        order.setClient(new Client());
        order.setItems(List.of());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponseDTO result = orderService.updateStatus(orderId, PAID);

        // Assert
        assertNotNull(result);
        assertEquals(PAID, result.status());
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