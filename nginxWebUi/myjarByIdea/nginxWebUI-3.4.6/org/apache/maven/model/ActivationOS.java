package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivationOS implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String family;
   private String arch;
   private String version;
   private Map<Object, InputLocation> locations;

   public ActivationOS clone() {
      try {
         ActivationOS copy = (ActivationOS)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getArch() {
      return this.arch;
   }

   public String getFamily() {
      return this.family;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.version;
   }

   public void setArch(String arch) {
      this.arch = arch;
   }

   public void setFamily(String family) {
      this.family = family;
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

   public void setVersion(String version) {
      this.version = version;
   }
}
