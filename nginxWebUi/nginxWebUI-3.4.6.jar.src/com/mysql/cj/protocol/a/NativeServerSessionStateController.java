/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.protocol.ServerSessionStateController;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class NativeServerSessionStateController
/*     */   implements ServerSessionStateController
/*     */ {
/*     */   private NativeServerSessionStateChanges sessionStateChanges;
/*     */   private List<WeakReference<ServerSessionStateController.SessionStateChangesListener>> listeners;
/*     */   
/*     */   public void setSessionStateChanges(ServerSessionStateController.ServerSessionStateChanges changes) {
/*  47 */     this.sessionStateChanges = (NativeServerSessionStateChanges)changes;
/*  48 */     if (this.listeners != null) {
/*  49 */       for (WeakReference<ServerSessionStateController.SessionStateChangesListener> wr : this.listeners) {
/*  50 */         ServerSessionStateController.SessionStateChangesListener l = wr.get();
/*  51 */         if (l != null) {
/*  52 */           l.handleSessionStateChanges(changes); continue;
/*     */         } 
/*  54 */         this.listeners.remove(wr);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NativeServerSessionStateChanges getSessionStateChanges() {
/*  62 */     return this.sessionStateChanges;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addSessionStateChangesListener(ServerSessionStateController.SessionStateChangesListener l) {
/*  67 */     if (this.listeners == null) {
/*  68 */       this.listeners = new ArrayList<>();
/*     */     }
/*  70 */     for (WeakReference<ServerSessionStateController.SessionStateChangesListener> wr : this.listeners) {
/*  71 */       if (l.equals(wr.get())) {
/*     */         return;
/*     */       }
/*     */     } 
/*  75 */     this.listeners.add(new WeakReference<>(l));
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeSessionStateChangesListener(ServerSessionStateController.SessionStateChangesListener listener) {
/*  80 */     if (this.listeners != null)
/*  81 */       for (WeakReference<ServerSessionStateController.SessionStateChangesListener> wr : this.listeners) {
/*  82 */         ServerSessionStateController.SessionStateChangesListener l = wr.get();
/*  83 */         if (l == null || l.equals(listener)) {
/*  84 */           this.listeners.remove(wr);
/*     */           break;
/*     */         } 
/*     */       }  
/*     */   }
/*     */   
/*     */   public static class NativeServerSessionStateChanges
/*     */     implements ServerSessionStateController.ServerSessionStateChanges {
/*  92 */     private List<ServerSessionStateController.SessionStateChange> sessionStateChanges = new ArrayList<>();
/*     */ 
/*     */     
/*     */     public List<ServerSessionStateController.SessionStateChange> getSessionStateChangesList() {
/*  96 */       return this.sessionStateChanges;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public NativeServerSessionStateChanges init(NativePacketPayload buf, String encoding) {
/* 103 */       int totalLen = (int)buf.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 104 */       int start = buf.getPosition();
/* 105 */       int end = start + totalLen;
/* 106 */       while (totalLen > 0 && end > start) {
/* 107 */         int type = (int)buf.readInteger(NativeConstants.IntegerDataType.INT1);
/* 108 */         NativePacketPayload b = new NativePacketPayload(buf.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC));
/* 109 */         switch (type) {
/*     */           case 0:
/* 111 */             this.sessionStateChanges.add((new ServerSessionStateController.SessionStateChange(type))
/* 112 */                 .addValue(b.readString(NativeConstants.StringSelfDataType.STRING_LENENC, encoding))
/* 113 */                 .addValue(b.readString(NativeConstants.StringSelfDataType.STRING_LENENC, encoding)));
/*     */             break;
/*     */           case 1:
/*     */           case 4:
/*     */           case 5:
/* 118 */             this.sessionStateChanges.add((new ServerSessionStateController.SessionStateChange(type))
/* 119 */                 .addValue(b.readString(NativeConstants.StringSelfDataType.STRING_LENENC, encoding)));
/*     */             break;
/*     */           case 3:
/* 122 */             b.readInteger(NativeConstants.IntegerDataType.INT1);
/* 123 */             this.sessionStateChanges.add((new ServerSessionStateController.SessionStateChange(type))
/* 124 */                 .addValue(b.readString(NativeConstants.StringSelfDataType.STRING_LENENC, encoding)));
/*     */             break;
/*     */ 
/*     */           
/*     */           default:
/* 129 */             this.sessionStateChanges.add((new ServerSessionStateController.SessionStateChange(type))
/* 130 */                 .addValue(b.readString(NativeConstants.StringLengthDataType.STRING_FIXED, encoding, b.getPayloadLength()))); break;
/*     */         } 
/* 132 */         start = buf.getPosition();
/*     */       } 
/* 134 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativeServerSessionStateController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */