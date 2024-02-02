/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import org.antlr.v4.runtime.misc.Interval;
/*     */ import org.antlr.v4.runtime.misc.Pair;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommonTokenFactory
/*     */   implements TokenFactory<CommonToken>
/*     */ {
/*  48 */   public static final TokenFactory<CommonToken> DEFAULT = new CommonTokenFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean copyText;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommonTokenFactory(boolean copyText) {
/*  77 */     this.copyText = copyText;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommonTokenFactory() {
/*  87 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommonToken create(Pair<TokenSource, CharStream> source, int type, String text, int channel, int start, int stop, int line, int charPositionInLine) {
/*  94 */     CommonToken t = new CommonToken(source, type, channel, start, stop);
/*  95 */     t.setLine(line);
/*  96 */     t.setCharPositionInLine(charPositionInLine);
/*  97 */     if (text != null) {
/*  98 */       t.setText(text);
/*     */     }
/* 100 */     else if (this.copyText && source.b != null) {
/* 101 */       t.setText(((CharStream)source.b).getText(Interval.of(start, stop)));
/*     */     } 
/*     */     
/* 104 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   public CommonToken create(int type, String text) {
/* 109 */     return new CommonToken(type, text);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\CommonTokenFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */