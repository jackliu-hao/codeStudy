package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivationFile implements Serializable, Cloneable, InputLocationTracker {
   private String missing;
   private String exists;
   private Map<Object, InputLocation> locations;

   public ActivationFile clone() {
      try {
         ActivationFile copy = (ActivationFile)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getExists() {
      return this.exists;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getMissing() {
      return this.missing;
   }

   public void setExists(String exists) {
      this.exists = exists;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setMissing(String missing) {
      this.missing = missing;
   }
}
