package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ReportSet implements Serializable, Cloneable, InputLocationTracker {
   private String id = "default";
   private Object configuration;
   private String inherited;
   private List<String> reports;
   private Map<Object, InputLocation> locations;
   private boolean inheritanceApplied = true;

   public void addReport(String string) {
      this.getReports().add(string);
   }

   public ReportSet clone() {
      try {
         ReportSet copy = (ReportSet)super.clone();
         if (this.configuration != null) {
            copy.configuration = new Xpp3Dom((Xpp3Dom)this.configuration);
         }

         if (this.reports != null) {
            copy.reports = new ArrayList();
            copy.reports.addAll(this.reports);
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

   public String getId() {
      return this.id;
   }

   public String getInherited() {
      return this.inherited;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public List<String> getReports() {
      if (this.reports == null) {
         this.reports = new ArrayList();
      }

      return this.reports;
   }

   public void removeReport(String string) {
      this.getReports().remove(string);
   }

   public void setConfiguration(Object configuration) {
      this.configuration = configuration;
   }

   public void setId(String id) {
      this.id = id;
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

   public void setReports(List<String> reports) {
      this.reports = reports;
   }

   public void unsetInheritanceApplied() {
      this.inheritanceApplied = false;
   }

   public boolean isInheritanceApplied() {
      return this.inheritanceApplied;
   }

   public String toString() {
      return this.getId();
   }
}
