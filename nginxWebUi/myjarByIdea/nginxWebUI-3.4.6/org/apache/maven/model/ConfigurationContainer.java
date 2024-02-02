package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ConfigurationContainer implements Serializable, Cloneable, InputLocationTracker {
   private String inherited;
   private Object configuration;
   private Map<Object, InputLocation> locations;
   private boolean inheritanceApplied = true;

   public ConfigurationContainer clone() {
      try {
         ConfigurationContainer copy = (ConfigurationContainer)super.clone();
         if (this.configuration != null) {
            copy.configuration = new Xpp3Dom((Xpp3Dom)this.configuration);
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public Object getConfiguration() {
      return this.configuration;
   }

   public String getInherited() {
      return this.inherited;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public void setConfiguration(Object configuration) {
      this.configuration = configuration;
   }

   public void setInherited(String inherited) {
      this.inherited = inherited;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public boolean isInherited() {
      return this.inherited != null ? Boolean.parseBoolean(this.inherited) : true;
   }

   public void setInherited(boolean inherited) {
      this.inherited = String.valueOf(inherited);
   }

   public void unsetInheritanceApplied() {
      this.inheritanceApplied = false;
   }

   public boolean isInheritanceApplied() {
      return this.inheritanceApplied;
   }
}
