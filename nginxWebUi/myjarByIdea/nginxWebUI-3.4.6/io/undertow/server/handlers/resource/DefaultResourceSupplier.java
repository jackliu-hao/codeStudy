package io.undertow.server.handlers.resource;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;

public class DefaultResourceSupplier implements ResourceSupplier {
   private final ResourceManager resourceManager;

   public DefaultResourceSupplier(ResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public Resource getResource(HttpServerExchange exchange, String path) throws IOException {
      return this.resourceManager.getResource(path);
   }
}
