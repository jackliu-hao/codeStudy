package org.h2.security.auth;

public class PropertyConfig {
   private String name;
   private String value;

   public PropertyConfig() {
   }

   public PropertyConfig(String var1, String var2) {
      this.name = var1;
      this.value = var2;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }
}
