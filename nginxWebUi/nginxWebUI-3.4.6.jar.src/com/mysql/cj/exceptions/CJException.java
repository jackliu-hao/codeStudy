/*     */ package com.mysql.cj.exceptions;
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
/*     */ public class CJException
/*     */   extends RuntimeException
/*     */ {
/*     */   private static final long serialVersionUID = -8618536991444733607L;
/*     */   protected String exceptionMessage;
/*  45 */   private String SQLState = "S1000";
/*     */   
/*  47 */   private int vendorCode = 0;
/*     */   
/*     */   private boolean isTransient = false;
/*     */ 
/*     */   
/*     */   public CJException() {}
/*     */ 
/*     */   
/*     */   public CJException(String message) {
/*  56 */     super(message);
/*     */   }
/*     */   
/*     */   public CJException(Throwable cause) {
/*  60 */     super(cause);
/*     */   }
/*     */   
/*     */   public CJException(String message, Throwable cause) {
/*  64 */     super(message, cause);
/*     */   }
/*     */   
/*     */   protected CJException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/*  68 */     super(message, cause, enableSuppression, writableStackTrace);
/*     */   }
/*     */   
/*     */   public String getSQLState() {
/*  72 */     return this.SQLState;
/*     */   }
/*     */   
/*     */   public void setSQLState(String sQLState) {
/*  76 */     this.SQLState = sQLState;
/*     */   }
/*     */   
/*     */   public int getVendorCode() {
/*  80 */     return this.vendorCode;
/*     */   }
/*     */   
/*     */   public void setVendorCode(int vendorCode) {
/*  84 */     this.vendorCode = vendorCode;
/*     */   }
/*     */   
/*     */   public boolean isTransient() {
/*  88 */     return this.isTransient;
/*     */   }
/*     */   
/*     */   public void setTransient(boolean isTransient) {
/*  92 */     this.isTransient = isTransient;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMessage() {
/*  97 */     return (this.exceptionMessage != null) ? this.exceptionMessage : super.getMessage();
/*     */   }
/*     */   
/*     */   public void appendMessage(String messageToAppend) {
/* 101 */     this.exceptionMessage = getMessage() + messageToAppend;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\CJException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */