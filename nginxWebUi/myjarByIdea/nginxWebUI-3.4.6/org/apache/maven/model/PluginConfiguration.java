package org.apache.maven.model;

import java.io.Serializable;

public class PluginConfiguration extends PluginContainer implements Serializable, Cloneable {
   private PluginManagement pluginManagement;

   public PluginConfiguration clone() {
      try {
         PluginConfiguration copy = (PluginConfiguration)super.clone();
         if (this.pluginManagement != null) {
            copy.pluginManagement = this.pluginManagement.clone();
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public PluginManagement getPluginManagement() {
      return this.pluginManagement;
   }

   public void setPluginManagement(PluginManagement pluginManagement) {
      this.pluginManagement = pluginManagement;
   }
}
