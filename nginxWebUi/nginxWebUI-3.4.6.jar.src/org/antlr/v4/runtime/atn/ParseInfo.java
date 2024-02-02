/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
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
/*     */ public class ParseInfo
/*     */ {
/*     */   protected final ProfilingATNSimulator atnSimulator;
/*     */   
/*     */   public ParseInfo(ProfilingATNSimulator atnSimulator) {
/*  48 */     this.atnSimulator = atnSimulator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DecisionInfo[] getDecisionInfo() {
/*  59 */     return this.atnSimulator.getDecisionInfo();
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
/*     */   public List<Integer> getLLDecisions() {
/*  71 */     DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
/*  72 */     List<Integer> LL = new ArrayList<Integer>();
/*  73 */     for (int i = 0; i < decisions.length; i++) {
/*  74 */       long fallBack = (decisions[i]).LL_Fallback;
/*  75 */       if (fallBack > 0L) LL.add(Integer.valueOf(i)); 
/*     */     } 
/*  77 */     return LL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalTimeInPrediction() {
/*  86 */     DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
/*  87 */     long t = 0L;
/*  88 */     for (int i = 0; i < decisions.length; i++) {
/*  89 */       t += (decisions[i]).timeInPrediction;
/*     */     }
/*  91 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalSLLLookaheadOps() {
/* 100 */     DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
/* 101 */     long k = 0L;
/* 102 */     for (int i = 0; i < decisions.length; i++) {
/* 103 */       k += (decisions[i]).SLL_TotalLook;
/*     */     }
/* 105 */     return k;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalLLLookaheadOps() {
/* 114 */     DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
/* 115 */     long k = 0L;
/* 116 */     for (int i = 0; i < decisions.length; i++) {
/* 117 */       k += (decisions[i]).LL_TotalLook;
/*     */     }
/* 119 */     return k;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalSLLATNLookaheadOps() {
/* 127 */     DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
/* 128 */     long k = 0L;
/* 129 */     for (int i = 0; i < decisions.length; i++) {
/* 130 */       k += (decisions[i]).SLL_ATNTransitions;
/*     */     }
/* 132 */     return k;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalLLATNLookaheadOps() {
/* 140 */     DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
/* 141 */     long k = 0L;
/* 142 */     for (int i = 0; i < decisions.length; i++) {
/* 143 */       k += (decisions[i]).LL_ATNTransitions;
/*     */     }
/* 145 */     return k;
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
/*     */   public long getTotalATNLookaheadOps() {
/* 157 */     DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
/* 158 */     long k = 0L;
/* 159 */     for (int i = 0; i < decisions.length; i++) {
/* 160 */       k += (decisions[i]).SLL_ATNTransitions;
/* 161 */       k += (decisions[i]).LL_ATNTransitions;
/*     */     } 
/* 163 */     return k;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDFASize() {
/* 171 */     int n = 0;
/* 172 */     DFA[] decisionToDFA = this.atnSimulator.decisionToDFA;
/* 173 */     for (int i = 0; i < decisionToDFA.length; i++) {
/* 174 */       n += getDFASize(i);
/*     */     }
/* 176 */     return n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDFASize(int decision) {
/* 184 */     DFA decisionToDFA = this.atnSimulator.decisionToDFA[decision];
/* 185 */     return decisionToDFA.states.size();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ParseInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */