/*     */ package ch.qos.logback.classic.net;
/*     */ 
/*     */ import ch.qos.logback.classic.PatternLayout;
/*     */ import ch.qos.logback.classic.pattern.SyslogStartConverter;
/*     */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*     */ import ch.qos.logback.classic.spi.IThrowableProxy;
/*     */ import ch.qos.logback.classic.spi.StackTraceElementProxy;
/*     */ import ch.qos.logback.classic.util.LevelToSyslogSeverity;
/*     */ import ch.qos.logback.core.Layout;
/*     */ import ch.qos.logback.core.net.SyslogAppenderBase;
/*     */ import ch.qos.logback.core.net.SyslogOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
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
/*     */ public class SyslogAppender
/*     */   extends SyslogAppenderBase<ILoggingEvent>
/*     */ {
/*     */   public static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";
/*     */   public static final String DEFAULT_STACKTRACE_PATTERN = "\t";
/*  44 */   PatternLayout stackTraceLayout = new PatternLayout();
/*  45 */   String stackTracePattern = "\t";
/*     */   
/*     */   boolean throwableExcluded = false;
/*     */   
/*     */   public void start() {
/*  50 */     super.start();
/*  51 */     setupStackTraceLayout();
/*     */   }
/*     */   
/*     */   String getPrefixPattern() {
/*  55 */     return "%syslogStart{" + getFacility() + "}%nopex{}";
/*     */   }
/*     */ 
/*     */   
/*     */   public SyslogOutputStream createOutputStream() throws SocketException, UnknownHostException {
/*  60 */     return new SyslogOutputStream(getSyslogHost(), getPort());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSeverityForEvent(Object eventObject) {
/*  71 */     ILoggingEvent event = (ILoggingEvent)eventObject;
/*  72 */     return LevelToSyslogSeverity.convert(event);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void postProcess(Object eventObject, OutputStream sw) {
/*  77 */     if (this.throwableExcluded) {
/*     */       return;
/*     */     }
/*  80 */     ILoggingEvent event = (ILoggingEvent)eventObject;
/*  81 */     IThrowableProxy tp = event.getThrowableProxy();
/*     */     
/*  83 */     if (tp == null) {
/*     */       return;
/*     */     }
/*  86 */     String stackTracePrefix = this.stackTraceLayout.doLayout(event);
/*  87 */     boolean isRootException = true;
/*  88 */     while (tp != null) {
/*  89 */       StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
/*     */       try {
/*  91 */         handleThrowableFirstLine(sw, tp, stackTracePrefix, isRootException);
/*  92 */         isRootException = false;
/*  93 */         for (StackTraceElementProxy step : stepArray) {
/*  94 */           StringBuilder sb = new StringBuilder();
/*  95 */           sb.append(stackTracePrefix).append(step);
/*  96 */           sw.write(sb.toString().getBytes());
/*  97 */           sw.flush();
/*     */         } 
/*  99 */       } catch (IOException e) {
/*     */         break;
/*     */       } 
/* 102 */       tp = tp.getCause();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleThrowableFirstLine(OutputStream sw, IThrowableProxy tp, String stackTracePrefix, boolean isRootException) throws IOException {
/* 108 */     StringBuilder sb = (new StringBuilder()).append(stackTracePrefix);
/*     */     
/* 110 */     if (!isRootException) {
/* 111 */       sb.append("Caused by: ");
/*     */     }
/* 113 */     sb.append(tp.getClassName()).append(": ").append(tp.getMessage());
/* 114 */     sw.write(sb.toString().getBytes());
/* 115 */     sw.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   boolean stackTraceHeaderLine(StringBuilder sb, boolean topException) {
/* 120 */     return false;
/*     */   }
/*     */   
/*     */   public Layout<ILoggingEvent> buildLayout() {
/* 124 */     PatternLayout layout = new PatternLayout();
/* 125 */     layout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
/* 126 */     if (this.suffixPattern == null) {
/* 127 */       this.suffixPattern = "[%thread] %logger %msg";
/*     */     }
/* 129 */     layout.setPattern(getPrefixPattern() + this.suffixPattern);
/* 130 */     layout.setContext(getContext());
/* 131 */     layout.start();
/* 132 */     return (Layout<ILoggingEvent>)layout;
/*     */   }
/*     */   
/*     */   private void setupStackTraceLayout() {
/* 136 */     this.stackTraceLayout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
/*     */     
/* 138 */     this.stackTraceLayout.setPattern(getPrefixPattern() + this.stackTracePattern);
/* 139 */     this.stackTraceLayout.setContext(getContext());
/* 140 */     this.stackTraceLayout.start();
/*     */   }
/*     */   
/*     */   public boolean isThrowableExcluded() {
/* 144 */     return this.throwableExcluded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThrowableExcluded(boolean throwableExcluded) {
/* 155 */     this.throwableExcluded = throwableExcluded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStackTracePattern() {
/* 165 */     return this.stackTracePattern;
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
/*     */   public void setStackTracePattern(String stackTracePattern) {
/* 178 */     this.stackTracePattern = stackTracePattern;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\SyslogAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */