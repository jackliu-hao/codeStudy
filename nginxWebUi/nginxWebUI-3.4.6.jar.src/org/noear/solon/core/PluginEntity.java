/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.util.Properties;
/*     */ import org.noear.solon.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PluginEntity
/*     */ {
/*     */   private String className;
/*     */   private ClassLoader classLoader;
/*  27 */   private int priority = 0;
/*     */ 
/*     */   
/*     */   private Plugin plugin;
/*     */   
/*     */   private Properties props;
/*     */ 
/*     */   
/*     */   public PluginEntity(ClassLoader classLoader, String className, Properties props) {
/*  36 */     this.classLoader = classLoader;
/*  37 */     this.className = className;
/*  38 */     this.props = props;
/*     */   }
/*     */   
/*     */   public PluginEntity(Plugin plugin) {
/*  42 */     this.plugin = plugin;
/*     */   }
/*     */   
/*     */   public PluginEntity(Plugin plugin, int priority) {
/*  46 */     this.plugin = plugin;
/*  47 */     this.priority = priority;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPriority() {
/*  55 */     return this.priority;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPriority(int priority) {
/*  62 */     this.priority = priority;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Plugin getPlugin() {
/*  69 */     init();
/*     */     
/*  71 */     return this.plugin;
/*     */   }
/*     */   
/*     */   public Properties getProps() {
/*  75 */     return this.props;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start(AopContext context) {
/*  82 */     init();
/*     */     
/*  84 */     if (this.plugin != null) {
/*  85 */       this.plugin.start(context);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prestop() {
/*  93 */     init();
/*     */     
/*  95 */     if (this.plugin != null) {
/*     */       try {
/*  97 */         this.plugin.prestop();
/*  98 */       } catch (Throwable throwable) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 108 */     init();
/*     */     
/* 110 */     if (this.plugin != null) {
/*     */       try {
/* 112 */         this.plugin.stop();
/* 113 */       } catch (Throwable throwable) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init() {
/* 123 */     if (this.plugin == null && 
/* 124 */       this.classLoader != null)
/* 125 */       this.plugin = (Plugin)Utils.newInstance(this.classLoader, this.className); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\PluginEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */