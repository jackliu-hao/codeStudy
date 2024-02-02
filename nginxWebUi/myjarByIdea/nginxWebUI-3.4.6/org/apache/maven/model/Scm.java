package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scm implements Serializable, Cloneable, InputLocationTracker {
   private String connection;
   private String developerConnection;
   private String tag = "HEAD";
   private String url;
   private Map<Object, InputLocation> locations;

   public Scm clone() {
      try {
         Scm copy = (Scm)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getConnection() {
      return this.connection;
   }

   public String getDeveloperConnection() {
      return this.developerConnection;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getTag() {
      return this.tag;
   }

   public String getUrl() {
      return this.url;
   }

   public void setConnection(String connection) {
      this.connection = connection;
   }

   public void setDeveloperConnection(String developerConnection) {
      this.developerConnection = developerConnection;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
