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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DiscardUnknownFieldsParser
/*    */ {
/*    */   public static final <T extends Message> Parser<T> wrap(final Parser<T> parser) {
/* 55 */     return new AbstractParser<T>()
/*    */       {
/*    */         public T parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*    */         {
/*    */           try {
/* 60 */             input.discardUnknownFields();
/* 61 */             return (T)parser.parsePartialFrom(input, extensionRegistry);
/*    */           } finally {
/* 63 */             input.unsetDiscardUnknownFields();
/*    */           } 
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\DiscardUnknownFieldsParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */