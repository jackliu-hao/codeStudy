/*    */ package com.google.protobuf;
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
/*    */ class GeneratedMessageInfoFactory
/*    */   implements MessageInfoFactory
/*    */ {
/* 37 */   private static final GeneratedMessageInfoFactory instance = new GeneratedMessageInfoFactory();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static GeneratedMessageInfoFactory getInstance() {
/* 43 */     return instance;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSupported(Class<?> messageType) {
/* 48 */     return GeneratedMessageLite.class.isAssignableFrom(messageType);
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageInfo messageInfoFor(Class<?> messageType) {
/* 53 */     if (!GeneratedMessageLite.class.isAssignableFrom(messageType)) {
/* 54 */       throw new IllegalArgumentException("Unsupported message type: " + messageType.getName());
/*    */     }
/*    */     
/*    */     try {
/* 58 */       return (MessageInfo)GeneratedMessageLite.<GeneratedMessageLite<?, ?>>getDefaultInstance((Class)messageType
/* 59 */           .asSubclass((Class)GeneratedMessageLite.class))
/* 60 */         .buildMessageInfo();
/* 61 */     } catch (Exception e) {
/* 62 */       throw new RuntimeException("Unable to get message info for " + messageType.getName(), e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\GeneratedMessageInfoFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */