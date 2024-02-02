package org.noear.solon.core.event;

public interface EventListener<Event> {
   void onEvent(Event event) throws Throwable;
}
