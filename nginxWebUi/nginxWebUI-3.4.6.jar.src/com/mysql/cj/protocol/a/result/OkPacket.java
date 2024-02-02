/*     */ package com.mysql.cj.protocol.a.result;
/*     */ 
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.NativeServerSessionStateController;
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
/*     */ public class OkPacket
/*     */   implements ProtocolEntity
/*     */ {
/*  41 */   private long updateCount = -1L;
/*  42 */   private long updateID = -1L;
/*  43 */   private int statusFlags = 0;
/*  44 */   private int warningCount = 0;
/*  45 */   private String info = null;
/*     */   private NativeServerSessionStateController.NativeServerSessionStateChanges sessionStateChanges;
/*     */   
/*     */   private OkPacket() {
/*  49 */     this.sessionStateChanges = new NativeServerSessionStateController.NativeServerSessionStateChanges();
/*     */   }
/*     */   
/*     */   public static OkPacket parse(NativePacketPayload buf, String errorMessageEncoding) {
/*  53 */     OkPacket ok = new OkPacket();
/*     */     
/*  55 */     buf.setPosition(1);
/*     */ 
/*     */     
/*  58 */     ok.setUpdateCount(buf.readInteger(NativeConstants.IntegerDataType.INT_LENENC));
/*  59 */     ok.setUpdateID(buf.readInteger(NativeConstants.IntegerDataType.INT_LENENC));
/*  60 */     ok.setStatusFlags((int)buf.readInteger(NativeConstants.IntegerDataType.INT2));
/*  61 */     ok.setWarningCount((int)buf.readInteger(NativeConstants.IntegerDataType.INT2));
/*  62 */     ok.setInfo(buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, errorMessageEncoding));
/*     */ 
/*     */     
/*  65 */     if ((ok.getStatusFlags() & 0x4000) > 0) {
/*  66 */       ok.sessionStateChanges.init(buf, errorMessageEncoding);
/*     */     }
/*  68 */     return ok;
/*     */   }
/*     */   
/*     */   public long getUpdateCount() {
/*  72 */     return this.updateCount;
/*     */   }
/*     */   
/*     */   public void setUpdateCount(long updateCount) {
/*  76 */     this.updateCount = updateCount;
/*     */   }
/*     */   
/*     */   public long getUpdateID() {
/*  80 */     return this.updateID;
/*     */   }
/*     */   
/*     */   public void setUpdateID(long updateID) {
/*  84 */     this.updateID = updateID;
/*     */   }
/*     */   
/*     */   public String getInfo() {
/*  88 */     return this.info;
/*     */   }
/*     */   
/*     */   public void setInfo(String info) {
/*  92 */     this.info = info;
/*     */   }
/*     */   
/*     */   public int getStatusFlags() {
/*  96 */     return this.statusFlags;
/*     */   }
/*     */   
/*     */   public void setStatusFlags(int statusFlags) {
/* 100 */     this.statusFlags = statusFlags;
/*     */   }
/*     */   
/*     */   public int getWarningCount() {
/* 104 */     return this.warningCount;
/*     */   }
/*     */   
/*     */   public void setWarningCount(int warningCount) {
/* 108 */     this.warningCount = warningCount;
/*     */   }
/*     */   
/*     */   public NativeServerSessionStateController.NativeServerSessionStateChanges getSessionStateChanges() {
/* 112 */     return this.sessionStateChanges;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\OkPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */