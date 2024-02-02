/*     */ package ch.qos.logback.classic.spi;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ public class ThrowableProxyVO
/*     */   implements IThrowableProxy, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -773438177285807139L;
/*     */   private String className;
/*     */   private String message;
/*     */   private int commonFramesCount;
/*     */   private StackTraceElementProxy[] stackTraceElementProxyArray;
/*     */   private IThrowableProxy cause;
/*     */   private IThrowableProxy[] suppressed;
/*     */   
/*     */   public String getMessage() {
/*  31 */     return this.message;
/*     */   }
/*     */   
/*     */   public String getClassName() {
/*  35 */     return this.className;
/*     */   }
/*     */   
/*     */   public int getCommonFrames() {
/*  39 */     return this.commonFramesCount;
/*     */   }
/*     */   
/*     */   public IThrowableProxy getCause() {
/*  43 */     return this.cause;
/*     */   }
/*     */   
/*     */   public StackTraceElementProxy[] getStackTraceElementProxyArray() {
/*  47 */     return this.stackTraceElementProxyArray;
/*     */   }
/*     */   
/*     */   public IThrowableProxy[] getSuppressed() {
/*  51 */     return this.suppressed;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  56 */     int prime = 31;
/*  57 */     int result = 1;
/*  58 */     result = 31 * result + ((this.className == null) ? 0 : this.className.hashCode());
/*  59 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  64 */     if (this == obj)
/*  65 */       return true; 
/*  66 */     if (obj == null)
/*  67 */       return false; 
/*  68 */     if (getClass() != obj.getClass())
/*  69 */       return false; 
/*  70 */     ThrowableProxyVO other = (ThrowableProxyVO)obj;
/*     */     
/*  72 */     if (this.className == null) {
/*  73 */       if (other.className != null)
/*  74 */         return false; 
/*  75 */     } else if (!this.className.equals(other.className)) {
/*  76 */       return false;
/*     */     } 
/*  78 */     if (!Arrays.equals((Object[])this.stackTraceElementProxyArray, (Object[])other.stackTraceElementProxyArray)) {
/*  79 */       return false;
/*     */     }
/*  81 */     if (!Arrays.equals((Object[])this.suppressed, (Object[])other.suppressed)) {
/*  82 */       return false;
/*     */     }
/*  84 */     if (this.cause == null) {
/*  85 */       if (other.cause != null)
/*  86 */         return false; 
/*  87 */     } else if (!this.cause.equals(other.cause)) {
/*  88 */       return false;
/*     */     } 
/*  90 */     return true;
/*     */   }
/*     */   
/*     */   public static ThrowableProxyVO build(IThrowableProxy throwableProxy) {
/*  94 */     if (throwableProxy == null) {
/*  95 */       return null;
/*     */     }
/*  97 */     ThrowableProxyVO tpvo = new ThrowableProxyVO();
/*  98 */     tpvo.className = throwableProxy.getClassName();
/*  99 */     tpvo.message = throwableProxy.getMessage();
/* 100 */     tpvo.commonFramesCount = throwableProxy.getCommonFrames();
/* 101 */     tpvo.stackTraceElementProxyArray = throwableProxy.getStackTraceElementProxyArray();
/* 102 */     IThrowableProxy cause = throwableProxy.getCause();
/* 103 */     if (cause != null) {
/* 104 */       tpvo.cause = build(cause);
/*     */     }
/* 106 */     IThrowableProxy[] suppressed = throwableProxy.getSuppressed();
/* 107 */     if (suppressed != null) {
/* 108 */       tpvo.suppressed = new IThrowableProxy[suppressed.length];
/* 109 */       for (int i = 0; i < suppressed.length; i++) {
/* 110 */         tpvo.suppressed[i] = build(suppressed[i]);
/*     */       }
/*     */     } 
/* 113 */     return tpvo;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\ThrowableProxyVO.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */