package io.jexxa.jexxatemplate.domainservice;


import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.jexxatemplate.domain.book.BookSoldOut;

@InfrastructureService
public interface DomainEventSender
{
    void publish(BookSoldOut domainEvent);
}
