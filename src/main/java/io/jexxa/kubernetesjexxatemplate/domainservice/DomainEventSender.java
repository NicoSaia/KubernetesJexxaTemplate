package io.jexxa.kubernetesjexxatemplate.domainservice;


import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.kubernetesjexxatemplate.domain.book.BookSoldOut;

@InfrastructureService
public interface DomainEventSender
{
    void publish(BookSoldOut domainEvent);
}
