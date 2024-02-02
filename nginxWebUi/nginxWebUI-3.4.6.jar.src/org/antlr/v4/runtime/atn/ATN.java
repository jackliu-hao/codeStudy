/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ public class ATN
/*     */ {
/*     */   public static final int INVALID_ALT_NUMBER = 0;
/*  48 */   public final List<ATNState> states = new ArrayList<ATNState>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   public final List<DecisionState> decisionToState = new ArrayList<DecisionState>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RuleStartState[] ruleToStartState;
/*     */ 
/*     */ 
/*     */   
/*     */   public RuleStopState[] ruleToStopState;
/*     */ 
/*     */ 
/*     */   
/*  67 */   public final Map<String, TokensStartState> modeNameToStartState = new LinkedHashMap<String, TokensStartState>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ATNType grammarType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int maxTokenType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] ruleToTokenType;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerAction[] lexerActions;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   public final List<TokensStartState> modeToStartState = new ArrayList<TokensStartState>();
/*     */ 
/*     */   
/*     */   public ATN(ATNType grammarType, int maxTokenType) {
/*  99 */     this.grammarType = grammarType;
/* 100 */     this.maxTokenType = maxTokenType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntervalSet nextTokens(ATNState s, RuleContext ctx) {
/* 109 */     LL1Analyzer anal = new LL1Analyzer(this);
/* 110 */     IntervalSet next = anal.LOOK(s, ctx);
/* 111 */     return next;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntervalSet nextTokens(ATNState s) {
/* 120 */     if (s.nextTokenWithinRule != null) return s.nextTokenWithinRule; 
/* 121 */     s.nextTokenWithinRule = nextTokens(s, null);
/* 122 */     s.nextTokenWithinRule.setReadonly(true);
/* 123 */     return s.nextTokenWithinRule;
/*     */   }
/*     */   
/*     */   public void addState(ATNState state) {
/* 127 */     if (state != null) {
/* 128 */       state.atn = this;
/* 129 */       state.stateNumber = this.states.size();
/*     */     } 
/*     */     
/* 132 */     this.states.add(state);
/*     */   }
/*     */   
/*     */   public void removeState(ATNState state) {
/* 136 */     this.states.set(state.stateNumber, null);
/*     */   }
/*     */   
/*     */   public int defineDecisionState(DecisionState s) {
/* 140 */     this.decisionToState.add(s);
/* 141 */     s.decision = this.decisionToState.size() - 1;
/* 142 */     return s.decision;
/*     */   }
/*     */   
/*     */   public DecisionState getDecisionState(int decision) {
/* 146 */     if (!this.decisionToState.isEmpty()) {
/* 147 */       return this.decisionToState.get(decision);
/*     */     }
/* 149 */     return null;
/*     */   }
/*     */   
/*     */   public int getNumberOfDecisions() {
/* 153 */     return this.decisionToState.size();
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
/*     */   public IntervalSet getExpectedTokens(int stateNumber, RuleContext context) {
/* 176 */     if (stateNumber < 0 || stateNumber >= this.states.size()) {
/* 177 */       throw new IllegalArgumentException("Invalid state number.");
/*     */     }
/*     */     
/* 180 */     RuleContext ctx = context;
/* 181 */     ATNState s = this.states.get(stateNumber);
/* 182 */     IntervalSet following = nextTokens(s);
/* 183 */     if (!following.contains(-2)) {
/* 184 */       return following;
/*     */     }
/*     */     
/* 187 */     IntervalSet expected = new IntervalSet(new int[0]);
/* 188 */     expected.addAll(following);
/* 189 */     expected.remove(-2);
/* 190 */     while (ctx != null && ctx.invokingState >= 0 && following.contains(-2)) {
/* 191 */       ATNState invokingState = this.states.get(ctx.invokingState);
/* 192 */       RuleTransition rt = (RuleTransition)invokingState.transition(0);
/* 193 */       following = nextTokens(rt.followState);
/* 194 */       expected.addAll(following);
/* 195 */       expected.remove(-2);
/* 196 */       ctx = ctx.parent;
/*     */     } 
/*     */     
/* 199 */     if (following.contains(-2)) {
/* 200 */       expected.add(-1);
/*     */     }
/*     */     
/* 203 */     return expected;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ATN.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */