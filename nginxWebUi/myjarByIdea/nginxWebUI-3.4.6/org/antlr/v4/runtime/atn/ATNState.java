package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.antlr.v4.runtime.misc.IntervalSet;

public abstract class ATNState {
   public static final int INITIAL_NUM_TRANSITIONS = 4;
   public static final int INVALID_TYPE = 0;
   public static final int BASIC = 1;
   public static final int RULE_START = 2;
   public static final int BLOCK_START = 3;
   public static final int PLUS_BLOCK_START = 4;
   public static final int STAR_BLOCK_START = 5;
   public static final int TOKEN_START = 6;
   public static final int RULE_STOP = 7;
   public static final int BLOCK_END = 8;
   public static final int STAR_LOOP_BACK = 9;
   public static final int STAR_LOOP_ENTRY = 10;
   public static final int PLUS_LOOP_BACK = 11;
   public static final int LOOP_END = 12;
   public static final List<String> serializationNames = Collections.unmodifiableList(Arrays.asList("INVALID", "BASIC", "RULE_START", "BLOCK_START", "PLUS_BLOCK_START", "STAR_BLOCK_START", "TOKEN_START", "RULE_STOP", "BLOCK_END", "STAR_LOOP_BACK", "STAR_LOOP_ENTRY", "PLUS_LOOP_BACK", "LOOP_END"));
   public static final int INVALID_STATE_NUMBER = -1;
   public ATN atn = null;
   public int stateNumber = -1;
   public int ruleIndex;
   public boolean epsilonOnlyTransitions = false;
   protected final List<Transition> transitions = new ArrayList(4);
   public IntervalSet nextTokenWithinRule;

   public int hashCode() {
      return this.stateNumber;
   }

   public boolean equals(Object o) {
      if (o instanceof ATNState) {
         return this.stateNumber == ((ATNState)o).stateNumber;
      } else {
         return false;
      }
   }

   public boolean isNonGreedyExitState() {
      return false;
   }

   public String toString() {
      return String.valueOf(this.stateNumber);
   }

   public Transition[] getTransitions() {
      return (Transition[])this.transitions.toArray(new Transition[this.transitions.size()]);
   }

   public int getNumberOfTransitions() {
      return this.transitions.size();
   }

   public void addTransition(Transition e) {
      this.addTransition(this.transitions.size(), e);
   }

   public void addTransition(int index, Transition e) {
      if (this.transitions.isEmpty()) {
         this.epsilonOnlyTransitions = e.isEpsilon();
      } else if (this.epsilonOnlyTransitions != e.isEpsilon()) {
         System.err.format(Locale.getDefault(), "ATN state %d has both epsilon and non-epsilon transitions.\n", this.stateNumber);
         this.epsilonOnlyTransitions = false;
      }

      this.transitions.add(index, e);
   }

   public Transition transition(int i) {
      return (Transition)this.transitions.get(i);
   }

   public void setTransition(int i, Transition e) {
      this.transitions.set(i, e);
   }

   public Transition removeTransition(int index) {
      return (Transition)this.transitions.remove(index);
   }

   public abstract int getStateType();

   public final boolean onlyHasEpsilonTransitions() {
      return this.epsilonOnlyTransitions;
   }

   public void setRuleIndex(int ruleIndex) {
      this.ruleIndex = ruleIndex;
   }
}
