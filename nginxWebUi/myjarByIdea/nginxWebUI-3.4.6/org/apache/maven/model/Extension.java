package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Extension implements Serializable, Cloneable, InputLocationTracker {
   private String groupId;
   private String artifactId;
   private String version;
   private Map<Object, InputLocation> locations;

   public Extension clone() {
      try {
         Extension copy = (Extension)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getVersion() {
      return this.version;
   }

   public void setArtifactId(String artifactId) {
      this.artifactId = artifactId;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setVersion(String version) {
      this.version = version;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Extension)) {
         return false;
      } else {
         Extension e = (Extension)o;
         if (!equal(e.getArtifactId(), this.getArtifactId())) {
            return false;
         } else if (!equal(e.getGroupId(), this.getGroupId())) {
            return false;
         } else {
            return equal(e.getVersion(), this.getVersion());
         }
      }
   }

   private static <T> boolean equal(T obj1, T obj2) {
      return obj1 != null ? obj1.equals(obj2) : obj2 == null;
   }

   public int hashCode() {
      int result = 17;
      result = 37 * result + (this.getArtifactId() != null ? this.getArtifactId().hashCode() : 0);
      result = 37 * result + (this.getGroupId() != null ? this.getGroupId().hashCode() : 0);
      result = 37 * result + (this.getVersion() != null ? this.getVersion().hashCode() : 0);
      return result;
   }
}
