/*     */ package org.antlr.v4.runtime;
/*     */ 
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
/*     */ public class RecognitionException
/*     */   extends RuntimeException
/*     */ {
/*     */   private final Recognizer<?, ?> recognizer;
/*     */   private final RuleContext ctx;
/*     */   private final IntStream input;
/*     */   private Token offendingToken;
/*  56 */   private int offendingState = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RecognitionException(Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
/*  62 */     this.recognizer = recognizer;
/*  63 */     this.input = input;
/*  64 */     this.ctx = ctx;
/*  65 */     if (recognizer != null) this.offendingState = recognizer.getState();
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RecognitionException(String message, Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
/*  73 */     super(message);
/*  74 */     this.recognizer = recognizer;
/*  75 */     this.input = input;
/*  76 */     this.ctx = ctx;
/*  77 */     if (recognizer != null) this.offendingState = recognizer.getState();
/*     */   
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
/*     */   public int getOffendingState() {
/*  90 */     return this.offendingState;
/*     */   }
/*     */   
/*     */   protected final void setOffendingState(int offendingState) {
/*  94 */     this.offendingState = offendingState;
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
/*     */   public IntervalSet getExpectedTokens() {
/* 108 */     if (this.recognizer != null) {
/* 109 */       return this.recognizer.getATN().getExpectedTokens(this.offendingState, this.ctx);
/*     */     }
/*     */     
/* 112 */     return null;
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
/*     */   public RuleContext getCtx() {
/* 124 */     return this.ctx;
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
/*     */   public IntStream getInputStream() {
/* 138 */     return this.input;
/*     */   }
/*     */ 
/*     */   
/*     */   public Token getOffendingToken() {
/* 143 */     return this.offendingToken;
/*     */   }
/*     */   
/*     */   protected final void setOffendingToken(Token offendingToken) {
/* 147 */     this.offendingToken = offendingToken;
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
/*     */   public Recognizer<?, ?> getRecognizer() {
/* 159 */     return this.recognizer;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\RecognitionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */