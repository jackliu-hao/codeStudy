/*     */ package org.antlr.v4.runtime.dfa;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.antlr.v4.runtime.Vocabulary;
/*     */ import org.antlr.v4.runtime.VocabularyImpl;
/*     */ import org.antlr.v4.runtime.atn.ATNConfigSet;
/*     */ import org.antlr.v4.runtime.atn.DecisionState;
/*     */ import org.antlr.v4.runtime.atn.StarLoopEntryState;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DFA
/*     */ {
/*  53 */   public final Map<DFAState, DFAState> states = new HashMap<DFAState, DFAState>();
/*     */ 
/*     */   
/*     */   public volatile DFAState s0;
/*     */ 
/*     */   
/*     */   public final int decision;
/*     */ 
/*     */   
/*     */   public final DecisionState atnStartState;
/*     */ 
/*     */   
/*     */   private final boolean precedenceDfa;
/*     */ 
/*     */ 
/*     */   
/*     */   public DFA(DecisionState atnStartState) {
/*  70 */     this(atnStartState, 0);
/*     */   }
/*     */   
/*     */   public DFA(DecisionState atnStartState, int decision) {
/*  74 */     this.atnStartState = atnStartState;
/*  75 */     this.decision = decision;
/*     */     
/*  77 */     boolean precedenceDfa = false;
/*  78 */     if (atnStartState instanceof StarLoopEntryState && 
/*  79 */       ((StarLoopEntryState)atnStartState).isPrecedenceDecision) {
/*  80 */       precedenceDfa = true;
/*  81 */       DFAState precedenceState = new DFAState(new ATNConfigSet());
/*  82 */       precedenceState.edges = new DFAState[0];
/*  83 */       precedenceState.isAcceptState = false;
/*  84 */       precedenceState.requiresFullContext = false;
/*  85 */       this.s0 = precedenceState;
/*     */     } 
/*     */ 
/*     */     
/*  89 */     this.precedenceDfa = precedenceDfa;
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
/*     */   public final boolean isPrecedenceDfa() {
/* 104 */     return this.precedenceDfa;
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
/*     */   public final DFAState getPrecedenceStartState(int precedence) {
/* 119 */     if (!isPrecedenceDfa()) {
/* 120 */       throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
/*     */     }
/*     */ 
/*     */     
/* 124 */     if (precedence < 0 || precedence >= this.s0.edges.length) {
/* 125 */       return null;
/*     */     }
/*     */     
/* 128 */     return this.s0.edges[precedence];
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
/*     */   public final void setPrecedenceStartState(int precedence, DFAState startState) {
/* 143 */     if (!isPrecedenceDfa()) {
/* 144 */       throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
/*     */     }
/*     */     
/* 147 */     if (precedence < 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 153 */     synchronized (this.s0) {
/*     */       
/* 155 */       if (precedence >= this.s0.edges.length) {
/* 156 */         this.s0.edges = Arrays.<DFAState>copyOf(this.s0.edges, precedence + 1);
/*     */       }
/*     */       
/* 159 */       this.s0.edges[precedence] = startState;
/*     */     } 
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
/*     */   @Deprecated
/*     */   public final void setPrecedenceDfa(boolean precedenceDfa) {
/* 176 */     if (precedenceDfa != isPrecedenceDfa()) {
/* 177 */       throw new UnsupportedOperationException("The precedenceDfa field cannot change after a DFA is constructed.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<DFAState> getStates() {
/* 186 */     List<DFAState> result = new ArrayList<DFAState>(this.states.keySet());
/* 187 */     Collections.sort(result, new Comparator<DFAState>()
/*     */         {
/*     */           public int compare(DFAState o1, DFAState o2) {
/* 190 */             return o1.stateNumber - o2.stateNumber;
/*     */           }
/*     */         });
/*     */     
/* 194 */     return result;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 198 */     return toString(VocabularyImpl.EMPTY_VOCABULARY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String toString(String[] tokenNames) {
/* 205 */     if (this.s0 == null) return ""; 
/* 206 */     DFASerializer serializer = new DFASerializer(this, tokenNames);
/* 207 */     return serializer.toString();
/*     */   }
/*     */   
/*     */   public String toString(Vocabulary vocabulary) {
/* 211 */     if (this.s0 == null) {
/* 212 */       return "";
/*     */     }
/*     */     
/* 215 */     DFASerializer serializer = new DFASerializer(this, vocabulary);
/* 216 */     return serializer.toString();
/*     */   }
/*     */   
/*     */   public String toLexerString() {
/* 220 */     if (this.s0 == null) return ""; 
/* 221 */     DFASerializer serializer = new LexerDFASerializer(this);
/* 222 */     return serializer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\dfa\DFA.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */