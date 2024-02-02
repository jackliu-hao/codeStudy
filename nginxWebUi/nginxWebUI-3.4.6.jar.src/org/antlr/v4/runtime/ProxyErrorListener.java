/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.Collection;
/*     */ import org.antlr.v4.runtime.atn.ATNConfigSet;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProxyErrorListener
/*     */   implements ANTLRErrorListener
/*     */ {
/*     */   private final Collection<? extends ANTLRErrorListener> delegates;
/*     */   
/*     */   public ProxyErrorListener(Collection<? extends ANTLRErrorListener> delegates) {
/*  49 */     if (delegates == null) {
/*  50 */       throw new NullPointerException("delegates");
/*     */     }
/*     */     
/*  53 */     this.delegates = delegates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
/*  64 */     for (ANTLRErrorListener listener : this.delegates) {
/*  65 */       listener.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
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
/*     */   public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
/*  78 */     for (ANTLRErrorListener listener : this.delegates) {
/*  79 */       listener.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
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
/*     */   public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
/*  91 */     for (ANTLRErrorListener listener : this.delegates) {
/*  92 */       listener.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs);
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
/*     */   public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
/* 104 */     for (ANTLRErrorListener listener : this.delegates)
/* 105 */       listener.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\ProxyErrorListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */