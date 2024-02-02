/*     */ package org.antlr.v4.runtime.tree.pattern;
/*     */ 
/*     */ import org.antlr.v4.runtime.CharStream;
/*     */ import org.antlr.v4.runtime.Token;
/*     */ import org.antlr.v4.runtime.TokenSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RuleTagToken
/*     */   implements Token
/*     */ {
/*     */   private final String ruleName;
/*     */   private final int bypassTokenType;
/*     */   private final String label;
/*     */   
/*     */   public RuleTagToken(String ruleName, int bypassTokenType) {
/*  68 */     this(ruleName, bypassTokenType, null);
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
/*     */   public RuleTagToken(String ruleName, int bypassTokenType, String label) {
/*  84 */     if (ruleName == null || ruleName.isEmpty()) {
/*  85 */       throw new IllegalArgumentException("ruleName cannot be null or empty.");
/*     */     }
/*     */     
/*  88 */     this.ruleName = ruleName;
/*  89 */     this.bypassTokenType = bypassTokenType;
/*  90 */     this.label = label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getRuleName() {
/* 100 */     return this.ruleName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getLabel() {
/* 111 */     return this.label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getChannel() {
/* 121 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/* 132 */     if (this.label != null) {
/* 133 */       return "<" + this.label + ":" + this.ruleName + ">";
/*     */     }
/*     */     
/* 136 */     return "<" + this.ruleName + ">";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 147 */     return this.bypassTokenType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLine() {
/* 157 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCharPositionInLine() {
/* 167 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTokenIndex() {
/* 177 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStartIndex() {
/* 187 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStopIndex() {
/* 197 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenSource getTokenSource() {
/* 207 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharStream getInputStream() {
/* 217 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 228 */     return this.ruleName + ":" + this.bypassTokenType;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\pattern\RuleTagToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */