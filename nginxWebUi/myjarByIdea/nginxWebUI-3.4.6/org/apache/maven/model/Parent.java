package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parent implements Serializable, Cloneable, InputLocationTracker {
   private String artifactId;
   private String groupId;
   private String version;
   private String relativePath = "../pom.xml";
   private Map<Object, InputLocation> locations;

   public Parent clone() {
      try {
         Parent copy = (Parent)super.clone();
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

   public String getRelativePath() {
      return this.relativePath;
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

   public void setRelativePath(String relativePath) {
      this.relativePath = relativePath;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getId() {
      StringBuilder id = new StringBuilder(64);
      id.append(this.getGroupId());
      id.append(":");
      id.append(this.getArtifactId());
      id.append(":");
      id.append("pom");
      id.append(":");
      id.append(this.getVersion());
      return id.toString();
   }

   public String toString() {
      return this.getId();
   }
}
