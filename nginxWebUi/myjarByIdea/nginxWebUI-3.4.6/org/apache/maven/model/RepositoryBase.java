package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class RepositoryBase implements Serializable, Cloneable, InputLocationTracker {
   private String id;
   private String name;
   private String url;
   private String layout = "default";
   private Map<Object, InputLocation> locations;

   public RepositoryBase clone() {
      try {
         RepositoryBase copy = (RepositoryBase)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof RepositoryBase)) {
         return false;
      } else {
         boolean var10000;
         boolean result;
         label24: {
            label23: {
               RepositoryBase that = (RepositoryBase)other;
               result = true;
               if (result) {
                  if (this.getId() == null) {
                     if (that.getId() == null) {
                        break label23;
                     }
                  } else if (this.getId().equals(that.getId())) {
                     break label23;
                  }
               }

               var10000 = false;
               break label24;
            }

            var10000 = true;
         }

         result = var10000;
         return result;
      }
   }

   public String getId() {
      return this.id;
   }

   public String getLayout() {
      return this.layout;
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

   public int hashCode() {
      int result = 17;
      result = 37 * result + (this.id != null ? this.id.hashCode() : 0);
      return result;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setLayout(String layout) {
      this.layout = layout;
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

   public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("id = '");
      buf.append(this.getId());
      buf.append("'");
      return buf.toString();
   }
}
