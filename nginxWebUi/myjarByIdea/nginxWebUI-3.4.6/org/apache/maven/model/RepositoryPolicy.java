package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class RepositoryPolicy implements Serializable, Cloneable, InputLocationTracker {
   private String enabled;
   private String updatePolicy;
   private String checksumPolicy;
   private Map<Object, InputLocation> locations;

   public RepositoryPolicy clone() {
      try {
         RepositoryPolicy copy = (RepositoryPolicy)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getChecksumPolicy() {
      return this.checksumPolicy;
   }

   public String getEnabled() {
      return this.enabled;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getUpdatePolicy() {
      return this.updatePolicy;
   }

   public void setChecksumPolicy(String checksumPolicy) {
      this.checksumPolicy = checksumPolicy;
   }

   public void setEnabled(String enabled) {
      this.enabled = enabled;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setUpdatePolicy(String updatePolicy) {
      this.updatePolicy = updatePolicy;
   }

   public boolean isEnabled() {
      return this.enabled != null ? Boolean.parseBoolean(this.enabled) : true;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = String.valueOf(enabled);
   }
}
