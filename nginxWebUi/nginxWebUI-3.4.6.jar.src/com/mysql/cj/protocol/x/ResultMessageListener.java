/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.google.protobuf.GeneratedMessageV3;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.MessageListener;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.protocol.ResultBuilder;
/*    */ import com.mysql.cj.x.protobuf.Mysqlx;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ResultMessageListener<R>
/*    */   implements MessageListener<XMessage>
/*    */ {
/*    */   private ResultBuilder<?> resultBuilder;
/*    */   private CompletableFuture<R> future;
/* 51 */   private Map<Class<? extends GeneratedMessageV3>, ProtocolEntityFactory<? extends ProtocolEntity, XMessage>> messageToProtocolEntityFactory = new HashMap<>();
/*    */ 
/*    */ 
/*    */   
/*    */   public ResultMessageListener(Map<Class<? extends GeneratedMessageV3>, ProtocolEntityFactory<? extends ProtocolEntity, XMessage>> messageToProtocolEntityFactory, ResultBuilder<R> resultBuilder, CompletableFuture<R> future) {
/* 56 */     this.messageToProtocolEntityFactory = messageToProtocolEntityFactory;
/* 57 */     this.resultBuilder = resultBuilder;
/* 58 */     this.future = future;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean processMessage(XMessage message) {
/* 63 */     Class<? extends GeneratedMessageV3> msgClass = (Class)message.getMessage().getClass();
/*    */     
/* 65 */     if (Mysqlx.Error.class.equals(msgClass)) {
/* 66 */       this.future.completeExceptionally((Throwable)new XProtocolError(Mysqlx.Error.class.cast(message.getMessage())));
/*    */     }
/* 68 */     else if (!this.messageToProtocolEntityFactory.containsKey(msgClass)) {
/* 69 */       this.future.completeExceptionally((Throwable)new WrongArgumentException("Unhandled msg class (" + msgClass + ") + msg=" + message.getMessage()));
/*    */     } else {
/*    */       
/* 72 */       if (!this.resultBuilder.addProtocolEntity((ProtocolEntity)((ProtocolEntityFactory)this.messageToProtocolEntityFactory.get(msgClass)).createFromMessage(message))) {
/* 73 */         return false;
/*    */       }
/* 75 */       this.future.complete((R)this.resultBuilder.build());
/*    */     } 
/*    */     
/* 78 */     return true;
/*    */   }
/*    */   
/*    */   public void error(Throwable ex) {
/* 82 */     this.future.completeExceptionally(ex);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\ResultMessageListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */