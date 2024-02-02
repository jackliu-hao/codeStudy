package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ModelBase implements Serializable, Cloneable, InputLocationTracker {
   private List<String> modules;
   private DistributionManagement distributionManagement;
   private Properties properties;
   private DependencyManagement dependencyManagement;
   private List<Dependency> dependencies;
   private List<Repository> repositories;
   private List<Repository> pluginRepositories;
   private Object reports;
   private Reporting reporting;
   private Map<Object, InputLocation> locations;

   public void addDependency(Dependency dependency) {
      this.getDependencies().add(dependency);
   }

   public void addModule(String string) {
      this.getModules().add(string);
   }

   public void addPluginRepository(Repository repository) {
      this.getPluginRepositories().add(repository);
   }

   public void addProperty(String key, String value) {
      this.getProperties().put(key, value);
   }

   public void addRepository(Repository repository) {
      this.getRepositories().add(repository);
   }

   public ModelBase clone() {
      try {
         ModelBase copy = (ModelBase)super.clone();
         if (this.modules != null) {
            copy.modules = new ArrayList();
            copy.modules.addAll(this.modules);
         }

         if (this.distributionManagement != null) {
            copy.distributionManagement = this.distributionManagement.clone();
         }

         if (this.properties != null) {
            copy.properties = (Properties)this.properties.clone();
         }

         if (this.dependencyManagement != null) {
            copy.dependencyManagement = this.dependencyManagement.clone();
         }

         Iterator i$;
         if (this.dependencies != null) {
            copy.dependencies = new ArrayList();
            i$ = this.dependencies.iterator();

            while(i$.hasNext()) {
               Dependency item = (Dependency)i$.next();
               copy.dependencies.add(item.clone());
            }
         }

         Repository item;
         if (this.repositories != null) {
            copy.repositories = new ArrayList();
            i$ = this.repositories.iterator();

            while(i$.hasNext()) {
               item = (Repository)i$.next();
               copy.repositories.add(item.clone());
            }
         }

         if (this.pluginRepositories != null) {
            copy.pluginRepositories = new ArrayList();
            i$ = this.pluginRepositories.iterator();

            while(i$.hasNext()) {
               item = (Repository)i$.next();
               copy.pluginRepositories.add(item.clone());
            }
         }

         if (this.reports != null) {
            copy.reports = new Xpp3Dom((Xpp3Dom)this.reports);
         }

         if (this.reporting != null) {
            copy.reporting = this.reporting.clone();
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

   public DependencyManagement getDependencyManagement() {
      return this.dependencyManagement;
   }

   public DistributionManagement getDistributionManagement() {
      return this.distributionManagement;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public List<String> getModules() {
      if (this.modules == null) {
         this.modules = new ArrayList();
      }

      return this.modules;
   }

   public List<Repository> getPluginRepositories() {
      if (this.pluginRepositories == null) {
         this.pluginRepositories = new ArrayList();
      }

      return this.pluginRepositories;
   }

   public Properties getProperties() {
      if (this.properties == null) {
         this.properties = new Properties();
      }

      return this.properties;
   }

   public Reporting getReporting() {
      return this.reporting;
   }

   public Object getReports() {
      return this.reports;
   }

   public List<Repository> getRepositories() {
      if (this.repositories == null) {
         this.repositories = new ArrayList();
      }

      return this.repositories;
   }

   public void removeDependency(Dependency dependency) {
      this.getDependencies().remove(dependency);
   }

   public void removeModule(String string) {
      this.getModules().remove(string);
   }

   public void removePluginRepository(Repository repository) {
      this.getPluginRepositories().remove(repository);
   }

   public void removeRepository(Repository repository) {
      this.getRepositories().remove(repository);
   }

   public void setDependencies(List<Dependency> dependencies) {
      this.dependencies = dependencies;
   }

   public void setDependencyManagement(DependencyManagement dependencyManagement) {
      this.dependencyManagement = dependencyManagement;
   }

   public void setDistributionManagement(DistributionManagement distributionManagement) {
      this.distributionManagement = distributionManagement;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setModules(List<String> modules) {
      this.modules = modules;
   }

   public void setPluginRepositories(List<Repository> pluginRepositories) {
      this.pluginRepositories = pluginRepositories;
   }

   public void setProperties(Properties properties) {
      this.properties = properties;
   }

   public void setReporting(Reporting reporting) {
      this.reporting = reporting;
   }

   public void setReports(Object reports) {
      this.reports = reports;
   }

   public void setRepositories(List<Repository> repositories) {
      this.repositories = repositories;
   }
}
