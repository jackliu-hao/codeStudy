package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PluginContainer implements Serializable, Cloneable, InputLocationTracker {
   private List<Plugin> plugins;
   private Map<Object, InputLocation> locations;
   Map<String, Plugin> pluginMap;

   public void addPlugin(Plugin plugin) {
      this.getPlugins().add(plugin);
   }

   public PluginContainer clone() {
      try {
         PluginContainer copy = (PluginContainer)super.clone();
         if (this.plugins != null) {
            copy.plugins = new ArrayList();
            Iterator i$ = this.plugins.iterator();

            while(i$.hasNext()) {
               Plugin item = (Plugin)i$.next();
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

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public List<Plugin> getPlugins() {
      if (this.plugins == null) {
         this.plugins = new ArrayList();
      }

      return this.plugins;
   }

   public void removePlugin(Plugin plugin) {
      this.getPlugins().remove(plugin);
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setPlugins(List<Plugin> plugins) {
      this.plugins = plugins;
   }

   public synchronized void flushPluginMap() {
      this.pluginMap = null;
   }

   public synchronized Map<String, Plugin> getPluginsAsMap() {
      if (this.pluginMap == null) {
         this.pluginMap = new LinkedHashMap();
         if (this.plugins != null) {
            Iterator<Plugin> it = this.plugins.iterator();

            while(it.hasNext()) {
               Plugin plugin = (Plugin)it.next();
               this.pluginMap.put(plugin.getKey(), plugin);
            }
         }
      }

      return this.pluginMap;
   }
}
