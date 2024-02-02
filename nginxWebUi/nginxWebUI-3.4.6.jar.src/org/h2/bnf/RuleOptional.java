/*    */ package org.h2.bnf;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RuleOptional
/*    */   implements Rule
/*    */ {
/*    */   private final Rule rule;
/*    */   private boolean mapSet;
/*    */   
/*    */   public RuleOptional(Rule paramRule) {
/* 18 */     this.rule = paramRule;
/*    */   }
/*    */ 
/*    */   
/*    */   public void accept(BnfVisitor paramBnfVisitor) {
/* 23 */     if (this.rule instanceof RuleList) {
/* 24 */       RuleList ruleList = (RuleList)this.rule;
/* 25 */       if (ruleList.or) {
/* 26 */         paramBnfVisitor.visitRuleOptional(ruleList.list);
/*    */         return;
/*    */       } 
/*    */     } 
/* 30 */     paramBnfVisitor.visitRuleOptional(this.rule);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLinks(HashMap<String, RuleHead> paramHashMap) {
/* 35 */     if (!this.mapSet) {
/* 36 */       this.rule.setLinks(paramHashMap);
/* 37 */       this.mapSet = true;
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean autoComplete(Sentence paramSentence) {
/* 42 */     paramSentence.stopIfRequired();
/* 43 */     this.rule.autoComplete(paramSentence);
/* 44 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 49 */     return '[' + this.rule.toString() + ']';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\RuleOptional.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */