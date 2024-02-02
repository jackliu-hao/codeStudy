/*     */ package org.noear.solon.logging;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.Props;
/*     */ import org.noear.solon.logging.event.Level;
/*     */ import org.noear.solon.logging.model.LoggerLevelEntity;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LogOptions
/*     */ {
/*  18 */   private static volatile Map<String, LoggerLevelEntity> loggerLevelMap = new LinkedHashMap<>();
/*     */   
/*     */   private static volatile boolean loggerLevelMapInited = false;
/*     */   
/*  22 */   private static volatile Level rootLevel = Level.TRACE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void addLoggerLevel(String loggerExpr, Level level) {
/*  31 */     if (loggerExpr.endsWith(".*")) {
/*  32 */       loggerExpr = loggerExpr.substring(0, loggerExpr.length() - 1);
/*     */     }
/*     */     
/*  35 */     if (!loggerLevelMap.containsKey(loggerExpr)) {
/*  36 */       loggerLevelMap.put(loggerExpr, new LoggerLevelEntity(loggerExpr, level));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<LoggerLevelEntity> getLoggerLevels() {
/*  44 */     if (!loggerLevelMapInited) {
/*  45 */       loggerLevelMapInit();
/*     */     }
/*     */     
/*  48 */     return loggerLevelMap.values();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void getLoggerLevelInit() {
/*  56 */     if (!loggerLevelMapInited) {
/*  57 */       loggerLevelMapInit();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Level getLoggerLevel(String logger) {
/*  65 */     if (!loggerLevelMapInited) {
/*  66 */       loggerLevelMapInit();
/*     */     }
/*     */     
/*  69 */     if (Utils.isEmpty(logger)) {
/*  70 */       return getRootLevel();
/*     */     }
/*     */     
/*  73 */     for (LoggerLevelEntity l : loggerLevelMap.values()) {
/*  74 */       if (logger.startsWith(l.getLoggerExpr())) {
/*  75 */         return l.getLevel();
/*     */       }
/*     */     } 
/*     */     
/*  79 */     return getRootLevel();
/*     */   }
/*     */   
/*     */   public static Level getRootLevel() {
/*  83 */     return rootLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static synchronized void loggerLevelMapInit() {
/*  90 */     if (loggerLevelMapInited) {
/*     */       return;
/*     */     }
/*     */     
/*  94 */     if (Solon.app() == null) {
/*     */       return;
/*     */     }
/*     */     
/*  98 */     loggerLevelMapInited = true;
/*     */     
/* 100 */     Props props = Solon.cfg().getProp("solon.logging.logger");
/*     */     
/* 102 */     if (props.size() > 0)
/* 103 */       props.forEach((k, v) -> {
/*     */             String key = (String)k;
/*     */             String val = (String)v;
/*     */             if (key.endsWith(".level")) {
/*     */               String loggerExpr = key.substring(0, key.length() - 6);
/*     */               Level loggerLevel = Level.of(val, Level.INFO);
/*     */               addLoggerLevel(loggerExpr, loggerLevel);
/*     */               if ("root".equals(loggerExpr))
/*     */                 rootLevel = loggerLevel; 
/*     */             } 
/*     */           }); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\LogOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */