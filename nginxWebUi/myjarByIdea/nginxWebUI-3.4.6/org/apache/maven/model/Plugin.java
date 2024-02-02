package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class Plugin extends ConfigurationContainer implements Serializable, Cloneable {
   private String groupId = "org.apache.maven.plugins";
   private String artifactId;
   private String version;
   private String extensions;
   private List<PluginExecution> executions;
   private List<Dependency> dependencies;
   private Object goals;
   private Map<String, PluginExecution> executionMap = null;
   private String key = null;

   public void addDependency(Dependency dependency) {
      this.getDependencies().add(dependency);
   }

   public void addExecution(PluginExecution pluginExecution) {
      this.getExecutions().add(pluginExecution);
   }

   public Plugin clone() {
      try {
         Plugin copy = (Plugin)super.clone();
         Iterator i$;
         if (this.executions != null) {
            copy.executions = new ArrayList();
            i$ = this.executions.iterator();

            while(i$.hasNext()) {
               PluginExecution item = (PluginExecution)i$.next();
               copy.executions.add(item.clone());
            }
         }

         if (this.dependencies != null) {
            copy.dependencies = new ArrayList();
            i$ = this.dependencies.iterator();

            while(i$.hasNext()) {
               Dependency item = (Dependency)i$.next();
               copy.dependencies.add(item.clone());
            }
         }

         if (this.goals != null) {
            copy.goals = new Xpp3Dom((Xpp3Dom)this.goals);
         }

         return copy;
      } catch (Exception var4) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var4);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public List<Dependency> getDependencies() {
      if (this.dependencies == null) {
         this.dependencies = new ArrayList();
      }

      return this.dependencies;
   }

   public List<PluginExecution> getExecutions() {
      if (this.executions == null) {
         this.executions = new ArrayList();
      }

      return this.executions;
   }

   public String getExtensions() {
      return this.extensions;
   }

   public Object getGoals() {
      return this.goals;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getVersion() {
      return this.version;
   }

   public void removeDependency(Dependency dependency) {
      this.getDependencies().remove(dependency);
   }

   public void removeExecution(PluginExecution pluginExecution) {
      this.getExecutions().remove(pluginExecution);
   }

   public void setArtifactId(String artifactId) {
      this.artifactId = artifactId;
   }

   public void setDependencies(List<Dependency> dependencies) {
      this.dependencies = dependencies;
   }

   public void setExecutions(List<PluginExecution> executions) {
      this.executions = executions;
   }

   public void setExtensions(String extensions) {
      this.extensions = extensions;
   }

   public void setGoals(Object goals) {
      this.goals = goals;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public boolean isExtensions() {
      return this.extensions != null ? Boolean.parseBoolean(this.extensions) : false;
   }

   public void setExtensions(boolean extensions) {
      this.extensions = String.valueOf(extensions);
   }

   public void flushExecutionMap() {
      this.executionMap = null;
   }

   public Map<String, PluginExecution> getExecutionsAsMap() {
      if (this.executionMap == null) {
         this.executionMap = new LinkedHashMap();
         if (this.getExecutions() != null) {
            Iterator<PluginExecution> i = this.getExecutions().iterator();

            while(i.hasNext()) {
               PluginExecution exec = (PluginExecution)i.next();
               if (this.executionMap.containsKey(exec.getId())) {
                  throw new IllegalStateException("You cannot have two plugin executions with the same (or missing) <id/> elements.\nOffending execution\n\nId: '" + exec.getId() + "'\nPlugin:'" + this.getKey() + "'\n\n");
               }

               this.executionMap.put(exec.getId(), exec);
            }
         }
      }

      return this.executionMap;
   }

   public String getId() {
      StringBuilder id = new StringBuilder(128);
      id.append(this.getGroupId() == null ? "[unknown-group-id]" : this.getGroupId());
      id.append(":");
      id.append(this.getArtifactId() == null ? "[unknown-artifact-id]" : this.getArtifactId());
      id.append(":");
      id.append(this.getVersion() == null ? "[unknown-version]" : this.getVersion());
      return id.toString();
   }

   public String getKey() {
      if (this.key == null) {
         this.key = constructKey(this.groupId, this.artifactId);
      }

      return this.key;
   }

   public static String constructKey(String groupId, String artifactId) {
      return groupId + ":" + artifactId;
   }

   public boolean equals(Object other) {
      if (other instanceof Plugin) {
         Plugin otherPlugin = (Plugin)other;
         return this.getKey().equals(otherPlugin.getKey());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getKey().hashCode();
   }

   public String toString() {
      return "Plugin [" + this.getKey() + "]";
   }
}
