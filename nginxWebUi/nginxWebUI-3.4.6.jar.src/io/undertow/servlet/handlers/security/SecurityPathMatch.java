/*    */ package io.undertow.servlet.handlers.security;
/*    */ 
/*    */ import io.undertow.servlet.api.SingleConstraintMatch;
/*    */ import io.undertow.servlet.api.TransportGuaranteeType;
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
/*    */ class SecurityPathMatch
/*    */ {
/*    */   private final TransportGuaranteeType transportGuaranteeType;
/*    */   private final SingleConstraintMatch mergedConstraint;
/*    */   
/*    */   SecurityPathMatch(TransportGuaranteeType transportGuaranteeType, SingleConstraintMatch mergedConstraint) {
/* 32 */     this.transportGuaranteeType = transportGuaranteeType;
/* 33 */     this.mergedConstraint = mergedConstraint;
/*    */   }
/*    */   
/*    */   TransportGuaranteeType getTransportGuaranteeType() {
/* 37 */     return this.transportGuaranteeType;
/*    */   }
/*    */   
/*    */   SingleConstraintMatch getMergedConstraint() {
/* 41 */     return this.mergedConstraint;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\SecurityPathMatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */