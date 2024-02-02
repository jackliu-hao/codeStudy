/*    */ package com.google.protobuf;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ public class UninitializedMessageException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -7466929953374883507L;
/*    */   private final List<String> missingFields;
/*    */   
/*    */   public UninitializedMessageException(MessageLite message) {
/* 51 */     super("Message was missing required fields.  (Lite runtime could not determine which fields were missing).");
/*    */ 
/*    */     
/* 54 */     this.missingFields = null;
/*    */   }
/*    */   
/*    */   public UninitializedMessageException(List<String> missingFields) {
/* 58 */     super(buildDescription(missingFields));
/* 59 */     this.missingFields = missingFields;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<String> getMissingFields() {
/* 70 */     return Collections.unmodifiableList(this.missingFields);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InvalidProtocolBufferException asInvalidProtocolBufferException() {
/* 79 */     return new InvalidProtocolBufferException(getMessage());
/*    */   }
/*    */ 
/*    */   
/*    */   private static String buildDescription(List<String> missingFields) {
/* 84 */     StringBuilder description = new StringBuilder("Message missing required fields: ");
/* 85 */     boolean first = true;
/* 86 */     for (String field : missingFields) {
/* 87 */       if (first) {
/* 88 */         first = false;
/*    */       } else {
/* 90 */         description.append(", ");
/*    */       } 
/* 92 */       description.append(field);
/*    */     } 
/* 94 */     return description.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UninitializedMessageException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */