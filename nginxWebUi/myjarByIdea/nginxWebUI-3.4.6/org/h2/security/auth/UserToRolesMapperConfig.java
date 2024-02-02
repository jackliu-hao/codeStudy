package org.h2.security.auth;

import java.util.ArrayList;
import java.util.List;

public class UserToRolesMapperConfig implements HasConfigProperties {
   private String className;
   private List<PropertyConfig> properties;

   public String getClassName() {
      return this.className;
   }

   public void setClassName(String var1) {
      this.className = var1;
   }

   public List<PropertyConfig> getProperties() {
      if (this.properties == null) {
         this.properties = new ArrayList();
      }

      return this.properties;
   }
}
