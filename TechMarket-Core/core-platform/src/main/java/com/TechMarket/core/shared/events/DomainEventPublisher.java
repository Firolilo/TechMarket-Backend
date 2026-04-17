package com.techmarket.core.shared.events;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
