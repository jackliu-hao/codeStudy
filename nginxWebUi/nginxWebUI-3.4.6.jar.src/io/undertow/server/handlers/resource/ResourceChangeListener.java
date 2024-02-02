package io.undertow.server.handlers.resource;

import java.util.Collection;

public interface ResourceChangeListener {
  void handleChanges(Collection<ResourceChangeEvent> paramCollection);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\ResourceChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */