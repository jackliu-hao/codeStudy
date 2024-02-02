/*    */ package org.noear.nami.channel.socketd;
/*    */ 
/*    */ import org.noear.nami.Context;
/*    */ import org.noear.nami.NamiManager;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SocketChannelBase
/*    */ {
/*    */   protected void pretreatment(Context ctx) {
/* 20 */     if (ctx.config.getDecoder() == null) {
/* 21 */       String at = ctx.config.getHeader("Accept");
/*    */       
/* 23 */       if (at == null) {
/* 24 */         at = "application/json";
/*    */       }
/*    */       
/* 27 */       ctx.config.setDecoder(NamiManager.getDecoder(at));
/*    */     } 
/*    */     
/* 30 */     if (ctx.config.getEncoder() == null) {
/* 31 */       String ct = ctx.config.getHeader("Content-Type");
/*    */       
/* 33 */       if (ct == null) {
/* 34 */         ct = "application/json";
/*    */       }
/*    */       
/* 37 */       ctx.config.setEncoder(NamiManager.getEncoder(ct));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\nami\channel\socketd\SocketChannelBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */