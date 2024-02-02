package io.undertow.server.handlers.resource;

public class ResourceChangeEvent {
   private final String resource;
   private final Type type;

   public ResourceChangeEvent(String resource, Type type) {
      this.resource = resource;
      this.type = type;
   }

   public String getResource() {
      return this.resource;
   }

   public Type getType() {
      return this.type;
   }

   public static enum Type {
      ADDED,
      REMOVED,
      MODIFIED;
   }
}
