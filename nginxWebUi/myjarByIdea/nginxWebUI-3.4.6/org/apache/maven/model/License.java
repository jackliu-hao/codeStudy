package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class License implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String url;
   private String distribution;
   private String comments;
   private Map<Object, InputLocation> locations;

   public License clone() {
      try {
         License copy = (License)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getComments() {
      return this.comments;
   }

   public String getDistribution() {
      return this.distribution;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getName() {
      return this.name;
   }

   public String getUrl() {
      return this.url;
   }

   public void setComments(String comments) {
      this.comments = comments;
   }

   public void setDistribution(String distribution) {
      this.distribution = distribution;
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

   public void setUrl(String url) {
      this.url = url;
   }
}
