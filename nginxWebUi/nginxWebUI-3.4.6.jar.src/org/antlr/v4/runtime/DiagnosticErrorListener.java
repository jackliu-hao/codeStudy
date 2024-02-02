/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import org.antlr.v4.runtime.atn.ATNConfig;
/*     */ import org.antlr.v4.runtime.atn.ATNConfigSet;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ import org.antlr.v4.runtime.misc.Interval;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DiagnosticErrorListener
/*     */   extends BaseErrorListener
/*     */ {
/*     */   protected final boolean exactOnly;
/*     */   
/*     */   public DiagnosticErrorListener() {
/*  72 */     this(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DiagnosticErrorListener(boolean exactOnly) {
/*  83 */     this.exactOnly = exactOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
/*  95 */     if (this.exactOnly && !exact) {
/*     */       return;
/*     */     }
/*     */     
/*  99 */     String format = "reportAmbiguity d=%s: ambigAlts=%s, input='%s'";
/* 100 */     String decision = getDecisionDescription(recognizer, dfa);
/* 101 */     BitSet conflictingAlts = getConflictingAlts(ambigAlts, configs);
/* 102 */     String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
/* 103 */     String message = String.format(format, new Object[] { decision, conflictingAlts, text });
/* 104 */     recognizer.notifyErrorListeners(message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
/* 115 */     String format = "reportAttemptingFullContext d=%s, input='%s'";
/* 116 */     String decision = getDecisionDescription(recognizer, dfa);
/* 117 */     String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
/* 118 */     String message = String.format(format, new Object[] { decision, text });
/* 119 */     recognizer.notifyErrorListeners(message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
/* 130 */     String format = "reportContextSensitivity d=%s, input='%s'";
/* 131 */     String decision = getDecisionDescription(recognizer, dfa);
/* 132 */     String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
/* 133 */     String message = String.format(format, new Object[] { decision, text });
/* 134 */     recognizer.notifyErrorListeners(message);
/*     */   }
/*     */   
/*     */   protected String getDecisionDescription(Parser recognizer, DFA dfa) {
/* 138 */     int decision = dfa.decision;
/* 139 */     int ruleIndex = dfa.atnStartState.ruleIndex;
/*     */     
/* 141 */     String[] ruleNames = recognizer.getRuleNames();
/* 142 */     if (ruleIndex < 0 || ruleIndex >= ruleNames.length) {
/* 143 */       return String.valueOf(decision);
/*     */     }
/*     */     
/* 146 */     String ruleName = ruleNames[ruleIndex];
/* 147 */     if (ruleName == null || ruleName.isEmpty()) {
/* 148 */       return String.valueOf(decision);
/*     */     }
/*     */     
/* 151 */     return String.format("%d (%s)", new Object[] { Integer.valueOf(decision), ruleName });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BitSet getConflictingAlts(BitSet reportedAlts, ATNConfigSet configs) {
/* 166 */     if (reportedAlts != null) {
/* 167 */       return reportedAlts;
/*     */     }
/*     */     
/* 170 */     BitSet result = new BitSet();
/* 171 */     for (ATNConfig config : configs) {
/* 172 */       result.set(config.alt);
/*     */     }
/*     */     
/* 175 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\DiagnosticErrorListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */