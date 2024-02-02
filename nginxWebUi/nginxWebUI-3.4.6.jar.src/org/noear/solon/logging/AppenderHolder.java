/*     */ package org.noear.solon.logging;
/*     */ 
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.core.util.PrintUtil;
/*     */ import org.noear.solon.logging.event.Appender;
/*     */ import org.noear.solon.logging.event.Level;
/*     */ import org.noear.solon.logging.event.LogEvent;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class AppenderHolder
/*     */ {
/*     */   Appender real;
/*     */   private String name;
/*     */   private boolean enable;
/*     */   private Level level;
/*     */   
/*     */   public AppenderHolder(String name, Appender real) {
/*  59 */     this.enable = true; this.real = real; this.name = name; real.setName(name); real.start(); if (Solon.app() != null) {
/*     */       String levelStr = Solon.cfg().get("solon.logging.appender." + getName() + ".level"); setLevel(Level.of(levelStr, real.getDefaultLevel())); this.enable = Solon.cfg().getBool("solon.logging.appender." + getName() + ".enable", true); Map<String, Object> meta = new LinkedHashMap<>(); meta.put("level", getLevel().name());
/*     */       meta.put("enable", Boolean.valueOf(this.enable));
/*     */       PrintUtil.info("Logging", getName() + " " + meta);
/*     */     } else {
/*     */       setLevel(real.getDefaultLevel());
/*  65 */     }  } public boolean getEnable() { return this.enable; }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*     */     return this.name;
/*     */   }
/*     */   
/*     */   public Level getLevel() {
/*  74 */     return this.level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevel(Level level) {
/*  81 */     this.level = level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(LogEvent logEvent) {
/*  88 */     if (!this.enable) {
/*     */       return;
/*     */     }
/*     */     
/*  92 */     if (this.level.code > (logEvent.getLevel()).code) {
/*     */       return;
/*     */     }
/*     */     
/*  96 */     this.real.append(logEvent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 103 */     this.real.stop();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\AppenderHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */