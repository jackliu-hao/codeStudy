/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.io.StringWriter;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogRecord;
/*     */ import java.util.logging.Logger;
/*     */ import org.apache.commons.logging.Log;
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
/*     */ public class Jdk13LumberjackLogger
/*     */   implements Log, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -8649807923527610591L;
/*  48 */   protected transient Logger logger = null;
/*  49 */   protected String name = null;
/*  50 */   private String sourceClassName = "unknown";
/*  51 */   private String sourceMethodName = "unknown";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean classAndMethodFound = false;
/*     */ 
/*     */ 
/*     */   
/*  60 */   protected static final Level dummyLevel = Level.FINE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jdk13LumberjackLogger(String name) {
/*  70 */     this.name = name;
/*  71 */     this.logger = getLogger();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void log(Level level, String msg, Throwable ex) {
/*  77 */     if (getLogger().isLoggable(level)) {
/*  78 */       LogRecord record = new LogRecord(level, msg);
/*  79 */       if (!this.classAndMethodFound) {
/*  80 */         getClassAndMethod();
/*     */       }
/*  82 */       record.setSourceClassName(this.sourceClassName);
/*  83 */       record.setSourceMethodName(this.sourceMethodName);
/*  84 */       if (ex != null) {
/*  85 */         record.setThrown(ex);
/*     */       }
/*  87 */       getLogger().log(record);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void getClassAndMethod() {
/*     */     try {
/*  97 */       Throwable throwable = new Throwable();
/*  98 */       throwable.fillInStackTrace();
/*  99 */       StringWriter stringWriter = new StringWriter();
/* 100 */       PrintWriter printWriter = new PrintWriter(stringWriter);
/* 101 */       throwable.printStackTrace(printWriter);
/* 102 */       String traceString = stringWriter.getBuffer().toString();
/* 103 */       StringTokenizer tokenizer = new StringTokenizer(traceString, "\n");
/*     */       
/* 105 */       tokenizer.nextToken();
/* 106 */       String line = tokenizer.nextToken();
/* 107 */       while (line.indexOf(getClass().getName()) == -1) {
/* 108 */         line = tokenizer.nextToken();
/*     */       }
/* 110 */       while (line.indexOf(getClass().getName()) >= 0) {
/* 111 */         line = tokenizer.nextToken();
/*     */       }
/* 113 */       int start = line.indexOf("at ") + 3;
/* 114 */       int end = line.indexOf('(');
/* 115 */       String temp = line.substring(start, end);
/* 116 */       int lastPeriod = temp.lastIndexOf('.');
/* 117 */       this.sourceClassName = temp.substring(0, lastPeriod);
/* 118 */       this.sourceMethodName = temp.substring(lastPeriod + 1);
/* 119 */     } catch (Exception ex) {}
/*     */ 
/*     */     
/* 122 */     this.classAndMethodFound = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(Object message) {
/* 132 */     log(Level.FINE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(Object message, Throwable exception) {
/* 143 */     log(Level.FINE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(Object message) {
/* 153 */     log(Level.SEVERE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(Object message, Throwable exception) {
/* 164 */     log(Level.SEVERE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fatal(Object message) {
/* 174 */     log(Level.SEVERE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fatal(Object message, Throwable exception) {
/* 185 */     log(Level.SEVERE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getLogger() {
/* 192 */     if (this.logger == null) {
/* 193 */       this.logger = Logger.getLogger(this.name);
/*     */     }
/* 195 */     return this.logger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(Object message) {
/* 205 */     log(Level.INFO, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(Object message, Throwable exception) {
/* 216 */     log(Level.INFO, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 223 */     return getLogger().isLoggable(Level.FINE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 230 */     return getLogger().isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFatalEnabled() {
/* 237 */     return getLogger().isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 244 */     return getLogger().isLoggable(Level.INFO);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/* 251 */     return getLogger().isLoggable(Level.FINEST);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 258 */     return getLogger().isLoggable(Level.WARNING);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void trace(Object message) {
/* 268 */     log(Level.FINEST, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void trace(Object message, Throwable exception) {
/* 279 */     log(Level.FINEST, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(Object message) {
/* 289 */     log(Level.WARNING, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(Object message, Throwable exception) {
/* 300 */     log(Level.WARNING, String.valueOf(message), exception);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\Jdk13LumberjackLogger.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */