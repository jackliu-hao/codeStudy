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
/*    */ public class RuleRepeat
/*    */   implements Rule
/*    */ {
/*    */   private final Rule rule;
/*    */   private final boolean comma;
/*    */   
/*    */   public RuleRepeat(Rule paramRule, boolean paramBoolean) {
/* 19 */     this.rule = paramRule;
/* 20 */     this.comma = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public void accept(BnfVisitor paramBnfVisitor) {
/* 25 */     paramBnfVisitor.visitRuleRepeat(this.comma, this.rule);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void setLinks(HashMap<String, RuleHead> paramHashMap) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean autoComplete(Sentence paramSentence) {
/* 35 */     paramSentence.stopIfRequired();
/* 36 */     while (this.rule.autoComplete(paramSentence));
/*    */ 
/*    */     
/* 39 */     String str = paramSentence.getQuery();
/* 40 */     while (Bnf.startWithSpace(str)) {
/* 41 */       str = str.substring(1);
/*    */     }
/* 43 */     paramSentence.setQuery(str);
/* 44 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 49 */     return this.comma ? ", ..." : " ...";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\RuleRepeat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */