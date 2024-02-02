/*     */ package org.antlr.v4.runtime.dfa;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.antlr.v4.runtime.atn.ATNConfig;
/*     */ import org.antlr.v4.runtime.atn.ATNConfigSet;
/*     */ import org.antlr.v4.runtime.atn.LexerActionExecutor;
/*     */ import org.antlr.v4.runtime.atn.SemanticContext;
/*     */ import org.antlr.v4.runtime.misc.MurmurHash;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DFAState
/*     */ {
/*  71 */   public int stateNumber = -1;
/*     */ 
/*     */   
/*  74 */   public ATNConfigSet configs = new ATNConfigSet();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DFAState[] edges;
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAcceptState = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public int prediction;
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerActionExecutor lexerActionExecutor;
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean requiresFullContext;
/*     */ 
/*     */ 
/*     */   
/*     */   public PredPrediction[] predicates;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DFAState() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class PredPrediction
/*     */   {
/*     */     public SemanticContext pred;
/*     */ 
/*     */ 
/*     */     
/*     */     public int alt;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PredPrediction(SemanticContext pred, int alt) {
/* 121 */       this.alt = alt;
/* 122 */       this.pred = pred;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 126 */       return "(" + this.pred + ", " + this.alt + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public DFAState(int stateNumber) {
/* 132 */     this.stateNumber = stateNumber;
/*     */   } public DFAState(ATNConfigSet configs) {
/* 134 */     this.configs = configs;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Integer> getAltSet() {
/* 140 */     Set<Integer> alts = new HashSet<Integer>();
/* 141 */     if (this.configs != null) {
/* 142 */       for (ATNConfig c : this.configs) {
/* 143 */         alts.add(Integer.valueOf(c.alt));
/*     */       }
/*     */     }
/* 146 */     if (alts.isEmpty()) return null; 
/* 147 */     return alts;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 152 */     int hash = MurmurHash.initialize(7);
/* 153 */     hash = MurmurHash.update(hash, this.configs.hashCode());
/* 154 */     hash = MurmurHash.finish(hash, 1);
/* 155 */     return hash;
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
/*     */   public boolean equals(Object o) {
/* 174 */     if (this == o) return true;
/*     */     
/* 176 */     if (!(o instanceof DFAState)) {
/* 177 */       return false;
/*     */     }
/*     */     
/* 180 */     DFAState other = (DFAState)o;
/*     */     
/* 182 */     boolean sameSet = this.configs.equals(other.configs);
/*     */     
/* 184 */     return sameSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 189 */     StringBuilder buf = new StringBuilder();
/* 190 */     buf.append(this.stateNumber).append(":").append(this.configs);
/* 191 */     if (this.isAcceptState) {
/* 192 */       buf.append("=>");
/* 193 */       if (this.predicates != null) {
/* 194 */         buf.append(Arrays.toString((Object[])this.predicates));
/*     */       } else {
/*     */         
/* 197 */         buf.append(this.prediction);
/*     */       } 
/*     */     } 
/* 200 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\dfa\DFAState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */