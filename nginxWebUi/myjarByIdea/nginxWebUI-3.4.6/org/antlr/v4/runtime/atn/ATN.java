package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.IntervalSet;

public class ATN {
   public static final int INVALID_ALT_NUMBER = 0;
   public final List<ATNState> states = new ArrayList();
   public final List<DecisionState> decisionToState = new ArrayList();
   public RuleStartState[] ruleToStartState;
   public RuleStopState[] ruleToStopState;
   public final Map<String, TokensStartState> modeNameToStartState = new LinkedHashMap();
   public final ATNType grammarType;
   public final int maxTokenType;
   public int[] ruleToTokenType;
   public LexerAction[] lexerActions;
   public final List<TokensStartState> modeToStartState = new ArrayList();

   public ATN(ATNType grammarType, int maxTokenType) {
      this.grammarType = grammarType;
      this.maxTokenType = maxTokenType;
   }

   public IntervalSet nextTokens(ATNState s, RuleContext ctx) {
      LL1Analyzer anal = new LL1Analyzer(this);
      IntervalSet next = anal.LOOK(s, ctx);
      return next;
   }

   public IntervalSet nextTokens(ATNState s) {
      if (s.nextTokenWithinRule != null) {
         return s.nextTokenWithinRule;
      } else {
         s.nextTokenWithinRule = this.nextTokens(s, (RuleContext)null);
         s.nextTokenWithinRule.setReadonly(true);
         return s.nextTokenWithinRule;
      }
   }

   public void addState(ATNState state) {
      if (state != null) {
         state.atn = this;
         state.stateNumber = this.states.size();
      }

      this.states.add(state);
   }

   public void removeState(ATNState state) {
      this.states.set(state.stateNumber, (Object)null);
   }

   public int defineDecisionState(DecisionState s) {
      this.decisionToState.add(s);
      s.decision = this.decisionToState.size() - 1;
      return s.decision;
   }

   public DecisionState getDecisionState(int decision) {
      return !this.decisionToState.isEmpty() ? (DecisionState)this.decisionToState.get(decision) : null;
   }

   public int getNumberOfDecisions() {
      return this.decisionToState.size();
   }

   public IntervalSet getExpectedTokens(int stateNumber, RuleContext context) {
      if (stateNumber >= 0 && stateNumber < this.states.size()) {
         RuleContext ctx = context;
         ATNState s = (ATNState)this.states.get(stateNumber);
         IntervalSet following = this.nextTokens(s);
         if (!following.contains(-2)) {
            return following;
         } else {
            IntervalSet expected = new IntervalSet(new int[0]);
            expected.addAll(following);
            expected.remove(-2);

            while(ctx != null && ctx.invokingState >= 0 && following.contains(-2)) {
               ATNState invokingState = (ATNState)this.states.get(ctx.invokingState);
               RuleTransition rt = (RuleTransition)invokingState.transition(0);
               following = this.nextTokens(rt.followState);
               expected.addAll(following);
               expected.remove(-2);
               ctx = ctx.parent;
            }

            if (following.contains(-2)) {
               expected.add(-1);
            }

            return expected;
         }
      } else {
         throw new IllegalArgumentException("Invalid state number.");
      }
   }
}
