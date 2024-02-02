package io.undertow.servlet.api;

import java.util.HashMap;
import java.util.Map;

public class AuthMethodConfig implements Cloneable {
   private final String name;
   private final Map<String, String> properties;

   public AuthMethodConfig(String name, Map<String, String> properties) {
      this.name = name;
      this.properties = new HashMap(properties);
   }

   public AuthMethodConfig(String name) {
      this.name = name;
      this.properties = new HashMap();
   }

   public String getName() {
      return this.name;
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   public AuthMethodConfig clone() {
      return new AuthMethodConfig(this.name, this.properties);
   }
}
