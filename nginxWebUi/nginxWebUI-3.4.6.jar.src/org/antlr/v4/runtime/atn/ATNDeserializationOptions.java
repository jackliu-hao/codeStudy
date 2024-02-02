/*    */ package org.antlr.v4.runtime.atn;
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
/*    */ public class ATNDeserializationOptions
/*    */ {
/* 40 */   private static final ATNDeserializationOptions defaultOptions = new ATNDeserializationOptions(); static {
/* 41 */     defaultOptions.makeReadOnly();
/*    */   }
/*    */   
/*    */   private boolean readOnly;
/*    */   private boolean verifyATN;
/*    */   private boolean generateRuleBypassTransitions;
/*    */   
/*    */   public ATNDeserializationOptions() {
/* 49 */     this.verifyATN = true;
/* 50 */     this.generateRuleBypassTransitions = false;
/*    */   }
/*    */   
/*    */   public ATNDeserializationOptions(ATNDeserializationOptions options) {
/* 54 */     this.verifyATN = options.verifyATN;
/* 55 */     this.generateRuleBypassTransitions = options.generateRuleBypassTransitions;
/*    */   }
/*    */ 
/*    */   
/*    */   public static ATNDeserializationOptions getDefaultOptions() {
/* 60 */     return defaultOptions;
/*    */   }
/*    */   
/*    */   public final boolean isReadOnly() {
/* 64 */     return this.readOnly;
/*    */   }
/*    */   
/*    */   public final void makeReadOnly() {
/* 68 */     this.readOnly = true;
/*    */   }
/*    */   
/*    */   public final boolean isVerifyATN() {
/* 72 */     return this.verifyATN;
/*    */   }
/*    */   
/*    */   public final void setVerifyATN(boolean verifyATN) {
/* 76 */     throwIfReadOnly();
/* 77 */     this.verifyATN = verifyATN;
/*    */   }
/*    */   
/*    */   public final boolean isGenerateRuleBypassTransitions() {
/* 81 */     return this.generateRuleBypassTransitions;
/*    */   }
/*    */   
/*    */   public final void setGenerateRuleBypassTransitions(boolean generateRuleBypassTransitions) {
/* 85 */     throwIfReadOnly();
/* 86 */     this.generateRuleBypassTransitions = generateRuleBypassTransitions;
/*    */   }
/*    */   
/*    */   protected void throwIfReadOnly() {
/* 90 */     if (isReadOnly())
/* 91 */       throw new IllegalStateException("The object is read only."); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ATNDeserializationOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */