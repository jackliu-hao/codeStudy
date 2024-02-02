package org.noear.solon.core;

import java.util.Properties;
import org.noear.solon.Utils;

public class PluginEntity {
   private String className;
   private ClassLoader classLoader;
   private int priority = 0;
   private Plugin plugin;
   private Properties props;

   public PluginEntity(ClassLoader classLoader, String className, Properties props) {
      this.classLoader = classLoader;
      this.className = className;
      this.props = props;
   }

   public PluginEntity(Plugin plugin) {
      this.plugin = plugin;
   }

   public PluginEntity(Plugin plugin, int priority) {
      this.plugin = plugin;
      this.priority = priority;
   }

   public int getPriority() {
      return this.priority;
   }

   public void setPriority(int priority) {
      this.priority = priority;
   }

   public Plugin getPlugin() {
      this.init();
      return this.plugin;
   }

   public Properties getProps() {
      return this.props;
   }

   public void start(AopContext context) {
      this.init();
      if (this.plugin != null) {
         this.plugin.start(context);
      }

   }

   public void prestop() {
      this.init();
      if (this.plugin != null) {
         try {
            this.plugin.prestop();
         } catch (Throwable var2) {
         }
      }

   }

   public void stop() {
      this.init();
      if (this.plugin != null) {
         try {
            this.plugin.stop();
         } catch (Throwable var2) {
         }
      }

   }

   private void init() {
      if (this.plugin == null && this.classLoader != null) {
         this.plugin = (Plugin)Utils.newInstance(this.classLoader, this.className);
      }

   }
}
