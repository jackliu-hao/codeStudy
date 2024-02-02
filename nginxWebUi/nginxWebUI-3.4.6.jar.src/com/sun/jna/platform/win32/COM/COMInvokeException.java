/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.platform.win32.WinNT;
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
/*     */ public class COMInvokeException
/*     */   extends COMException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Integer wCode;
/*     */   private final String source;
/*     */   private final String description;
/*     */   private final String helpFile;
/*     */   private final Integer helpContext;
/*     */   private final Integer scode;
/*     */   private final Integer errorArg;
/*     */   
/*     */   public COMInvokeException() {
/*  47 */     this("", (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public COMInvokeException(String message) {
/*  57 */     this(message, (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public COMInvokeException(Throwable cause) {
/*  68 */     this(null, cause);
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
/*     */   public COMInvokeException(String message, Throwable cause) {
/*  80 */     super(message, cause);
/*  81 */     this.description = null;
/*  82 */     this.errorArg = null;
/*  83 */     this.helpContext = null;
/*  84 */     this.helpFile = null;
/*  85 */     this.scode = null;
/*  86 */     this.source = null;
/*  87 */     this.wCode = null;
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
/*     */   public COMInvokeException(String message, WinNT.HRESULT hresult, Integer errorArg, String description, Integer helpContext, String helpFile, Integer scode, String source, Integer wCode) {
/* 114 */     super(formatMessage(hresult, message, errorArg), hresult);
/* 115 */     this.description = description;
/* 116 */     this.errorArg = errorArg;
/* 117 */     this.helpContext = helpContext;
/* 118 */     this.helpFile = helpFile;
/* 119 */     this.scode = scode;
/* 120 */     this.source = source;
/* 121 */     this.wCode = wCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getErrorArg() {
/* 130 */     return this.errorArg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getWCode() {
/* 139 */     return this.wCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSource() {
/* 148 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDescription() {
/* 156 */     return this.description;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHelpFile() {
/* 164 */     return this.helpFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getHelpContext() {
/* 171 */     return this.helpContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getScode() {
/* 180 */     return this.scode;
/*     */   }
/*     */   
/*     */   private static String formatMessage(WinNT.HRESULT hresult, String message, Integer errArg) {
/* 184 */     if (hresult.intValue() == -2147352571 || hresult
/* 185 */       .intValue() == -2147352572) {
/* 186 */       return message + " (puArgErr=" + errArg + ")";
/*     */     }
/* 188 */     return message;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\COMInvokeException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */