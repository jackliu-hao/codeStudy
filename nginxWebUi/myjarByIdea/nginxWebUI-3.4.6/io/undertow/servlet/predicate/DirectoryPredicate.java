package io.undertow.servlet.predicate;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.PredicateBuilder;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DirectoryPredicate implements Predicate {
   private final ExchangeAttribute location;

   public DirectoryPredicate(ExchangeAttribute location) {
      this.location = location;
   }

   public boolean resolve(HttpServerExchange value) {
      String location = this.location.readAttribute(value);
      ServletRequestContext src = (ServletRequestContext)value.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (src == null) {
         return false;
      } else {
         ResourceManager manager = src.getDeployment().getDeploymentInfo().getResourceManager();
         if (manager == null) {
            return false;
         } else {
            try {
               Resource resource = manager.getResource(location);
               return resource == null ? false : resource.isDirectory();
            } catch (IOException var6) {
               throw new RuntimeException(var6);
            }
         }
      }
   }

   public String toString() {
      return "directory( " + this.location.toString() + " )";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "directory";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("value", ExchangeAttribute.class);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return "value";
      }

      public Predicate build(Map<String, Object> config) {
         ExchangeAttribute value = (ExchangeAttribute)config.get("value");
         if (value == null) {
            value = ExchangeAttributes.relativePath();
         }

         return new DirectoryPredicate(value);
      }
   }
}
