package org.h2.security.auth;

import java.util.ArrayList;
import java.util.List;

public class RealmConfig implements HasConfigProperties {
   private String name;
   private String validatorClass;
   private List<PropertyConfig> properties;

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getValidatorClass() {
      return this.validatorClass;
   }

   public void setValidatorClass(String var1) {
      this.validatorClass = var1;
   }

   public List<PropertyConfig> getProperties() {
      if (this.properties == null) {
         this.properties = new ArrayList();
      }

      return this.properties;
   }
}
