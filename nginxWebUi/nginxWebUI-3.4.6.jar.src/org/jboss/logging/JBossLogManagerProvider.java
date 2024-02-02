/*     */ package org.jboss.logging;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.jboss.logmanager.LogContext;
/*     */ import org.jboss.logmanager.Logger;
/*     */ import org.jboss.logmanager.MDC;
/*     */ import org.jboss.logmanager.NDC;
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
/*     */ final class JBossLogManagerProvider
/*     */   implements LoggerProvider
/*     */ {
/*  35 */   private static final Logger.AttachmentKey<Logger> KEY = new Logger.AttachmentKey();
/*  36 */   private static final Logger.AttachmentKey<ConcurrentMap<String, Logger>> LEGACY_KEY = new Logger.AttachmentKey();
/*     */   
/*     */   public Logger getLogger(final String name) {
/*  39 */     SecurityManager sm = System.getSecurityManager();
/*  40 */     if (sm != null)
/*  41 */       return AccessController.<Logger>doPrivileged(new PrivilegedAction<Logger>() {
/*     */             public Logger run() {
/*     */               try {
/*  44 */                 return JBossLogManagerProvider.doGetLogger(name);
/*  45 */               } catch (NoSuchMethodError noSuchMethodError) {
/*     */ 
/*     */                 
/*  48 */                 return JBossLogManagerProvider.doLegacyGetLogger(name);
/*     */               } 
/*     */             }
/*     */           }); 
/*     */     try {
/*  53 */       return doGetLogger(name);
/*  54 */     } catch (NoSuchMethodError noSuchMethodError) {
/*     */ 
/*     */       
/*  57 */       return doLegacyGetLogger(name);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Logger doLegacyGetLogger(String name) {
/*  62 */     Logger lmLogger = LogContext.getLogContext().getLogger("");
/*  63 */     ConcurrentMap<String, Logger> loggers = (ConcurrentMap<String, Logger>)lmLogger.getAttachment(LEGACY_KEY);
/*  64 */     if (loggers == null) {
/*  65 */       loggers = new ConcurrentHashMap<>();
/*  66 */       ConcurrentMap<String, Logger> concurrentMap = (ConcurrentMap<String, Logger>)lmLogger.attachIfAbsent(LEGACY_KEY, loggers);
/*  67 */       if (concurrentMap != null) {
/*  68 */         loggers = concurrentMap;
/*     */       }
/*     */     } 
/*     */     
/*  72 */     Logger l = loggers.get(name);
/*  73 */     if (l != null) {
/*  74 */       return l;
/*     */     }
/*     */     
/*  77 */     Logger logger = Logger.getLogger(name);
/*  78 */     l = new JBossLogManagerLogger(name, logger);
/*  79 */     Logger appearing = loggers.putIfAbsent(name, l);
/*  80 */     if (appearing == null) {
/*  81 */       return l;
/*     */     }
/*  83 */     return appearing;
/*     */   }
/*     */   
/*     */   private static Logger doGetLogger(String name) {
/*  87 */     Logger l = (Logger)LogContext.getLogContext().getAttachment(name, KEY);
/*  88 */     if (l != null) {
/*  89 */       return l;
/*     */     }
/*  91 */     Logger logger = Logger.getLogger(name);
/*  92 */     l = new JBossLogManagerLogger(name, logger);
/*  93 */     Logger a = (Logger)logger.attachIfAbsent(KEY, l);
/*  94 */     if (a == null) {
/*  95 */       return l;
/*     */     }
/*  97 */     return a;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearMdc() {
/* 102 */     MDC.clear();
/*     */   }
/*     */   
/*     */   public Object putMdc(String key, Object value) {
/* 106 */     return MDC.put(key, String.valueOf(value));
/*     */   }
/*     */   
/*     */   public Object getMdc(String key) {
/* 110 */     return MDC.get(key);
/*     */   }
/*     */   
/*     */   public void removeMdc(String key) {
/* 114 */     MDC.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Object> getMdcMap() {
/* 120 */     return MDC.copy();
/*     */   }
/*     */   
/*     */   public void clearNdc() {
/* 124 */     NDC.clear();
/*     */   }
/*     */   
/*     */   public String getNdc() {
/* 128 */     return NDC.get();
/*     */   }
/*     */   
/*     */   public int getNdcDepth() {
/* 132 */     return NDC.getDepth();
/*     */   }
/*     */   
/*     */   public String popNdc() {
/* 136 */     return NDC.pop();
/*     */   }
/*     */   
/*     */   public String peekNdc() {
/* 140 */     return NDC.get();
/*     */   }
/*     */   
/*     */   public void pushNdc(String message) {
/* 144 */     NDC.push(message);
/*     */   }
/*     */   
/*     */   public void setNdcMaxDepth(int maxDepth) {
/* 148 */     NDC.trimTo(maxDepth);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\JBossLogManagerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */