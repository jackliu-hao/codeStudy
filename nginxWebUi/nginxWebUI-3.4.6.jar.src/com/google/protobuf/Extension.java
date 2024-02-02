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
/*    */ public abstract class Extension<ContainingType extends MessageLite, Type>
/*    */   extends ExtensionLite<ContainingType, Type>
/*    */ {
/*    */   final boolean isLite() {
/* 53 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected enum ExtensionType
/*    */   {
/* 60 */     IMMUTABLE,
/* 61 */     MUTABLE,
/* 62 */     PROTO1;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public enum MessageType
/*    */   {
/* 69 */     PROTO1,
/* 70 */     PROTO2;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MessageType getMessageType() {
/* 78 */     return MessageType.PROTO2;
/*    */   }
/*    */   
/*    */   public abstract Message getMessageDefaultInstance();
/*    */   
/*    */   public abstract Descriptors.FieldDescriptor getDescriptor();
/*    */   
/*    */   protected abstract ExtensionType getExtensionType();
/*    */   
/*    */   protected abstract Object fromReflectionType(Object paramObject);
/*    */   
/*    */   protected abstract Object singularFromReflectionType(Object paramObject);
/*    */   
/*    */   protected abstract Object toReflectionType(Object paramObject);
/*    */   
/*    */   protected abstract Object singularToReflectionType(Object paramObject);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Extension.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */