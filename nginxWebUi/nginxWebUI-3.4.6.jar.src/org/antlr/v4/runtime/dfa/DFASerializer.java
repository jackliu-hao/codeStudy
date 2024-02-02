/*     */ package org.antlr.v4.runtime.dfa;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.Vocabulary;
/*     */ import org.antlr.v4.runtime.VocabularyImpl;
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
/*     */ public class DFASerializer
/*     */ {
/*     */   private final DFA dfa;
/*     */   private final Vocabulary vocabulary;
/*     */   
/*     */   @Deprecated
/*     */   public DFASerializer(DFA dfa, String[] tokenNames) {
/*  51 */     this(dfa, VocabularyImpl.fromTokenNames(tokenNames));
/*     */   }
/*     */   
/*     */   public DFASerializer(DFA dfa, Vocabulary vocabulary) {
/*  55 */     this.dfa = dfa;
/*  56 */     this.vocabulary = vocabulary;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  61 */     if (this.dfa.s0 == null) return null; 
/*  62 */     StringBuilder buf = new StringBuilder();
/*  63 */     List<DFAState> states = this.dfa.getStates();
/*  64 */     for (DFAState s : states) {
/*  65 */       int n = 0;
/*  66 */       if (s.edges != null) n = s.edges.length; 
/*  67 */       for (int i = 0; i < n; i++) {
/*  68 */         DFAState t = s.edges[i];
/*  69 */         if (t != null && t.stateNumber != Integer.MAX_VALUE) {
/*  70 */           buf.append(getStateString(s));
/*  71 */           String label = getEdgeLabel(i);
/*  72 */           buf.append("-").append(label).append("->").append(getStateString(t)).append('\n');
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  77 */     String output = buf.toString();
/*  78 */     if (output.length() == 0) return null;
/*     */     
/*  80 */     return output;
/*     */   }
/*     */   
/*     */   protected String getEdgeLabel(int i) {
/*  84 */     return this.vocabulary.getDisplayName(i - 1);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getStateString(DFAState s) {
/*  89 */     int n = s.stateNumber;
/*  90 */     String baseStateStr = (s.isAcceptState ? ":" : "") + "s" + n + (s.requiresFullContext ? "^" : "");
/*  91 */     if (s.isAcceptState) {
/*  92 */       if (s.predicates != null) {
/*  93 */         return baseStateStr + "=>" + Arrays.toString((Object[])s.predicates);
/*     */       }
/*     */       
/*  96 */       return baseStateStr + "=>" + s.prediction;
/*     */     } 
/*     */ 
/*     */     
/* 100 */     return baseStateStr;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\dfa\DFASerializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */