package org.xnio;

import java.security.PrivilegedAction;

public final class ReadPropertyAction implements PrivilegedAction<String> {
   private final String propertyName;
   private final String defaultValue;

   public ReadPropertyAction(String propertyName, String defaultValue) {
      this.propertyName = propertyName;
      this.defaultValue = defaultValue;
   }

   public String run() {
      return System.getProperty(this.propertyName, this.defaultValue);
   }
}
