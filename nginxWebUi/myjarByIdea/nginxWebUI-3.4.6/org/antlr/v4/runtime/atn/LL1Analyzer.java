package org.antlr.v4.runtime.atn;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.IntervalSet;

public class LL1Analyzer {
   public static final int HIT_PRED = 0;
   public final ATN atn;

   public LL1Analyzer(ATN atn) {
      this.atn = atn;
   }

   public IntervalSet[] getDecisionLookahead(ATNState s) {
      if (s == null) {
         return null;
      } else {
         IntervalSet[] look = new IntervalSet[s.getNumberOfTransitions()];

         for(int alt = 0; alt < s.getNumberOfTransitions(); ++alt) {
            look[alt] = new IntervalSet(new int[0]);
            Set<ATNConfig> lookBusy = new HashSet();
            boolean seeThruPreds = false;
            this._LOOK(s.transition(alt).target, (ATNState)null, PredictionContext.EMPTY, look[alt], lookBusy, new BitSet(), seeThruPreds, false);
            if (look[alt].size() == 0 || look[alt].contains(0)) {
               look[alt] = null;
            }
         }

         return look;
      }
   }

   public IntervalSet LOOK(ATNState s, RuleContext ctx) {
      return this.LOOK(s, (ATNState)null, ctx);
   }

   public IntervalSet LOOK(ATNState s, ATNState stopState, RuleContext ctx) {
      IntervalSet r = new IntervalSet(new int[0]);
      boolean seeThruPreds = true;
      PredictionContext lookContext = ctx != null ? PredictionContext.fromRuleContext(s.atn, ctx) : null;
      this._LOOK(s, stopState, lookContext, r, new HashSet(), new BitSet(), seeThruPreds, true);
      return r;
   }

   protected void _LOOK(ATNState s, ATNState stopState, PredictionContext ctx, IntervalSet look, Set<ATNConfig> lookBusy, BitSet calledRuleStack, boolean seeThruPreds, boolean addEOF) {
      ATNConfig c = new ATNConfig(s, 0, ctx);
      if (lookBusy.add(c)) {
         if (s == stopState) {
            if (ctx == null) {
               look.add(-2);
               return;
            }

            if (ctx.isEmpty() && addEOF) {
               look.add(-1);
               return;
            }
         }

         int i;
         if (s instanceof RuleStopState) {
            if (ctx == null) {
               look.add(-2);
               return;
            }

            if (ctx.isEmpty() && addEOF) {
               look.add(-1);
               return;
            }

            if (ctx != PredictionContext.EMPTY) {
               for(i = 0; i < ctx.size(); ++i) {
                  ATNState returnState = (ATNState)this.atn.states.get(ctx.getReturnState(i));
                  boolean removed = calledRuleStack.get(returnState.ruleIndex);

                  try {
                     calledRuleStack.clear(returnState.ruleIndex);
                     this._LOOK(returnState, stopState, ctx.getParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                  } finally {
                     if (removed) {
                        calledRuleStack.set(returnState.ruleIndex);
                     }

                  }
               }

               return;
            }
         }

         i = s.getNumberOfTransitions();

         for(int i = 0; i < i; ++i) {
            Transition t = s.transition(i);
            if (t.getClass() == RuleTransition.class) {
               if (!calledRuleStack.get(((RuleTransition)t).target.ruleIndex)) {
                  PredictionContext newContext = SingletonPredictionContext.create(ctx, ((RuleTransition)t).followState.stateNumber);

                  try {
                     calledRuleStack.set(((RuleTransition)t).target.ruleIndex);
                     this._LOOK(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                  } finally {
                     calledRuleStack.clear(((RuleTransition)t).target.ruleIndex);
                  }
               }
            } else if (t instanceof AbstractPredicateTransition) {
               if (seeThruPreds) {
                  this._LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
               } else {
                  look.add(0);
               }
            } else if (t.isEpsilon()) {
               this._LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
            } else if (t.getClass() == WildcardTransition.class) {
               look.addAll(IntervalSet.of(1, this.atn.maxTokenType));
            } else {
               IntervalSet set = t.label();
               if (set != null) {
                  if (t instanceof NotSetTransition) {
                     set = set.complement(IntervalSet.of(1, this.atn.maxTokenType));
                  }

                  look.addAll(set);
               }
            }
         }

      }
   }
}
