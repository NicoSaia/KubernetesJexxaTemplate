package io.jexxa.kubernetesjexxatemplate.infrastructure.drivenadapter.messaging;

import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.kubernetesjexxatemplate.domain.book.BookSoldOut;
import io.jexxa.kubernetesjexxatemplate.domainservice.DomainEventSender;

import java.util.Objects;
import java.util.Properties;

import static io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager.getMessageSender;

@SuppressWarnings("unused")
@DrivenAdapter
public class DomainEventSenderImpl implements DomainEventSender
{
    private final MessageSender messageSender;

    public DomainEventSenderImpl(Properties properties)
    {
        messageSender = getMessageSender(DomainEventSender.class, properties);
    }

    @Override
    public void publish(BookSoldOut domainEvent)
    {
        Objects.requireNonNull(domainEvent);
        messageSender
                .send(domainEvent)
                .toTopic("BookStoreTopic")
                .addHeader("Type", domainEvent.getClass().getSimpleName())
                .asJson();
    }
}
