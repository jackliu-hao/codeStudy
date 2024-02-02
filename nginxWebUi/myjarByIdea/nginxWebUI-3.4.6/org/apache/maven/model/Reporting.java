package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Reporting implements Serializable, Cloneable, InputLocationTracker {
   private String excludeDefaults;
   private String outputDirectory;
   private List<ReportPlugin> plugins;
   private Map<Object, InputLocation> locations;
   Map<String, ReportPlugin> reportPluginMap;

   public void addPlugin(ReportPlugin reportPlugin) {
      this.getPlugins().add(reportPlugin);
   }

   public Reporting clone() {
      try {
         Reporting copy = (Reporting)super.clone();
         if (this.plugins != null) {
            copy.plugins = new ArrayList();
            Iterator i$ = this.plugins.iterator();

            while(i$.hasNext()) {
               ReportPlugin item = (ReportPlugin)i$.next();
               copy.plugins.add(item.clone());
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

   public String getExcludeDefaults() {
      return this.excludeDefaults;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getOutputDirectory() {
      return this.outputDirectory;
   }

   public List<ReportPlugin> getPlugins() {
      if (this.plugins == null) {
         this.plugins = new ArrayList();
      }

      return this.plugins;
   }

   public void removePlugin(ReportPlugin reportPlugin) {
      this.getPlugins().remove(reportPlugin);
   }

   public void setExcludeDefaults(String excludeDefaults) {
      this.excludeDefaults = excludeDefaults;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setOutputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory;
   }

   public void setPlugins(List<ReportPlugin> plugins) {
      this.plugins = plugins;
   }

   public boolean isExcludeDefaults() {
      return this.excludeDefaults != null ? Boolean.parseBoolean(this.excludeDefaults) : false;
   }

   public void setExcludeDefaults(boolean excludeDefaults) {
      this.excludeDefaults = String.valueOf(excludeDefaults);
   }

   public synchronized void flushReportPluginMap() {
      this.reportPluginMap = null;
   }

   public synchronized Map<String, ReportPlugin> getReportPluginsAsMap() {
      if (this.reportPluginMap == null) {
         this.reportPluginMap = new LinkedHashMap();
         if (this.getPlugins() != null) {
            Iterator<ReportPlugin> it = this.getPlugins().iterator();

            while(it.hasNext()) {
               ReportPlugin reportPlugin = (ReportPlugin)it.next();
               this.reportPluginMap.put(reportPlugin.getKey(), reportPlugin);
            }
         }
      }

      return this.reportPluginMap;
   }
}
