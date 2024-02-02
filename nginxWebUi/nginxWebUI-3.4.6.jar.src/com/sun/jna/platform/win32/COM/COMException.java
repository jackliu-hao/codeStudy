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
/*     */ 
/*     */ public class COMException
/*     */   extends RuntimeException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final WinNT.HRESULT hresult;
/*     */   
/*     */   public COMException() {
/*  42 */     this("", (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public COMException(String message) {
/*  52 */     this(message, (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public COMException(Throwable cause) {
/*  63 */     this((String)null, cause);
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
/*     */   public COMException(String message, Throwable cause) {
/*  75 */     super(message, cause);
/*  76 */     this.hresult = null;
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
/*     */   public COMException(String message, WinNT.HRESULT hresult) {
/*  88 */     super(message);
/*  89 */     this.hresult = hresult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT getHresult() {
/*  96 */     return this.hresult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matchesErrorCode(int errorCode) {
/* 105 */     return (this.hresult != null && this.hresult.intValue() == errorCode);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\COMException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */