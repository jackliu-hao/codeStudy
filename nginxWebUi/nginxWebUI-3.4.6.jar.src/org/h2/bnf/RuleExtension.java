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
/*    */ 
/*    */ 
/*    */ public class RuleExtension
/*    */   implements Rule
/*    */ {
/*    */   private final Rule rule;
/*    */   private final boolean compatibility;
/*    */   private boolean mapSet;
/*    */   
/*    */   public RuleExtension(Rule paramRule, boolean paramBoolean) {
/* 21 */     this.rule = paramRule;
/* 22 */     this.compatibility = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public void accept(BnfVisitor paramBnfVisitor) {
/* 27 */     paramBnfVisitor.visitRuleExtension(this.rule, this.compatibility);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLinks(HashMap<String, RuleHead> paramHashMap) {
/* 32 */     if (!this.mapSet) {
/* 33 */       this.rule.setLinks(paramHashMap);
/* 34 */       this.mapSet = true;
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean autoComplete(Sentence paramSentence) {
/* 39 */     paramSentence.stopIfRequired();
/* 40 */     this.rule.autoComplete(paramSentence);
/* 41 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     return (this.compatibility ? "@c@ " : "@h2@ ") + this.rule.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\RuleExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */