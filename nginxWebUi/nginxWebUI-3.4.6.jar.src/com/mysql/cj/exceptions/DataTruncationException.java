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
/*     */ public class DataTruncationException
/*     */   extends CJException
/*     */ {
/*     */   private static final long serialVersionUID = -5209088385943506720L;
/*     */   private int index;
/*     */   private boolean parameter;
/*     */   private boolean read;
/*     */   private int dataSize;
/*     */   private int transferSize;
/*     */   
/*     */   public DataTruncationException() {}
/*     */   
/*     */   public DataTruncationException(String message) {
/*  47 */     super(message);
/*     */   }
/*     */   
/*     */   public DataTruncationException(String message, Throwable cause) {
/*  51 */     super(message, cause);
/*     */   }
/*     */   
/*     */   public DataTruncationException(Throwable cause) {
/*  55 */     super(cause);
/*     */   }
/*     */   
/*     */   protected DataTruncationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/*  59 */     super(message, cause, enableSuppression, writableStackTrace);
/*     */   }
/*     */   
/*     */   public DataTruncationException(String message, int index, boolean parameter, boolean read, int dataSize, int transferSize, int vendorErrorCode) {
/*  63 */     super(message);
/*  64 */     setIndex(index);
/*  65 */     setParameter(parameter);
/*  66 */     setRead(read);
/*  67 */     setDataSize(dataSize);
/*  68 */     setTransferSize(transferSize);
/*  69 */     setVendorCode(vendorErrorCode);
/*     */   }
/*     */   
/*     */   public int getIndex() {
/*  73 */     return this.index;
/*     */   }
/*     */   
/*     */   public void setIndex(int index) {
/*  77 */     this.index = index;
/*     */   }
/*     */   
/*     */   public boolean isParameter() {
/*  81 */     return this.parameter;
/*     */   }
/*     */   
/*     */   public void setParameter(boolean parameter) {
/*  85 */     this.parameter = parameter;
/*     */   }
/*     */   
/*     */   public boolean isRead() {
/*  89 */     return this.read;
/*     */   }
/*     */   
/*     */   public void setRead(boolean read) {
/*  93 */     this.read = read;
/*     */   }
/*     */   
/*     */   public int getDataSize() {
/*  97 */     return this.dataSize;
/*     */   }
/*     */   
/*     */   public void setDataSize(int dataSize) {
/* 101 */     this.dataSize = dataSize;
/*     */   }
/*     */   
/*     */   public int getTransferSize() {
/* 105 */     return this.transferSize;
/*     */   }
/*     */   
/*     */   public void setTransferSize(int transferSize) {
/* 109 */     this.transferSize = transferSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\DataTruncationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */