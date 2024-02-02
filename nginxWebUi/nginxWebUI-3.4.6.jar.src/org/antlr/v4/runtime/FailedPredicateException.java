/*    */ package org.antlr.v4.runtime;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import org.antlr.v4.runtime.atn.ATNState;
/*    */ import org.antlr.v4.runtime.atn.AbstractPredicateTransition;
/*    */ import org.antlr.v4.runtime.atn.PredicateTransition;
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
/*    */ public class FailedPredicateException
/*    */   extends RecognitionException
/*    */ {
/*    */   private final int ruleIndex;
/*    */   private final int predicateIndex;
/*    */   private final String predicate;
/*    */   
/*    */   public FailedPredicateException(Parser recognizer) {
/* 49 */     this(recognizer, null);
/*    */   }
/*    */   
/*    */   public FailedPredicateException(Parser recognizer, String predicate) {
/* 53 */     this(recognizer, predicate, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FailedPredicateException(Parser recognizer, String predicate, String message) {
/* 60 */     super(formatMessage(predicate, message), recognizer, recognizer.getInputStream(), recognizer._ctx);
/* 61 */     ATNState s = (recognizer.getInterpreter()).atn.states.get(recognizer.getState());
/*    */     
/* 63 */     AbstractPredicateTransition trans = (AbstractPredicateTransition)s.transition(0);
/* 64 */     if (trans instanceof PredicateTransition) {
/* 65 */       this.ruleIndex = ((PredicateTransition)trans).ruleIndex;
/* 66 */       this.predicateIndex = ((PredicateTransition)trans).predIndex;
/*    */     } else {
/*    */       
/* 69 */       this.ruleIndex = 0;
/* 70 */       this.predicateIndex = 0;
/*    */     } 
/*    */     
/* 73 */     this.predicate = predicate;
/* 74 */     setOffendingToken(recognizer.getCurrentToken());
/*    */   }
/*    */   
/*    */   public int getRuleIndex() {
/* 78 */     return this.ruleIndex;
/*    */   }
/*    */   
/*    */   public int getPredIndex() {
/* 82 */     return this.predicateIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPredicate() {
/* 87 */     return this.predicate;
/*    */   }
/*    */ 
/*    */   
/*    */   private static String formatMessage(String predicate, String message) {
/* 92 */     if (message != null) {
/* 93 */       return message;
/*    */     }
/*    */     
/* 96 */     return String.format(Locale.getDefault(), "failed predicate: {%s}?", new Object[] { predicate });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\FailedPredicateException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */