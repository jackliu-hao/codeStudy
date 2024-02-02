package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Exclusion implements Serializable, Cloneable, InputLocationTracker {
   private String artifactId;
   private String groupId;
   private Map<Object, InputLocation> locations;

   public Exclusion clone() {
      try {
         Exclusion copy = (Exclusion)super.clone();
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
}
