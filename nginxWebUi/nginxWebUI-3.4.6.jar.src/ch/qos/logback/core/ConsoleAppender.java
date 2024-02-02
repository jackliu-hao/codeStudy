/*     */ package ch.qos.logback.core;
/*     */ 
/*     */ import ch.qos.logback.core.joran.spi.ConsoleTarget;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.WarnStatus;
/*     */ import ch.qos.logback.core.util.EnvUtil;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Arrays;
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
/*     */ public class ConsoleAppender<E>
/*     */   extends OutputStreamAppender<E>
/*     */ {
/*  40 */   protected ConsoleTarget target = ConsoleTarget.SystemOut;
/*     */ 
/*     */   
/*     */   protected boolean withJansi = false;
/*     */ 
/*     */   
/*     */   private static final String WindowsAnsiOutputStream_CLASS_NAME = "org.fusesource.jansi.WindowsAnsiOutputStream";
/*     */ 
/*     */   
/*     */   public void setTarget(String value) {
/*  50 */     ConsoleTarget t = ConsoleTarget.findByName(value.trim());
/*  51 */     if (t == null) {
/*  52 */       targetWarn(value);
/*     */     } else {
/*  54 */       this.target = t;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTarget() {
/*  65 */     return this.target.getName();
/*     */   }
/*     */   
/*     */   private void targetWarn(String val) {
/*  69 */     WarnStatus warnStatus = new WarnStatus("[" + val + "] should be one of " + Arrays.toString((Object[])ConsoleTarget.values()), this);
/*  70 */     warnStatus.add((Status)new WarnStatus("Using previously set target, System.out by default.", this));
/*  71 */     addStatus((Status)warnStatus);
/*     */   }
/*     */ 
/*     */   
/*     */   public void start() {
/*  76 */     OutputStream targetStream = this.target.getStream();
/*     */     
/*  78 */     if (EnvUtil.isWindows() && this.withJansi) {
/*  79 */       targetStream = getTargetStreamForWindows(targetStream);
/*     */     }
/*  81 */     setOutputStream(targetStream);
/*  82 */     super.start();
/*     */   }
/*     */   
/*     */   private OutputStream getTargetStreamForWindows(OutputStream targetStream) {
/*     */     try {
/*  87 */       addInfo("Enabling JANSI WindowsAnsiOutputStream for the console.");
/*  88 */       Object windowsAnsiOutputStream = OptionHelper.instantiateByClassNameAndParameter("org.fusesource.jansi.WindowsAnsiOutputStream", Object.class, this.context, OutputStream.class, targetStream);
/*     */       
/*  90 */       return (OutputStream)windowsAnsiOutputStream;
/*  91 */     } catch (Exception e) {
/*  92 */       addWarn("Failed to create WindowsAnsiOutputStream. Falling back on the default stream.", e);
/*     */       
/*  94 */       return targetStream;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWithJansi() {
/* 101 */     return this.withJansi;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWithJansi(boolean withJansi) {
/* 111 */     this.withJansi = withJansi;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\ConsoleAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */