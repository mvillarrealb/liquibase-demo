package org.mvillabe.books;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;

import javax.inject.Singleton;

@Singleton
public class ApplicationListener implements ApplicationEventListener<ServerStartupEvent> {
    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        System.out.println(event.getSource().getEnvironment());
    }
}
