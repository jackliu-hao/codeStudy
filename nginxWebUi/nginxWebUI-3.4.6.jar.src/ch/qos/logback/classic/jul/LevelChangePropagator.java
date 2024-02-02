/*     */ package ch.qos.logback.classic.jul;
/*     */ 
/*     */ import ch.qos.logback.classic.Level;
/*     */ import ch.qos.logback.classic.Logger;
/*     */ import ch.qos.logback.classic.LoggerContext;
/*     */ import ch.qos.logback.classic.spi.LoggerContextListener;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.spi.LifeCycle;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.Logger;
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
/*     */ public class LevelChangePropagator
/*     */   extends ContextAwareBase
/*     */   implements LoggerContextListener, LifeCycle
/*     */ {
/*  34 */   private Set<Logger> julLoggerSet = new HashSet<Logger>();
/*     */   boolean isStarted = false;
/*     */   boolean resetJUL = false;
/*     */   
/*     */   public void setResetJUL(boolean resetJUL) {
/*  39 */     this.resetJUL = resetJUL;
/*     */   }
/*     */   
/*     */   public boolean isResetResistant() {
/*  43 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onStart(LoggerContext context) {}
/*     */ 
/*     */   
/*     */   public void onReset(LoggerContext context) {}
/*     */ 
/*     */   
/*     */   public void onStop(LoggerContext context) {}
/*     */   
/*     */   public void onLevelChange(Logger logger, Level level) {
/*  56 */     propagate(logger, level);
/*     */   }
/*     */   
/*     */   private void propagate(Logger logger, Level level) {
/*  60 */     addInfo("Propagating " + level + " level on " + logger + " onto the JUL framework");
/*  61 */     Logger julLogger = JULHelper.asJULLogger(logger);
/*     */ 
/*     */     
/*  64 */     this.julLoggerSet.add(julLogger);
/*  65 */     Level julLevel = JULHelper.asJULLevel(level);
/*  66 */     julLogger.setLevel(julLevel);
/*     */   }
/*     */   
/*     */   public void resetJULLevels() {
/*  70 */     LogManager lm = LogManager.getLogManager();
/*     */     
/*  72 */     Enumeration<String> e = lm.getLoggerNames();
/*  73 */     while (e.hasMoreElements()) {
/*  74 */       String loggerName = e.nextElement();
/*  75 */       Logger julLogger = lm.getLogger(loggerName);
/*  76 */       if (JULHelper.isRegularNonRootLogger(julLogger) && julLogger.getLevel() != null) {
/*  77 */         addInfo("Setting level of jul logger [" + loggerName + "] to null");
/*  78 */         julLogger.setLevel(null);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void propagateExistingLoggerLevels() {
/*  84 */     LoggerContext loggerContext = (LoggerContext)this.context;
/*  85 */     List<Logger> loggerList = loggerContext.getLoggerList();
/*  86 */     for (Logger l : loggerList) {
/*  87 */       if (l.getLevel() != null) {
/*  88 */         propagate(l, l.getLevel());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void start() {
/*  94 */     if (this.resetJUL) {
/*  95 */       resetJULLevels();
/*     */     }
/*  97 */     propagateExistingLoggerLevels();
/*     */     
/*  99 */     this.isStarted = true;
/*     */   }
/*     */   
/*     */   public void stop() {
/* 103 */     this.isStarted = false;
/*     */   }
/*     */   
/*     */   public boolean isStarted() {
/* 107 */     return this.isStarted;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\jul\LevelChangePropagator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */