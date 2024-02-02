/*     */ package cn.hutool.script;
/*     */ 
/*     */ import cn.hutool.core.exceptions.ExceptionUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import javax.script.ScriptException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ScriptRuntimeException
/*     */   extends RuntimeException
/*     */ {
/*     */   private static final long serialVersionUID = 8247610319171014183L;
/*     */   private String fileName;
/*  17 */   private int lineNumber = -1;
/*  18 */   private int columnNumber = -1;
/*     */   
/*     */   public ScriptRuntimeException(Throwable e) {
/*  21 */     super(ExceptionUtil.getMessage(e), e);
/*     */   }
/*     */   
/*     */   public ScriptRuntimeException(String message) {
/*  25 */     super(message);
/*     */   }
/*     */   
/*     */   public ScriptRuntimeException(String messageTemplate, Object... params) {
/*  29 */     super(StrUtil.format(messageTemplate, params));
/*     */   }
/*     */   
/*     */   public ScriptRuntimeException(String message, Throwable throwable) {
/*  33 */     super(message, throwable);
/*     */   }
/*     */   
/*     */   public ScriptRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/*  37 */     super(message, throwable, enableSuppression, writableStackTrace);
/*     */   }
/*     */   
/*     */   public ScriptRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
/*  41 */     super(StrUtil.format(messageTemplate, params), throwable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ScriptRuntimeException(String message, String fileName, int lineNumber) {
/*  52 */     super(message);
/*  53 */     this.fileName = fileName;
/*  54 */     this.lineNumber = lineNumber;
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
/*     */   public ScriptRuntimeException(String message, String fileName, int lineNumber, int columnNumber) {
/*  66 */     super(message);
/*  67 */     this.fileName = fileName;
/*  68 */     this.lineNumber = lineNumber;
/*  69 */     this.columnNumber = columnNumber;
/*     */   }
/*     */   
/*     */   public ScriptRuntimeException(ScriptException e) {
/*  73 */     super(e);
/*  74 */     this.fileName = e.getFileName();
/*  75 */     this.lineNumber = e.getLineNumber();
/*  76 */     this.columnNumber = e.getColumnNumber();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/*  86 */     StringBuilder ret = (new StringBuilder()).append(super.getMessage());
/*  87 */     if (this.fileName != null) {
/*  88 */       ret.append(" in ").append(this.fileName);
/*  89 */       if (this.lineNumber != -1) {
/*  90 */         ret.append(" at line number ").append(this.lineNumber);
/*     */       }
/*     */       
/*  93 */       if (this.columnNumber != -1) {
/*  94 */         ret.append(" at column number ").append(this.columnNumber);
/*     */       }
/*     */     } 
/*     */     
/*  98 */     return ret.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLineNumber() {
/* 107 */     return this.lineNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnNumber() {
/* 116 */     return this.columnNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFileName() {
/* 126 */     return this.fileName;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\script\ScriptRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */