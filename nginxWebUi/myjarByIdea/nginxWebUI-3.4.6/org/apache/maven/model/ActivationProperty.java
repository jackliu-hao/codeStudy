package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivationProperty implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String value;
   private Map<Object, InputLocation> locations;

   public ActivationProperty clone() {
      try {
         ActivationProperty copy = (ActivationProperty)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setName(String name) {
      this.name = name;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
