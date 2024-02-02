/*    */ package org.h2.bnf;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import org.h2.util.Utils;
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
/*    */ public class RuleList
/*    */   implements Rule
/*    */ {
/*    */   final boolean or;
/* 23 */   final ArrayList<Rule> list = Utils.newSmallArrayList(); public RuleList(Rule paramRule1, Rule paramRule2, boolean paramBoolean) {
/* 24 */     if (paramRule1 instanceof RuleList && ((RuleList)paramRule1).or == paramBoolean) {
/* 25 */       this.list.addAll(((RuleList)paramRule1).list);
/*    */     } else {
/* 27 */       this.list.add(paramRule1);
/*    */     } 
/* 29 */     if (paramRule2 instanceof RuleList && ((RuleList)paramRule2).or == paramBoolean) {
/* 30 */       this.list.addAll(((RuleList)paramRule2).list);
/*    */     } else {
/* 32 */       this.list.add(paramRule2);
/*    */     } 
/* 34 */     this.or = paramBoolean;
/*    */   }
/*    */   private boolean mapSet;
/*    */   
/*    */   public void accept(BnfVisitor paramBnfVisitor) {
/* 39 */     paramBnfVisitor.visitRuleList(this.or, this.list);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLinks(HashMap<String, RuleHead> paramHashMap) {
/* 44 */     if (!this.mapSet) {
/* 45 */       for (Rule rule : this.list) {
/* 46 */         rule.setLinks(paramHashMap);
/*    */       }
/* 48 */       this.mapSet = true;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean autoComplete(Sentence paramSentence) {
/* 54 */     paramSentence.stopIfRequired();
/* 55 */     String str = paramSentence.getQuery();
/* 56 */     if (this.or) {
/* 57 */       for (Rule rule : this.list) {
/* 58 */         paramSentence.setQuery(str);
/* 59 */         if (rule.autoComplete(paramSentence)) {
/* 60 */           return true;
/*    */         }
/*    */       } 
/* 63 */       return false;
/*    */     } 
/* 65 */     for (Rule rule : this.list) {
/* 66 */       if (!rule.autoComplete(paramSentence)) {
/* 67 */         paramSentence.setQuery(str);
/* 68 */         return false;
/*    */       } 
/*    */     } 
/* 71 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     StringBuilder stringBuilder = new StringBuilder(); byte b; int i;
/* 77 */     for (b = 0, i = this.list.size(); b < i; b++) {
/* 78 */       if (b > 0) {
/* 79 */         if (this.or) {
/* 80 */           stringBuilder.append(" | ");
/*    */         } else {
/* 82 */           stringBuilder.append(' ');
/*    */         } 
/*    */       }
/* 85 */       stringBuilder.append(((Rule)this.list.get(b)).toString());
/*    */     } 
/* 87 */     return stringBuilder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\RuleList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */