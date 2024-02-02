/*    */ package org.noear.solon.core.util;
/*    */ 
/*    */ import java.net.URL;
/*    */ import java.util.Properties;
/*    */ import java.util.function.Consumer;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.PluginEntity;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PluginUtil
/*    */ {
/*    */   public static void scanPlugins(ClassLoader classLoader, String limitFile, Consumer<PluginEntity> consumer) {
/* 20 */     ScanUtil.scan(classLoader, "META-INF/solon", n -> 
/* 21 */         (n.endsWith(".properties") || n.endsWith(".yml")))
/*    */       
/* 23 */       .stream()
/* 24 */       .map(k -> Utils.getResource(classLoader, k))
/* 25 */       .filter(url -> 
/* 26 */         !(Utils.isNotEmpty(limitFile) && !url.toString().contains(limitFile)))
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 34 */       .forEach(url -> {
/*    */           Properties props = Utils.loadProperties(url);
/*    */           findPlugins(classLoader, props, consumer);
/*    */         });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void findPlugins(ClassLoader classLoader, Properties props, Consumer<PluginEntity> consumer) {
/* 44 */     String pluginStr = props.getProperty("solon.plugin");
/*    */     
/* 46 */     if (Utils.isNotEmpty(pluginStr)) {
/* 47 */       int priority = Integer.parseInt(props.getProperty("solon.plugin.priority", "0"));
/* 48 */       String[] plugins = pluginStr.trim().split(",");
/*    */       
/* 50 */       for (String clzName : plugins) {
/* 51 */         if (clzName.length() > 0) {
/* 52 */           PluginEntity ent = new PluginEntity(classLoader, clzName.trim(), props);
/* 53 */           ent.setPriority(priority);
/* 54 */           consumer.accept(ent);
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\PluginUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */