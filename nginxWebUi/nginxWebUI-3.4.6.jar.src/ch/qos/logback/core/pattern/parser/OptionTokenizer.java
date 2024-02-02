/*     */ package ch.qos.logback.core.pattern.parser;
/*     */ 
/*     */ import ch.qos.logback.core.pattern.util.AsIsEscapeUtil;
/*     */ import ch.qos.logback.core.pattern.util.IEscapeUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OptionTokenizer
/*     */ {
/*     */   private static final int EXPECTING_STATE = 0;
/*     */   private static final int RAW_COLLECTING_STATE = 1;
/*     */   private static final int QUOTED_COLLECTING_STATE = 2;
/*     */   final IEscapeUtil escapeUtil;
/*     */   final TokenStream tokenStream;
/*     */   final String pattern;
/*     */   final int patternLength;
/*     */   char quoteChar;
/*  44 */   int state = 0;
/*     */   
/*     */   OptionTokenizer(TokenStream tokenStream) {
/*  47 */     this(tokenStream, (IEscapeUtil)new AsIsEscapeUtil());
/*     */   }
/*     */   
/*     */   OptionTokenizer(TokenStream tokenStream, IEscapeUtil escapeUtil) {
/*  51 */     this.tokenStream = tokenStream;
/*  52 */     this.pattern = tokenStream.pattern;
/*  53 */     this.patternLength = tokenStream.patternLength;
/*  54 */     this.escapeUtil = escapeUtil;
/*     */   }
/*     */   
/*     */   void tokenize(char firstChar, List<Token> tokenList) throws ScanException {
/*  58 */     StringBuffer buf = new StringBuffer();
/*  59 */     List<String> optionList = new ArrayList<String>();
/*  60 */     char c = firstChar;
/*     */     
/*  62 */     while (this.tokenStream.pointer < this.patternLength) {
/*  63 */       switch (this.state) {
/*     */         case 0:
/*  65 */           switch (c) {
/*     */             case '\t':
/*     */             case '\n':
/*     */             case '\r':
/*     */             case ' ':
/*     */             case ',':
/*     */               break;
/*     */             case '"':
/*     */             case '\'':
/*  74 */               this.state = 2;
/*  75 */               this.quoteChar = c;
/*     */               break;
/*     */             case '}':
/*  78 */               emitOptionToken(tokenList, optionList);
/*     */               return;
/*     */           } 
/*  81 */           buf.append(c);
/*  82 */           this.state = 1;
/*     */           break;
/*     */         
/*     */         case 1:
/*  86 */           switch (c) {
/*     */             case ',':
/*  88 */               optionList.add(buf.toString().trim());
/*  89 */               buf.setLength(0);
/*  90 */               this.state = 0;
/*     */               break;
/*     */             case '}':
/*  93 */               optionList.add(buf.toString().trim());
/*  94 */               emitOptionToken(tokenList, optionList);
/*     */               return;
/*     */           } 
/*  97 */           buf.append(c);
/*     */           break;
/*     */         
/*     */         case 2:
/* 101 */           if (c == this.quoteChar) {
/* 102 */             optionList.add(buf.toString());
/* 103 */             buf.setLength(0);
/* 104 */             this.state = 0; break;
/* 105 */           }  if (c == '\\') {
/* 106 */             escape(String.valueOf(this.quoteChar), buf); break;
/*     */           } 
/* 108 */           buf.append(c);
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 114 */       c = this.pattern.charAt(this.tokenStream.pointer);
/* 115 */       this.tokenStream.pointer++;
/*     */     } 
/*     */ 
/*     */     
/* 119 */     if (c == '}') {
/* 120 */       if (this.state == 0) {
/* 121 */         emitOptionToken(tokenList, optionList);
/* 122 */       } else if (this.state == 1) {
/* 123 */         optionList.add(buf.toString().trim());
/* 124 */         emitOptionToken(tokenList, optionList);
/*     */       } else {
/* 126 */         throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
/*     */       } 
/*     */     } else {
/* 129 */       throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
/*     */     } 
/*     */   }
/*     */   
/*     */   void emitOptionToken(List<Token> tokenList, List<String> optionList) {
/* 134 */     tokenList.add(new Token(1006, optionList));
/* 135 */     this.tokenStream.state = TokenStream.TokenizerState.LITERAL_STATE;
/*     */   }
/*     */   
/*     */   void escape(String escapeChars, StringBuffer buf) {
/* 139 */     if (this.tokenStream.pointer < this.patternLength) {
/* 140 */       char next = this.pattern.charAt(this.tokenStream.pointer++);
/* 141 */       this.escapeUtil.escape(escapeChars, buf, next, this.tokenStream.pointer);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\parser\OptionTokenizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */