/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import org.antlr.v4.runtime.Recognizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ATNConfig
/*     */ {
/*     */   private static final int SUPPRESS_PRECEDENCE_FILTER = 1073741824;
/*     */   public final ATNState state;
/*     */   public final int alt;
/*     */   public PredictionContext context;
/*     */   public int reachesIntoOuterContext;
/*     */   public final SemanticContext semanticContext;
/*     */   
/*     */   public ATNConfig(ATNConfig old) {
/*  92 */     this.state = old.state;
/*  93 */     this.alt = old.alt;
/*  94 */     this.context = old.context;
/*  95 */     this.semanticContext = old.semanticContext;
/*  96 */     this.reachesIntoOuterContext = old.reachesIntoOuterContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNConfig(ATNState state, int alt, PredictionContext context) {
/* 103 */     this(state, alt, context, SemanticContext.NONE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNConfig(ATNState state, int alt, PredictionContext context, SemanticContext semanticContext) {
/* 111 */     this.state = state;
/* 112 */     this.alt = alt;
/* 113 */     this.context = context;
/* 114 */     this.semanticContext = semanticContext;
/*     */   }
/*     */   
/*     */   public ATNConfig(ATNConfig c, ATNState state) {
/* 118 */     this(c, state, c.context, c.semanticContext);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNConfig(ATNConfig c, ATNState state, SemanticContext semanticContext) {
/* 124 */     this(c, state, c.context, semanticContext);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNConfig(ATNConfig c, SemanticContext semanticContext) {
/* 130 */     this(c, c.state, c.context, semanticContext);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNConfig(ATNConfig c, ATNState state, PredictionContext context) {
/* 136 */     this(c, state, context, c.semanticContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNConfig(ATNConfig c, ATNState state, PredictionContext context, SemanticContext semanticContext) {
/* 143 */     this.state = state;
/* 144 */     this.alt = c.alt;
/* 145 */     this.context = context;
/* 146 */     this.semanticContext = semanticContext;
/* 147 */     this.reachesIntoOuterContext = c.reachesIntoOuterContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getOuterContextDepth() {
/* 156 */     return this.reachesIntoOuterContext & 0xBFFFFFFF;
/*     */   }
/*     */   
/*     */   public final boolean isPrecedenceFilterSuppressed() {
/* 160 */     return ((this.reachesIntoOuterContext & 0x40000000) != 0);
/*     */   }
/*     */   
/*     */   public final void setPrecedenceFilterSuppressed(boolean value) {
/* 164 */     if (value) {
/* 165 */       this.reachesIntoOuterContext |= 0x40000000;
/*     */     } else {
/*     */       
/* 168 */       this.reachesIntoOuterContext &= 0xBFFFFFFF;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 178 */     if (!(o instanceof ATNConfig)) {
/* 179 */       return false;
/*     */     }
/*     */     
/* 182 */     return equals((ATNConfig)o);
/*     */   }
/*     */   
/*     */   public boolean equals(ATNConfig other) {
/* 186 */     if (this == other)
/* 187 */       return true; 
/* 188 */     if (other == null) {
/* 189 */       return false;
/*     */     }
/*     */     
/* 192 */     return (this.state.stateNumber == other.state.stateNumber && this.alt == other.alt && (this.context == other.context || (this.context != null && this.context.equals(other.context))) && this.semanticContext.equals(other.semanticContext) && isPrecedenceFilterSuppressed() == other.isPrecedenceFilterSuppressed());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 201 */     int hashCode = MurmurHash.initialize(7);
/* 202 */     hashCode = MurmurHash.update(hashCode, this.state.stateNumber);
/* 203 */     hashCode = MurmurHash.update(hashCode, this.alt);
/* 204 */     hashCode = MurmurHash.update(hashCode, this.context);
/* 205 */     hashCode = MurmurHash.update(hashCode, this.semanticContext);
/* 206 */     hashCode = MurmurHash.finish(hashCode, 4);
/* 207 */     return hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 212 */     return toString(null, true);
/*     */   }
/*     */   
/*     */   public String toString(Recognizer<?, ?> recog, boolean showAlt) {
/* 216 */     StringBuilder buf = new StringBuilder();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 221 */     buf.append('(');
/* 222 */     buf.append(this.state);
/* 223 */     if (showAlt) {
/* 224 */       buf.append(",");
/* 225 */       buf.append(this.alt);
/*     */     } 
/* 227 */     if (this.context != null) {
/* 228 */       buf.append(",[");
/* 229 */       buf.append(this.context.toString());
/* 230 */       buf.append("]");
/*     */     } 
/* 232 */     if (this.semanticContext != null && this.semanticContext != SemanticContext.NONE) {
/* 233 */       buf.append(",");
/* 234 */       buf.append(this.semanticContext);
/*     */     } 
/* 236 */     if (getOuterContextDepth() > 0) {
/* 237 */       buf.append(",up=").append(getOuterContextDepth());
/*     */     }
/* 239 */     buf.append(')');
/* 240 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ATNConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */