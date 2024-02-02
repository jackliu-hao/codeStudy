/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.List;
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
/*     */ public class ListTokenSource
/*     */   implements TokenSource
/*     */ {
/*     */   protected final List<? extends Token> tokens;
/*     */   private final String sourceName;
/*     */   protected int i;
/*     */   protected Token eofToken;
/*  45 */   private TokenFactory<?> _factory = CommonTokenFactory.DEFAULT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTokenSource(List<? extends Token> tokens) {
/*  56 */     this(tokens, null);
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
/*     */   public ListTokenSource(List<? extends Token> tokens, String sourceName) {
/*  73 */     if (tokens == null) {
/*  74 */       throw new NullPointerException("tokens cannot be null");
/*     */     }
/*     */     
/*  77 */     this.tokens = tokens;
/*  78 */     this.sourceName = sourceName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCharPositionInLine() {
/*  86 */     if (this.i < this.tokens.size()) {
/*  87 */       return ((Token)this.tokens.get(this.i)).getCharPositionInLine();
/*     */     }
/*  89 */     if (this.eofToken != null) {
/*  90 */       return this.eofToken.getCharPositionInLine();
/*     */     }
/*  92 */     if (this.tokens.size() > 0) {
/*     */ 
/*     */       
/*  95 */       Token lastToken = this.tokens.get(this.tokens.size() - 1);
/*  96 */       String tokenText = lastToken.getText();
/*  97 */       if (tokenText != null) {
/*  98 */         int lastNewLine = tokenText.lastIndexOf('\n');
/*  99 */         if (lastNewLine >= 0) {
/* 100 */           return tokenText.length() - lastNewLine - 1;
/*     */         }
/*     */       } 
/*     */       
/* 104 */       return lastToken.getCharPositionInLine() + lastToken.getStopIndex() - lastToken.getStartIndex() + 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 109 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token nextToken() {
/* 117 */     if (this.i >= this.tokens.size()) {
/* 118 */       if (this.eofToken == null) {
/* 119 */         int start = -1;
/* 120 */         if (this.tokens.size() > 0) {
/* 121 */           int previousStop = ((Token)this.tokens.get(this.tokens.size() - 1)).getStopIndex();
/* 122 */           if (previousStop != -1) {
/* 123 */             start = previousStop + 1;
/*     */           }
/*     */         } 
/*     */         
/* 127 */         int stop = Math.max(-1, start - 1);
/* 128 */         this.eofToken = (Token)this._factory.create(new Pair<TokenSource, CharStream>(this, getInputStream()), -1, "EOF", 0, start, stop, getLine(), getCharPositionInLine());
/*     */       } 
/*     */       
/* 131 */       return this.eofToken;
/*     */     } 
/*     */     
/* 134 */     Token t = this.tokens.get(this.i);
/* 135 */     if (this.i == this.tokens.size() - 1 && t.getType() == -1) {
/* 136 */       this.eofToken = t;
/*     */     }
/*     */     
/* 139 */     this.i++;
/* 140 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLine() {
/* 148 */     if (this.i < this.tokens.size()) {
/* 149 */       return ((Token)this.tokens.get(this.i)).getLine();
/*     */     }
/* 151 */     if (this.eofToken != null) {
/* 152 */       return this.eofToken.getLine();
/*     */     }
/* 154 */     if (this.tokens.size() > 0) {
/*     */ 
/*     */       
/* 157 */       Token lastToken = this.tokens.get(this.tokens.size() - 1);
/* 158 */       int line = lastToken.getLine();
/*     */       
/* 160 */       String tokenText = lastToken.getText();
/* 161 */       if (tokenText != null) {
/* 162 */         for (int i = 0; i < tokenText.length(); i++) {
/* 163 */           if (tokenText.charAt(i) == '\n') {
/* 164 */             line++;
/*     */           }
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 170 */       return line;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 175 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharStream getInputStream() {
/* 183 */     if (this.i < this.tokens.size()) {
/* 184 */       return ((Token)this.tokens.get(this.i)).getInputStream();
/*     */     }
/* 186 */     if (this.eofToken != null) {
/* 187 */       return this.eofToken.getInputStream();
/*     */     }
/* 189 */     if (this.tokens.size() > 0) {
/* 190 */       return ((Token)this.tokens.get(this.tokens.size() - 1)).getInputStream();
/*     */     }
/*     */ 
/*     */     
/* 194 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSourceName() {
/* 202 */     if (this.sourceName != null) {
/* 203 */       return this.sourceName;
/*     */     }
/*     */     
/* 206 */     CharStream inputStream = getInputStream();
/* 207 */     if (inputStream != null) {
/* 208 */       return inputStream.getSourceName();
/*     */     }
/*     */     
/* 211 */     return "List";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTokenFactory(TokenFactory<?> factory) {
/* 219 */     this._factory = factory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenFactory<?> getTokenFactory() {
/* 227 */     return this._factory;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\ListTokenSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */