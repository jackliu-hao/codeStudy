/*     */ package com.sun.jna.platform.mac;
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
/*     */ public class IOReturnException
/*     */   extends RuntimeException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private int ioReturn;
/*     */   
/*     */   public IOReturnException(int kr) {
/*  47 */     this(kr, formatMessage(kr));
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
/*     */   protected IOReturnException(int kr, String msg) {
/*  59 */     super(msg);
/*  60 */     this.ioReturn = kr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIOReturnCode() {
/*  67 */     return this.ioReturn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getSystem(int kr) {
/*  78 */     return kr >> 26 & 0x3F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getSubSystem(int kr) {
/*  89 */     return kr >> 14 & 0xFFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getCode(int kr) {
/* 100 */     return kr & 0x3FFF;
/*     */   }
/*     */   
/*     */   private static String formatMessage(int kr) {
/* 104 */     return "IOReturn error code: " + kr + " (system=" + getSystem(kr) + ", subSystem=" + getSubSystem(kr) + ", code=" + 
/* 105 */       getCode(kr) + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\mac\IOReturnException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */