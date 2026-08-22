package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.OrderItemRequestDTO;
import br.com.iuriraredu.ecommerce.dto.OrderItemResponseDTO;
import br.com.iuriraredu.ecommerce.dto.OrderRequestDTO;
import br.com.iuriraredu.ecommerce.dto.OrderResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Address;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.entity.Order;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.exception.BusinessException;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.mapper.OrderMapper;
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

    // OrderService now delegates Order -> OrderResponseDTO conversion to OrderMapper (MapStruct).
    // We stub it with an Answer that mirrors the real mapper's field-by-field behavior, so tests
    // can still assert on business-logic outcomes (stock decrement, snapshot content, sold price)
    // through the response DTO, exactly as before — only the conversion mechanism changed.
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private void stubMapperToMirrorRealBehavior() {
        when(orderMapper.toResponseDTO(any(Order.class))).thenAnswer(invocation -> {
            final Order order = invocation.getArgument(0);
            final List<OrderItemResponseDTO> items = order.getItems() == null ? List.of() : order.getItems().stream()
                    .map(item -> new OrderItemResponseDTO(item.getId(), item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getSoldPrice()))
                    .toList();
            return new OrderResponseDTO(
                    order.getId(), order.getOrderDate(), order.getStatus(), order.getClient().getId(),
                    order.getClientDocumentSnapshot(), order.getDeliveryAddressSnapshot(), items
            );
        });
    }

    @Test
    @DisplayName("Should create order successfully with snapshot and item prices")
    void createOrderSuccess() {
        // Arrange
        final Long clientId = 1L;
        final Long addressId = 10L;
        final Long productId = 100L;

        final Client client = new Client();
        client.setId(clientId);
        client.setCpf("123.456.789-00");

        final Address address = new Address();
        address.setId(addressId);
        address.setStreet("Av. Paulista");
        address.setNumber("1000");
        address.setNeighborhood("Bela Vista");
        address.setCep("01310-100");
        client.setAddresses(List.of(address));

        final Product product = new Product();
        product.setId(productId);
        product.setPrice(BigDecimal.valueOf(150.00));
        product.setStockQuantity(10);

        final OrderRequestDTO dto = new OrderRequestDTO(clientId, addressId, List.of(new OrderItemRequestDTO(productId, 2)));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        stubMapperToMirrorRealBehavior();

        // Act
        final OrderResponseDTO createdOrder = orderService.create(dto);

        // Assert
        assertNotNull(createdOrder);
        assertEquals("123.456.789-00", createdOrder.clientDocumentSnapshot());
        assertTrue(createdOrder.deliveryAddressSnapshot().contains("Av. Paulista, 1000"));
        assertEquals(BigDecimal.valueOf(150.00), createdOrder.items().get(0).soldPrice());
        assertEquals(8, product.getStockQuantity()); // 10 - 2 sold
        verify(productRepository, times(1)).save(product);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when stock is insufficient for requested quantity")
    void createOrderInsufficientStock() {
        // Arrange
        final Long clientId = 1L;
        final Long addressId = 10L;
        final Long productId = 100L;

        final Client client = new Client();
        client.setId(clientId);
        client.setCpf("123.456.789-00");

        final Address address = new Address();
        address.setId(addressId);
        address.setStreet("Av. Paulista");
        address.setNumber("1000");
        address.setNeighborhood("Bela Vista");
        address.setCep("01310-100");
        client.setAddresses(List.of(address));

        final Product product = new Product();
        product.setId(productId);
        product.setName("Mechanical keyboard");
        product.setPrice(BigDecimal.valueOf(150.00));
        product.setStockQuantity(1); // only 1 in stock

        final OrderRequestDTO dto = new OrderRequestDTO(clientId, addressId, List.of(new OrderItemRequestDTO(productId, 5)));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        final BusinessException exception = assertThrows(BusinessException.class, () -> orderService.create(dto));

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when client is not found during order creation")
    void createOrderClientNotFound() {
        // Arrange
        final Long clientId = 99L;
        final OrderRequestDTO dto = new OrderRequestDTO(clientId, 1L, List.of(new OrderItemRequestDTO(1L, 1)));

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                orderService.create(dto));

        assertEquals("Client not found!", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return all orders")
    void getAllOrdersSuccess() {
        // Arrange
        final Order order1 = new Order();
        order1.setClient(new Client());
        order1.setItems(List.of());
        final Order order2 = new Order();
        order2.setClient(new Client());
        order2.setItems(List.of());

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));
        stubMapperToMirrorRealBehavior();

        // Act
        final List<OrderResponseDTO> result = orderService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update order status successfully when order exists")
    void updateOrderStatusSuccess() {
        // Arrange
        final Long orderId = 1L;
        final Order order = new Order();
        order.setId(orderId);
        order.setStatus(WAITING_FOR_PAYMENT);
        order.setClient(new Client());
        order.setItems(List.of());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        stubMapperToMirrorRealBehavior();

        // Act
        final OrderResponseDTO result = orderService.updateStatus(orderId, PAID);

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
        final Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateStatus(orderId, PAID)
        );

        assertEquals("Order not found with id: 99", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }
}
