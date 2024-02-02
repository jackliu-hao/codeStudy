/*    */ package org.antlr.v4.runtime;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RuleContextWithAltNum
/*    */   extends ParserRuleContext
/*    */ {
/*    */   public int altNum;
/*    */   
/*    */   public RuleContextWithAltNum() {
/* 17 */     this.altNum = 0;
/*    */   }
/*    */   public RuleContextWithAltNum(ParserRuleContext parent, int invokingStateNumber) {
/* 20 */     super(parent, invokingStateNumber);
/*    */   }
/* 22 */   public int getAltNumber() { return this.altNum; } public void setAltNumber(int altNum) {
/* 23 */     this.altNum = altNum;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\RuleContextWithAltNum.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */