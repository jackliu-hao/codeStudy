/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
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
/*     */ public abstract class ATNState
/*     */ {
/*     */   public static final int INITIAL_NUM_TRANSITIONS = 4;
/*     */   public static final int INVALID_TYPE = 0;
/*     */   public static final int BASIC = 1;
/*     */   public static final int RULE_START = 2;
/*     */   public static final int BLOCK_START = 3;
/*     */   public static final int PLUS_BLOCK_START = 4;
/*     */   public static final int STAR_BLOCK_START = 5;
/*     */   public static final int TOKEN_START = 6;
/*     */   public static final int RULE_STOP = 7;
/*     */   public static final int BLOCK_END = 8;
/*     */   public static final int STAR_LOOP_BACK = 9;
/*     */   public static final int STAR_LOOP_ENTRY = 10;
/*     */   public static final int PLUS_LOOP_BACK = 11;
/*     */   public static final int LOOP_END = 12;
/* 119 */   public static final List<String> serializationNames = Collections.unmodifiableList(Arrays.asList(new String[] { "INVALID", "BASIC", "RULE_START", "BLOCK_START", "PLUS_BLOCK_START", "STAR_BLOCK_START", "TOKEN_START", "RULE_STOP", "BLOCK_END", "STAR_LOOP_BACK", "STAR_LOOP_ENTRY", "PLUS_LOOP_BACK", "LOOP_END" }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int INVALID_STATE_NUMBER = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 139 */   public ATN atn = null;
/*     */   
/* 141 */   public int stateNumber = -1;
/*     */ 
/*     */   
/*     */   public int ruleIndex;
/*     */   
/*     */   public boolean epsilonOnlyTransitions = false;
/*     */   
/* 148 */   protected final List<Transition> transitions = new ArrayList<Transition>(4);
/*     */ 
/*     */   
/*     */   public IntervalSet nextTokenWithinRule;
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 155 */     return this.stateNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 160 */     if (o instanceof ATNState) return (this.stateNumber == ((ATNState)o).stateNumber); 
/* 161 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isNonGreedyExitState() {
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 170 */     return String.valueOf(this.stateNumber);
/*     */   }
/*     */   
/*     */   public Transition[] getTransitions() {
/* 174 */     return this.transitions.<Transition>toArray(new Transition[this.transitions.size()]);
/*     */   }
/*     */   
/*     */   public int getNumberOfTransitions() {
/* 178 */     return this.transitions.size();
/*     */   }
/*     */   
/*     */   public void addTransition(Transition e) {
/* 182 */     addTransition(this.transitions.size(), e);
/*     */   }
/*     */   
/*     */   public void addTransition(int index, Transition e) {
/* 186 */     if (this.transitions.isEmpty()) {
/* 187 */       this.epsilonOnlyTransitions = e.isEpsilon();
/*     */     }
/* 189 */     else if (this.epsilonOnlyTransitions != e.isEpsilon()) {
/* 190 */       System.err.format(Locale.getDefault(), "ATN state %d has both epsilon and non-epsilon transitions.\n", new Object[] { Integer.valueOf(this.stateNumber) });
/* 191 */       this.epsilonOnlyTransitions = false;
/*     */     } 
/*     */     
/* 194 */     this.transitions.add(index, e);
/*     */   }
/*     */   public Transition transition(int i) {
/* 197 */     return this.transitions.get(i);
/*     */   }
/*     */   public void setTransition(int i, Transition e) {
/* 200 */     this.transitions.set(i, e);
/*     */   }
/*     */   
/*     */   public Transition removeTransition(int index) {
/* 204 */     return this.transitions.remove(index);
/*     */   }
/*     */   
/*     */   public abstract int getStateType();
/*     */   
/*     */   public final boolean onlyHasEpsilonTransitions() {
/* 210 */     return this.epsilonOnlyTransitions;
/*     */   }
/*     */   public void setRuleIndex(int ruleIndex) {
/* 213 */     this.ruleIndex = ruleIndex;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ATNState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */