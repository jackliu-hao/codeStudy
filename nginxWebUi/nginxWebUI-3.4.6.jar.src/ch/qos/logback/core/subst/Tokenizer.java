/*     */ package ch.qos.logback.core.subst;
/*     */ 
/*     */ import ch.qos.logback.core.spi.ScanException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Tokenizer
/*     */ {
/*     */   final String pattern;
/*     */   final int patternLength;
/*     */   TokenizerState state;
/*     */   int pointer;
/*     */   
/*     */   enum TokenizerState
/*     */   {
/*  25 */     LITERAL_STATE, START_STATE, DEFAULT_VAL_STATE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tokenizer(String pattern) {
/*  36 */     this.state = TokenizerState.LITERAL_STATE;
/*  37 */     this.pointer = 0;
/*     */     this.pattern = pattern;
/*     */     this.patternLength = pattern.length(); } List<Token> tokenize() throws ScanException {
/*  40 */     List<Token> tokenList = new ArrayList<Token>();
/*  41 */     StringBuilder buf = new StringBuilder();
/*     */     
/*  43 */     while (this.pointer < this.patternLength) {
/*  44 */       char c = this.pattern.charAt(this.pointer);
/*  45 */       this.pointer++;
/*     */       
/*  47 */       switch (this.state) {
/*     */         case LITERAL_STATE:
/*  49 */           handleLiteralState(c, tokenList, buf);
/*     */         
/*     */         case START_STATE:
/*  52 */           handleStartState(c, tokenList, buf);
/*     */         
/*     */         case DEFAULT_VAL_STATE:
/*  55 */           handleDefaultValueState(c, tokenList, buf);
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/*  60 */     switch (this.state) {
/*     */       case LITERAL_STATE:
/*  62 */         addLiteralToken(tokenList, buf);
/*     */         break;
/*     */       
/*     */       case DEFAULT_VAL_STATE:
/*  66 */         buf.append(':');
/*  67 */         addLiteralToken(tokenList, buf);
/*     */         break;
/*     */       
/*     */       case START_STATE:
/*  71 */         buf.append('$');
/*  72 */         addLiteralToken(tokenList, buf);
/*     */         break;
/*     */     } 
/*  75 */     return tokenList;
/*     */   }
/*     */   
/*     */   private void handleDefaultValueState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
/*  79 */     switch (c) {
/*     */       case '-':
/*  81 */         tokenList.add(Token.DEFAULT_SEP_TOKEN);
/*  82 */         this.state = TokenizerState.LITERAL_STATE;
/*     */         return;
/*     */       case '$':
/*  85 */         stringBuilder.append(':');
/*  86 */         addLiteralToken(tokenList, stringBuilder);
/*  87 */         stringBuilder.setLength(0);
/*  88 */         this.state = TokenizerState.START_STATE;
/*     */         return;
/*     */     } 
/*  91 */     stringBuilder.append(':').append(c);
/*  92 */     this.state = TokenizerState.LITERAL_STATE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void handleStartState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
/*  98 */     if (c == '{') {
/*  99 */       tokenList.add(Token.START_TOKEN);
/*     */     } else {
/* 101 */       stringBuilder.append('$').append(c);
/*     */     } 
/* 103 */     this.state = TokenizerState.LITERAL_STATE;
/*     */   }
/*     */   
/*     */   private void handleLiteralState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
/* 107 */     if (c == '$') {
/* 108 */       addLiteralToken(tokenList, stringBuilder);
/* 109 */       stringBuilder.setLength(0);
/* 110 */       this.state = TokenizerState.START_STATE;
/* 111 */     } else if (c == ':') {
/* 112 */       addLiteralToken(tokenList, stringBuilder);
/* 113 */       stringBuilder.setLength(0);
/* 114 */       this.state = TokenizerState.DEFAULT_VAL_STATE;
/* 115 */     } else if (c == '{') {
/* 116 */       addLiteralToken(tokenList, stringBuilder);
/* 117 */       tokenList.add(Token.CURLY_LEFT_TOKEN);
/* 118 */       stringBuilder.setLength(0);
/* 119 */     } else if (c == '}') {
/* 120 */       addLiteralToken(tokenList, stringBuilder);
/* 121 */       tokenList.add(Token.CURLY_RIGHT_TOKEN);
/* 122 */       stringBuilder.setLength(0);
/*     */     } else {
/* 124 */       stringBuilder.append(c);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addLiteralToken(List<Token> tokenList, StringBuilder stringBuilder) {
/* 130 */     if (stringBuilder.length() == 0)
/*     */       return; 
/* 132 */     tokenList.add(new Token(Token.Type.LITERAL, stringBuilder.toString()));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\subst\Tokenizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */