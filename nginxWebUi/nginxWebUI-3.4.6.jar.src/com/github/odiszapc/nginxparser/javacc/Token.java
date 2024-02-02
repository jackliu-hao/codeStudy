/*     */ package com.github.odiszapc.nginxparser.javacc;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Token
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
/*  88 */     return null;
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
/* 101 */     this(kind, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token(int kind, String image) {
/* 109 */     this.kind = kind;
/* 110 */     this.image = image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 118 */     return this.image;
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
/* 135 */     switch (ofKind) {
/*     */     
/* 137 */     }  return new Token(ofKind, image);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Token newToken(int ofKind) {
/* 143 */     return newToken(ofKind, null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\javacc\Token.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */