package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Activation implements Serializable, Cloneable, InputLocationTracker {
   private boolean activeByDefault = false;
   private String jdk;
   private ActivationOS os;
   private ActivationProperty property;
   private ActivationFile file;
   private Map<Object, InputLocation> locations;

   public Activation clone() {
      try {
         Activation copy = (Activation)super.clone();
         if (this.os != null) {
            copy.os = this.os.clone();
         }

         if (this.property != null) {
            copy.property = this.property.clone();
         }

         if (this.file != null) {
            copy.file = this.file.clone();
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public ActivationFile getFile() {
      return this.file;
   }

   public String getJdk() {
      return this.jdk;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public ActivationOS getOs() {
      return this.os;
   }

   public ActivationProperty getProperty() {
      return this.property;
   }

   public boolean isActiveByDefault() {
      return this.activeByDefault;
   }

   public void setActiveByDefault(boolean activeByDefault) {
      this.activeByDefault = activeByDefault;
   }

   public void setFile(ActivationFile file) {
      this.file = file;
   }

   public void setJdk(String jdk) {
      this.jdk = jdk;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setOs(ActivationOS os) {
      this.os = os;
   }

   public void setProperty(ActivationProperty property) {
      this.property = property;
   }
}
