package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ReportPlugin implements Serializable, Cloneable, InputLocationTracker {
   private String groupId = "org.apache.maven.plugins";
   private String artifactId;
   private String version;
   private String inherited;
   private Object configuration;
   private List<ReportSet> reportSets;
   private Map<Object, InputLocation> locations;
   private Map<String, ReportSet> reportSetMap = null;
   private boolean inheritanceApplied = true;

   public void addReportSet(ReportSet reportSet) {
      this.getReportSets().add(reportSet);
   }

   public ReportPlugin clone() {
      try {
         ReportPlugin copy = (ReportPlugin)super.clone();
         if (this.configuration != null) {
            copy.configuration = new Xpp3Dom((Xpp3Dom)this.configuration);
         }

         if (this.reportSets != null) {
            copy.reportSets = new ArrayList();
            Iterator i$ = this.reportSets.iterator();

            while(i$.hasNext()) {
               ReportSet item = (ReportSet)i$.next();
               copy.reportSets.add(item.clone());
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

   public Object getConfiguration() {
      return this.configuration;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getInherited() {
      return this.inherited;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public List<ReportSet> getReportSets() {
      if (this.reportSets == null) {
         this.reportSets = new ArrayList();
      }

      return this.reportSets;
   }

   public String getVersion() {
      return this.version;
   }

   public void removeReportSet(ReportSet reportSet) {
      this.getReportSets().remove(reportSet);
   }

   public void setArtifactId(String artifactId) {
      this.artifactId = artifactId;
   }

   public void setConfiguration(Object configuration) {
      this.configuration = configuration;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
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

   public void setReportSets(List<ReportSet> reportSets) {
      this.reportSets = reportSets;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public void flushReportSetMap() {
      this.reportSetMap = null;
   }

   public Map<String, ReportSet> getReportSetsAsMap() {
      if (this.reportSetMap == null) {
         this.reportSetMap = new LinkedHashMap();
         if (this.getReportSets() != null) {
            Iterator<ReportSet> i = this.getReportSets().iterator();

            while(i.hasNext()) {
               ReportSet reportSet = (ReportSet)i.next();
               this.reportSetMap.put(reportSet.getId(), reportSet);
            }
         }
      }

      return this.reportSetMap;
   }

   public String getKey() {
      return constructKey(this.groupId, this.artifactId);
   }

   public static String constructKey(String groupId, String artifactId) {
      return groupId + ":" + artifactId;
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
