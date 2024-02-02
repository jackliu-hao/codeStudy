package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class IssueManagement implements Serializable, Cloneable, InputLocationTracker {
   private String system;
   private String url;
   private Map<Object, InputLocation> locations;

   public IssueManagement clone() {
      try {
         IssueManagement copy = (IssueManagement)super.clone();
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

   public String getSystem() {
      return this.system;
   }

   public String getUrl() {
      return this.url;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setSystem(String system) {
      this.system = system;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
