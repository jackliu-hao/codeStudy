/*    */ package org.antlr.v4.runtime;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import org.antlr.v4.runtime.atn.ATNConfigSet;
/*    */ import org.antlr.v4.runtime.misc.Interval;
/*    */ import org.antlr.v4.runtime.misc.Utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LexerNoViableAltException
/*    */   extends RecognitionException
/*    */ {
/*    */   private final int startIndex;
/*    */   private final ATNConfigSet deadEndConfigs;
/*    */   
/*    */   public LexerNoViableAltException(Lexer lexer, CharStream input, int startIndex, ATNConfigSet deadEndConfigs) {
/* 50 */     super(lexer, input, null);
/* 51 */     this.startIndex = startIndex;
/* 52 */     this.deadEndConfigs = deadEndConfigs;
/*    */   }
/*    */   
/*    */   public int getStartIndex() {
/* 56 */     return this.startIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public ATNConfigSet getDeadEndConfigs() {
/* 61 */     return this.deadEndConfigs;
/*    */   }
/*    */ 
/*    */   
/*    */   public CharStream getInputStream() {
/* 66 */     return (CharStream)super.getInputStream();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 71 */     String symbol = "";
/* 72 */     if (this.startIndex >= 0 && this.startIndex < getInputStream().size()) {
/* 73 */       symbol = getInputStream().getText(Interval.of(this.startIndex, this.startIndex));
/* 74 */       symbol = Utils.escapeWhitespace(symbol, false);
/*    */     } 
/*    */     
/* 77 */     return String.format(Locale.getDefault(), "%s('%s')", new Object[] { LexerNoViableAltException.class.getSimpleName(), symbol });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\LexerNoViableAltException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */