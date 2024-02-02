/*     */ package ch.qos.logback.core.joran.util;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
/*     */ import ch.qos.logback.core.status.InfoStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.StatusManager;
/*     */ import ch.qos.logback.core.status.WarnStatus;
/*     */ import java.net.URL;
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
/*     */ 
/*     */ public class ConfigurationWatchListUtil
/*     */ {
/*  31 */   static final ConfigurationWatchListUtil origin = new ConfigurationWatchListUtil();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerConfigurationWatchList(Context context, ConfigurationWatchList cwl) {
/*  38 */     context.putObject("CONFIGURATION_WATCH_LIST", cwl);
/*     */   }
/*     */   public static void setMainWatchURL(Context context, URL url) {
/*  41 */     ConfigurationWatchList cwl = getConfigurationWatchList(context);
/*  42 */     if (cwl == null) {
/*  43 */       cwl = new ConfigurationWatchList();
/*  44 */       cwl.setContext(context);
/*  45 */       context.putObject("CONFIGURATION_WATCH_LIST", cwl);
/*     */     } else {
/*  47 */       cwl.clear();
/*     */     } 
/*     */     
/*  50 */     cwl.setMainURL(url);
/*     */   }
/*     */   
/*     */   public static URL getMainWatchURL(Context context) {
/*  54 */     ConfigurationWatchList cwl = getConfigurationWatchList(context);
/*  55 */     if (cwl == null) {
/*  56 */       return null;
/*     */     }
/*  58 */     return cwl.getMainURL();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void addToWatchList(Context context, URL url) {
/*  63 */     ConfigurationWatchList cwl = getConfigurationWatchList(context);
/*  64 */     if (cwl == null) {
/*  65 */       addWarn(context, "Null ConfigurationWatchList. Cannot add " + url);
/*     */     } else {
/*  67 */       addInfo(context, "Adding [" + url + "] to configuration watch list.");
/*  68 */       cwl.addToWatchList(url);
/*     */     } 
/*     */   }
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
/*     */   public static ConfigurationWatchList getConfigurationWatchList(Context context) {
/*  86 */     return (ConfigurationWatchList)context.getObject("CONFIGURATION_WATCH_LIST");
/*     */   }
/*     */   
/*     */   static void addStatus(Context context, Status s) {
/*  90 */     if (context == null) {
/*  91 */       System.out.println("Null context in " + ConfigurationWatchList.class.getName());
/*     */       return;
/*     */     } 
/*  94 */     StatusManager sm = context.getStatusManager();
/*  95 */     if (sm == null)
/*     */       return; 
/*  97 */     sm.add(s);
/*     */   }
/*     */   
/*     */   static void addInfo(Context context, String msg) {
/* 101 */     addStatus(context, (Status)new InfoStatus(msg, origin));
/*     */   }
/*     */   
/*     */   static void addWarn(Context context, String msg) {
/* 105 */     addStatus(context, (Status)new WarnStatus(msg, origin));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\jora\\util\ConfigurationWatchListUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */