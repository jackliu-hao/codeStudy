package org.noear.solon.core.util;

import java.util.Properties;
import java.util.function.Consumer;
import org.noear.solon.Utils;
import org.noear.solon.core.PluginEntity;

public class PluginUtil {
   public static void scanPlugins(ClassLoader classLoader, String limitFile, Consumer<PluginEntity> consumer) {
      ScanUtil.scan(classLoader, "META-INF/solon", (n) -> {
         return n.endsWith(".properties") || n.endsWith(".yml");
      }).stream().map((k) -> {
         return Utils.getResource(classLoader, k);
      }).filter((url) -> {
         return !Utils.isNotEmpty(limitFile) || url.toString().contains(limitFile);
      }).forEach((url) -> {
         Properties props = Utils.loadProperties(url);
         findPlugins(classLoader, props, consumer);
      });
   }

   public static void findPlugins(ClassLoader classLoader, Properties props, Consumer<PluginEntity> consumer) {
      String pluginStr = props.getProperty("solon.plugin");
      if (Utils.isNotEmpty(pluginStr)) {
         int priority = Integer.parseInt(props.getProperty("solon.plugin.priority", "0"));
         String[] plugins = pluginStr.trim().split(",");
         String[] var6 = plugins;
         int var7 = plugins.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String clzName = var6[var8];
            if (clzName.length() > 0) {
               PluginEntity ent = new PluginEntity(classLoader, clzName.trim(), props);
               ent.setPriority(priority);
               consumer.accept(ent);
            }
         }
      }

   }
}
