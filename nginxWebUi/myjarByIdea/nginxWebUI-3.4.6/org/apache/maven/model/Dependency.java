package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Dependency implements Serializable, Cloneable, InputLocationTracker {
   private String groupId;
   private String artifactId;
   private String version;
   private String type = "jar";
   private String classifier;
   private String scope;
   private String systemPath;
   private List<Exclusion> exclusions;
   private String optional;
   private Map<Object, InputLocation> locations;

   public void addExclusion(Exclusion exclusion) {
      this.getExclusions().add(exclusion);
   }

   public Dependency clone() {
      try {
         Dependency copy = (Dependency)super.clone();
         if (this.exclusions != null) {
            copy.exclusions = new ArrayList();
            Iterator i$ = this.exclusions.iterator();

            while(i$.hasNext()) {
               Exclusion item = (Exclusion)i$.next();
               copy.exclusions.add(item.clone());
            }
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var4) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var4);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getClassifier() {
      return this.classifier;
   }

   public List<Exclusion> getExclusions() {
      if (this.exclusions == null) {
         this.exclusions = new ArrayList();
      }

      return this.exclusions;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getOptional() {
      return this.optional;
   }

   public String getScope() {
      return this.scope;
   }

   public String getSystemPath() {
      return this.systemPath;
   }

   public String getType() {
      return this.type;
   }

   public String getVersion() {
      return this.version;
   }

   public void removeExclusion(Exclusion exclusion) {
      this.getExclusions().remove(exclusion);
   }

   public void setArtifactId(String artifactId) {
      this.artifactId = artifactId;
   }

   public void setClassifier(String classifier) {
      this.classifier = classifier;
   }

   public void setExclusions(List<Exclusion> exclusions) {
      this.exclusions = exclusions;
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

   public void setOptional(String optional) {
      this.optional = optional;
   }

   public void setScope(String scope) {
      this.scope = scope;
   }

   public void setSystemPath(String systemPath) {
      this.systemPath = systemPath;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public boolean isOptional() {
      return this.optional != null ? Boolean.parseBoolean(this.optional) : false;
   }

   public void setOptional(boolean optional) {
      this.optional = String.valueOf(optional);
   }

   public String toString() {
      return "Dependency {groupId=" + this.groupId + ", artifactId=" + this.artifactId + ", version=" + this.version + ", type=" + this.type + "}";
   }

   public String getManagementKey() {
      return this.groupId + ":" + this.artifactId + ":" + this.type + (this.classifier != null ? ":" + this.classifier : "");
   }
}
