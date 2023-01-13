package io.jexxa.jexxatemplate;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.jexxatemplate.applicationservice.BookStoreService;
import io.jexxa.jexxatemplate.domainservice.DomainEventService;
import io.jexxa.jexxatemplate.domainservice.ReferenceLibrary;

public final class JexxaTemplate
{
    public static void main(String[] args)
    {
        var jexxaMain = new JexxaMain(JexxaTemplate.class);

        jexxaMain
                .bootstrap(ReferenceLibrary.class).and()       // Bootstrap latest book via ReferenceLibrary
                .bootstrap(DomainEventService.class).and()     // DomainEventService to forward DomainEvents to a message bus

                .bind(RESTfulRPCAdapter.class).to(BookStoreService.class)        // Provide REST access to BookStoreService
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext()) // Provide REST access to BoundedContext

                .run(); // Finally run the application
    }
}
