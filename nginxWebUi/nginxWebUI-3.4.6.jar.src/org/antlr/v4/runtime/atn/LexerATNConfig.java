/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import org.antlr.v4.runtime.misc.MurmurHash;
/*     */ import org.antlr.v4.runtime.misc.ObjectEqualityComparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LexerATNConfig
/*     */   extends ATNConfig
/*     */ {
/*     */   private final LexerActionExecutor lexerActionExecutor;
/*     */   private final boolean passedThroughNonGreedyDecision;
/*     */   
/*     */   public LexerATNConfig(ATNState state, int alt, PredictionContext context) {
/*  48 */     super(state, alt, context, SemanticContext.NONE);
/*  49 */     this.passedThroughNonGreedyDecision = false;
/*  50 */     this.lexerActionExecutor = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerATNConfig(ATNState state, int alt, PredictionContext context, LexerActionExecutor lexerActionExecutor) {
/*  58 */     super(state, alt, context, SemanticContext.NONE);
/*  59 */     this.lexerActionExecutor = lexerActionExecutor;
/*  60 */     this.passedThroughNonGreedyDecision = false;
/*     */   }
/*     */   
/*     */   public LexerATNConfig(LexerATNConfig c, ATNState state) {
/*  64 */     super(c, state, c.context, c.semanticContext);
/*  65 */     this.lexerActionExecutor = c.lexerActionExecutor;
/*  66 */     this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerATNConfig(LexerATNConfig c, ATNState state, LexerActionExecutor lexerActionExecutor) {
/*  72 */     super(c, state, c.context, c.semanticContext);
/*  73 */     this.lexerActionExecutor = lexerActionExecutor;
/*  74 */     this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
/*     */   }
/*     */ 
/*     */   
/*     */   public LexerATNConfig(LexerATNConfig c, ATNState state, PredictionContext context) {
/*  79 */     super(c, state, context, c.semanticContext);
/*  80 */     this.lexerActionExecutor = c.lexerActionExecutor;
/*  81 */     this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final LexerActionExecutor getLexerActionExecutor() {
/*  89 */     return this.lexerActionExecutor;
/*     */   }
/*     */   
/*     */   public final boolean hasPassedThroughNonGreedyDecision() {
/*  93 */     return this.passedThroughNonGreedyDecision;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  98 */     int hashCode = MurmurHash.initialize(7);
/*  99 */     hashCode = MurmurHash.update(hashCode, this.state.stateNumber);
/* 100 */     hashCode = MurmurHash.update(hashCode, this.alt);
/* 101 */     hashCode = MurmurHash.update(hashCode, this.context);
/* 102 */     hashCode = MurmurHash.update(hashCode, this.semanticContext);
/* 103 */     hashCode = MurmurHash.update(hashCode, this.passedThroughNonGreedyDecision ? 1 : 0);
/* 104 */     hashCode = MurmurHash.update(hashCode, this.lexerActionExecutor);
/* 105 */     hashCode = MurmurHash.finish(hashCode, 6);
/* 106 */     return hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(ATNConfig other) {
/* 111 */     if (this == other) {
/* 112 */       return true;
/*     */     }
/* 114 */     if (!(other instanceof LexerATNConfig)) {
/* 115 */       return false;
/*     */     }
/*     */     
/* 118 */     LexerATNConfig lexerOther = (LexerATNConfig)other;
/* 119 */     if (this.passedThroughNonGreedyDecision != lexerOther.passedThroughNonGreedyDecision) {
/* 120 */       return false;
/*     */     }
/*     */     
/* 123 */     if (!ObjectEqualityComparator.INSTANCE.equals(this.lexerActionExecutor, lexerOther.lexerActionExecutor)) {
/* 124 */       return false;
/*     */     }
/*     */     
/* 127 */     return super.equals(other);
/*     */   }
/*     */   
/*     */   private static boolean checkNonGreedyDecision(LexerATNConfig source, ATNState target) {
/* 131 */     return (source.passedThroughNonGreedyDecision || (target instanceof DecisionState && ((DecisionState)target).nonGreedy));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerATNConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */