/*     */ package freemarker.core;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Token
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public int kind;
/*     */   public int beginLine;
/*     */   public int beginColumn;
/*     */   public int endLine;
/*     */   public int endColumn;
/*     */   public String image;
/*     */   public Token next;
/*     */   public Token specialToken;
/*     */   
/*     */   public Object getValue() {
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token(int kind) {
/*  85 */     this(kind, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token(int kind, String image) {
/*  93 */     this.kind = kind;
/*  94 */     this.image = image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 102 */     return this.image;
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
/*     */   public static Token newToken(int ofKind, String image) {
/* 119 */     switch (ofKind) {
/*     */     
/* 121 */     }  return new Token(ofKind, image);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Token newToken(int ofKind) {
/* 127 */     return newToken(ofKind, null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Token.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */