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
/*    */ interface MutabilityOracle
/*    */ {
/* 35 */   public static final MutabilityOracle IMMUTABLE = new MutabilityOracle()
/*    */     {
/*    */       public void ensureMutable()
/*    */       {
/* 39 */         throw new UnsupportedOperationException();
/*    */       }
/*    */     };
/*    */   
/*    */   void ensureMutable();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MutabilityOracle.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */