/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.antlr.v4.runtime.RuleContext;
/*     */ import org.antlr.v4.runtime.misc.IntervalSet;
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
/*     */ public class LL1Analyzer
/*     */ {
/*     */   public static final int HIT_PRED = 0;
/*     */   public final ATN atn;
/*     */   
/*     */   public LL1Analyzer(ATN atn) {
/*  49 */     this.atn = atn;
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
/*     */   public IntervalSet[] getDecisionLookahead(ATNState s) {
/*  63 */     if (s == null) {
/*  64 */       return null;
/*     */     }
/*     */     
/*  67 */     IntervalSet[] look = new IntervalSet[s.getNumberOfTransitions()];
/*  68 */     for (int alt = 0; alt < s.getNumberOfTransitions(); alt++) {
/*  69 */       look[alt] = new IntervalSet(new int[0]);
/*  70 */       Set<ATNConfig> lookBusy = new HashSet<ATNConfig>();
/*  71 */       boolean seeThruPreds = false;
/*  72 */       _LOOK((s.transition(alt)).target, null, PredictionContext.EMPTY, look[alt], lookBusy, new BitSet(), seeThruPreds, false);
/*     */ 
/*     */ 
/*     */       
/*  76 */       if (look[alt].size() == 0 || look[alt].contains(0)) {
/*  77 */         look[alt] = null;
/*     */       }
/*     */     } 
/*  80 */     return look;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntervalSet LOOK(ATNState s, RuleContext ctx) {
/* 100 */     return LOOK(s, null, ctx);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntervalSet LOOK(ATNState s, ATNState stopState, RuleContext ctx) {
/* 123 */     IntervalSet r = new IntervalSet(new int[0]);
/* 124 */     boolean seeThruPreds = true;
/* 125 */     PredictionContext lookContext = (ctx != null) ? PredictionContext.fromRuleContext(s.atn, ctx) : null;
/* 126 */     _LOOK(s, stopState, lookContext, r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, true);
/*     */     
/* 128 */     return r;
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
/*     */   protected void _LOOK(ATNState s, ATNState stopState, PredictionContext ctx, IntervalSet look, Set<ATNConfig> lookBusy, BitSet calledRuleStack, boolean seeThruPreds, boolean addEOF) {
/* 170 */     ATNConfig c = new ATNConfig(s, 0, ctx);
/* 171 */     if (!lookBusy.add(c))
/*     */       return; 
/* 173 */     if (s == stopState) {
/* 174 */       if (ctx == null) {
/* 175 */         look.add(-2); return;
/*     */       } 
/* 177 */       if (ctx.isEmpty() && addEOF) {
/* 178 */         look.add(-1);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 183 */     if (s instanceof RuleStopState) {
/* 184 */       if (ctx == null) {
/* 185 */         look.add(-2); return;
/*     */       } 
/* 187 */       if (ctx.isEmpty() && addEOF) {
/* 188 */         look.add(-1);
/*     */         
/*     */         return;
/*     */       } 
/* 192 */       if (ctx != PredictionContext.EMPTY) {
/*     */         
/* 194 */         for (int j = 0; j < ctx.size(); j++) {
/* 195 */           ATNState returnState = this.atn.states.get(ctx.getReturnState(j));
/*     */ 
/*     */           
/* 198 */           boolean removed = calledRuleStack.get(returnState.ruleIndex);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 213 */     int n = s.getNumberOfTransitions();
/* 214 */     for (int i = 0; i < n; i++) {
/* 215 */       Transition t = s.transition(i);
/* 216 */       if (t.getClass() == RuleTransition.class) {
/* 217 */         if (!calledRuleStack.get(((RuleTransition)t).target.ruleIndex))
/*     */         {
/*     */ 
/*     */           
/* 221 */           PredictionContext newContext = SingletonPredictionContext.create(ctx, ((RuleTransition)t).followState.stateNumber);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 232 */       else if (t instanceof AbstractPredicateTransition) {
/* 233 */         if (seeThruPreds) {
/* 234 */           _LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
/*     */         } else {
/*     */           
/* 237 */           look.add(0);
/*     */         }
/*     */       
/* 240 */       } else if (t.isEpsilon()) {
/* 241 */         _LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
/*     */       }
/* 243 */       else if (t.getClass() == WildcardTransition.class) {
/* 244 */         look.addAll(IntervalSet.of(1, this.atn.maxTokenType));
/*     */       }
/*     */       else {
/*     */         
/* 248 */         IntervalSet set = t.label();
/* 249 */         if (set != null) {
/* 250 */           if (t instanceof NotSetTransition) {
/* 251 */             set = set.complement(IntervalSet.of(1, this.atn.maxTokenType));
/*     */           }
/* 253 */           look.addAll(set);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LL1Analyzer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */