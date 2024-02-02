package io.undertow.server.handlers.resource;

import java.util.Collection;

public interface ResourceChangeListener {
   void handleChanges(Collection<ResourceChangeEvent> var1);
}
