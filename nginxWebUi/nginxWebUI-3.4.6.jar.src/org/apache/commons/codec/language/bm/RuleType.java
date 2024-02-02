/*    */ package org.apache.commons.codec.language.bm;
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
/*    */ public enum RuleType
/*    */ {
/* 28 */   APPROX("approx"),
/*    */   
/* 30 */   EXACT("exact"),
/*    */   
/* 32 */   RULES("rules");
/*    */   
/*    */   private final String name;
/*    */   
/*    */   RuleType(String name) {
/* 37 */     this.name = name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 46 */     return this.name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\bm\RuleType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */