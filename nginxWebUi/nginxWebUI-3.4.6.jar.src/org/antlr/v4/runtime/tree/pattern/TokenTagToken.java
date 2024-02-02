/*     */ package org.antlr.v4.runtime.tree.pattern;
/*     */ 
/*     */ import org.antlr.v4.runtime.CommonToken;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TokenTagToken
/*     */   extends CommonToken
/*     */ {
/*     */   private final String tokenName;
/*     */   private final String label;
/*     */   
/*     */   public TokenTagToken(String tokenName, int type) {
/*  61 */     this(tokenName, type, null);
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
/*     */   public TokenTagToken(String tokenName, int type, String label) {
/*  74 */     super(type);
/*  75 */     this.tokenName = tokenName;
/*  76 */     this.label = label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getTokenName() {
/*  85 */     return this.tokenName;
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
/*  96 */     return this.label;
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
/* 107 */     if (this.label != null) {
/* 108 */       return "<" + this.label + ":" + this.tokenName + ">";
/*     */     }
/*     */     
/* 111 */     return "<" + this.tokenName + ">";
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
/* 122 */     return this.tokenName + ":" + this.type;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\pattern\TokenTagToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */