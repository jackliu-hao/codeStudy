package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DependencyManagement implements Serializable, Cloneable, InputLocationTracker {
   private List<Dependency> dependencies;
   private Map<Object, InputLocation> locations;

   public void addDependency(Dependency dependency) {
      this.getDependencies().add(dependency);
   }

   public DependencyManagement clone() {
      try {
         DependencyManagement copy = (DependencyManagement)super.clone();
         if (this.dependencies != null) {
            copy.dependencies = new ArrayList();
            Iterator i$ = this.dependencies.iterator();

            while(i$.hasNext()) {
               Dependency item = (Dependency)i$.next();
               copy.dependencies.add(item.clone());
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

   public List<Dependency> getDependencies() {
      if (this.dependencies == null) {
         this.dependencies = new ArrayList();
      }

      return this.dependencies;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public void removeDependency(Dependency dependency) {
      this.getDependencies().remove(dependency);
   }

   public void setDependencies(List<Dependency> dependencies) {
      this.dependencies = dependencies;
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
