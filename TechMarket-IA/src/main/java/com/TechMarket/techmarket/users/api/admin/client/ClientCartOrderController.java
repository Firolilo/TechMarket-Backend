package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.users.api.admin.client.request.AddCartItemRequest;
import com.techmarket.techmarket.users.api.admin.client.request.CancelOrderRequest;
import com.techmarket.techmarket.users.api.admin.client.request.CheckoutRequest;
import com.techmarket.techmarket.users.api.admin.client.request.UpdateCartItemRequest;
import com.techmarket.techmarket.users.api.admin.client.response.CancelOrderResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CartItemResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CartResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CheckoutResponse;
import com.techmarket.techmarket.users.api.admin.client.response.MessageResponse;
import com.techmarket.techmarket.users.api.admin.client.response.OrderDetailResponse;
import com.techmarket.techmarket.users.api.admin.client.response.OrderItemResponse;
import com.techmarket.techmarket.users.api.admin.client.response.OrderSummaryResponse;
import com.techmarket.techmarket.users.api.admin.client.response.TrackingResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientCartItemJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientOrderItemJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientOrderJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientCartItemSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientOrderItemSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientOrderSpringDataRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients")
public class ClientCartOrderController {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);

    private final ClientCartItemSpringDataRepository cartItemRepository;
    private final ClientOrderSpringDataRepository orderRepository;
    private final ClientOrderItemSpringDataRepository orderItemRepository;
    private final ListingSpringDataRepository listingRepository;

    public ClientCartOrderController(
            ClientCartItemSpringDataRepository cartItemRepository,
            ClientOrderSpringDataRepository orderRepository,
            ClientOrderItemSpringDataRepository orderItemRepository,
            ListingSpringDataRepository listingRepository) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.listingRepository = listingRepository;
    }

    @GetMapping("/cart")
    public CartResponse cart(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        return toCartResponse(parseUserId(userId));
    }

    @PostMapping("/cart/items")
    public CartResponse addCartItem(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody AddCartItemRequest request) {
        UUID currentUserId = parseUserId(userId);
        UUID listingId = parsePrefixedUuid(request.productoId(), "PROD-");
        ListingJpaEntity listing =
                listingRepository
                        .findById(listingId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Product not found"));

        OffsetDateTime now = OffsetDateTime.now();
        ClientCartItemJpaEntity item =
                cartItemRepository
                        .findByUserIdAndListingId(currentUserId, listingId)
                        .orElseGet(
                                () -> {
                                    ClientCartItemJpaEntity created = new ClientCartItemJpaEntity();
                                    created.setId(UUID.randomUUID());
                                    created.setUserId(currentUserId);
                                    created.setListingId(listingId);
                                    created.setCreatedAt(now);
                                    return created;
                                });
        item.setQuantity(item.getQuantity() + request.cantidad());
        item.setUnitPrice(listing.getBasePrice() == null ? ZERO : listing.getBasePrice());
        item.setUpdatedAt(now);
        cartItemRepository.save(item);
        return toCartResponse(currentUserId);
    }

    @PutMapping("/cart/items/{itemId}")
    public CartResponse updateCartItem(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        UUID currentUserId = parseUserId(userId);
        ClientCartItemJpaEntity item =
                findCartItem(parsePrefixedUuid(itemId, "ITEM-"), currentUserId);
        item.setQuantity(request.cantidad());
        item.setUpdatedAt(OffsetDateTime.now());
        cartItemRepository.save(item);
        return toCartResponse(currentUserId);
    }

    @DeleteMapping("/cart/items/{itemId}")
    public CartResponse deleteCartItem(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String itemId) {
        UUID currentUserId = parseUserId(userId);
        ClientCartItemJpaEntity item =
                findCartItem(parsePrefixedUuid(itemId, "ITEM-"), currentUserId);
        cartItemRepository.delete(item);
        return toCartResponse(currentUserId);
    }

    @DeleteMapping("/cart")
    @Transactional
    public MessageResponse clearCart(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        cartItemRepository.deleteAllByUserId(parseUserId(userId));
        return new MessageResponse("Carrito vaciado correctamente");
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CheckoutResponse checkout(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CheckoutRequest request) {
        UUID currentUserId = parseUserId(userId);
        List<ClientCartItemJpaEntity> cartItems = cartItemRepository.findAllByUserId(currentUserId);
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        OffsetDateTime now = OffsetDateTime.now();
        BigDecimal total = subtotal(cartItems);
        ClientOrderJpaEntity order = new ClientOrderJpaEntity();
        order.setId(UUID.randomUUID());
        order.setUserId(currentUserId);
        order.setShippingAddressId(parsePrefixedUuid(request.direccionEnvioId(), "ADDR-"));
        order.setPaymentMethod(request.metodoPago());
        order.setStatus("PendientePago");
        order.setTotal(total);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        ClientOrderJpaEntity savedOrder = orderRepository.save(order);

        for (ClientCartItemJpaEntity cartItem : cartItems) {
            ClientOrderItemJpaEntity orderItem = new ClientOrderItemJpaEntity();
            orderItem.setId(UUID.randomUUID());
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setListingId(cartItem.getListingId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItemRepository.save(orderItem);
        }
        cartItemRepository.deleteAllByUserId(currentUserId);
        return new CheckoutResponse(
                formatOrderId(savedOrder.getId()), savedOrder.getStatus(), savedOrder.getTotal());
    }

    @GetMapping("/orders")
    public List<OrderSummaryResponse> orders(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toOrderSummary)
                .toList();
    }

    @GetMapping("/orders/{orderId}")
    public OrderDetailResponse order(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String orderId) {
        UUID currentUserId = parseUserId(userId);
        ClientOrderJpaEntity order =
                orderRepository
                        .findByIdAndUserId(parsePrefixedUuid(orderId, "ORD-"), currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Order not found"));
        List<OrderItemResponse> items =
                orderItemRepository.findAllByOrderId(order.getId()).stream()
                        .map(
                                item ->
                                        new OrderItemResponse(
                                                formatProductId(item.getListingId()),
                                                item.getQuantity()))
                        .toList();
        return new OrderDetailResponse(
                formatOrderId(order.getId()),
                order.getStatus(),
                order.getTotal(),
                items,
                new TrackingResponse(null, null));
    }

    @PutMapping("/orders/{orderId}/cancel")
    public CancelOrderResponse cancelOrder(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String orderId,
            @Valid @RequestBody CancelOrderRequest request) {
        UUID currentUserId = parseUserId(userId);
        ClientOrderJpaEntity order =
                orderRepository
                        .findByIdAndUserId(parsePrefixedUuid(orderId, "ORD-"), currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Order not found"));
        if ("Cancelada".equalsIgnoreCase(order.getStatus())) {
            return new CancelOrderResponse("Orden cancelada", order.getStatus());
        }
        order.setStatus("Cancelada");
        order.setUpdatedAt(OffsetDateTime.now());
        orderRepository.save(order);
        return new CancelOrderResponse("Orden cancelada", order.getStatus());
    }

    private CartResponse toCartResponse(UUID userId) {
        List<ClientCartItemJpaEntity> items = cartItemRepository.findAllByUserId(userId);
        return new CartResponse(
                formatCartId(userId),
                subtotal(items),
                items.stream().map(this::toCartItemResponse).toList());
    }

    private CartItemResponse toCartItemResponse(ClientCartItemJpaEntity item) {
        return new CartItemResponse(
                formatItemId(item.getId()),
                formatProductId(item.getListingId()),
                item.getQuantity(),
                item.getUnitPrice());
    }

    private OrderSummaryResponse toOrderSummary(ClientOrderJpaEntity order) {
        return new OrderSummaryResponse(
                formatOrderId(order.getId()),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotal());
    }

    private ClientCartItemJpaEntity findCartItem(UUID itemId, UUID userId) {
        return cartItemRepository
                .findByIdAndUserId(itemId, userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Cart item not found"));
    }

    private BigDecimal subtotal(List<ClientCartItemJpaEntity> items) {
        BigDecimal total = ZERO;
        for (ClientCartItemJpaEntity item : items) {
            BigDecimal unitPrice = item.getUnitPrice() == null ? ZERO : item.getUnitPrice();
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    private UUID parseUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-User-Id is required");
        }
        try {
            return UUID.fromString(userId.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-Id is invalid");
        }
    }

    private UUID parsePrefixedUuid(String value, String prefix) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.regionMatches(true, 0, prefix, 0, prefix.length())) {
            normalized = normalized.substring(prefix.length());
        }
        try {
            return UUID.fromString(normalized);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier is invalid");
        }
    }

    private String formatCartId(UUID userId) {
        return "CART-" + userId;
    }

    private String formatItemId(UUID itemId) {
        return "ITEM-" + itemId;
    }

    private String formatProductId(UUID productId) {
        return "PROD-" + productId;
    }

    private String formatOrderId(UUID orderId) {
        return "ORD-" + orderId;
    }
}
