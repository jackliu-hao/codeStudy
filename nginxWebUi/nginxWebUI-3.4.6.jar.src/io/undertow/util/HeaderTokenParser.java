/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HeaderTokenParser<E extends HeaderToken>
/*     */ {
/*     */   private static final char EQUALS = '=';
/*     */   private static final char COMMA = ',';
/*     */   private static final char QUOTE = '"';
/*     */   private static final char ESCAPE = '\\';
/*     */   private final Map<String, E> expectedTokens;
/*     */   
/*     */   public HeaderTokenParser(Map<String, E> expectedTokens) {
/*  40 */     this.expectedTokens = expectedTokens;
/*     */   }
/*     */   public Map<E, String> parseHeader(String header) {
/*     */     HeaderToken headerToken;
/*  44 */     char[] headerChars = header.toCharArray();
/*     */ 
/*     */     
/*  47 */     Map<E, String> response = new LinkedHashMap<>();
/*     */     
/*  49 */     SearchingFor searchingFor = SearchingFor.START_OF_NAME;
/*  50 */     int nameStart = 0;
/*  51 */     E currentToken = null;
/*  52 */     int valueStart = 0;
/*     */     
/*  54 */     int escapeCount = 0;
/*  55 */     boolean containsEscapes = false;
/*     */     
/*  57 */     for (int i = 0; i < headerChars.length; i++) {
/*  58 */       switch (searchingFor) {
/*     */         
/*     */         case START_OF_NAME:
/*  61 */           if (headerChars[i] != ',' && !Character.isWhitespace(headerChars[i])) {
/*  62 */             nameStart = i;
/*  63 */             searchingFor = SearchingFor.EQUALS_SIGN;
/*     */           } 
/*     */           break;
/*     */         case EQUALS_SIGN:
/*  67 */           if (headerChars[i] == '=') {
/*  68 */             String paramName = String.valueOf(headerChars, nameStart, i - nameStart);
/*  69 */             headerToken = (HeaderToken)this.expectedTokens.get(paramName);
/*  70 */             if (headerToken == null) {
/*  71 */               throw UndertowMessages.MESSAGES.unexpectedTokenInHeader(paramName);
/*     */             }
/*  73 */             searchingFor = SearchingFor.START_OF_VALUE;
/*     */           } 
/*     */           break;
/*     */         case START_OF_VALUE:
/*  77 */           if (!Character.isWhitespace(headerChars[i])) {
/*  78 */             if (headerChars[i] == '"' && headerToken.isAllowQuoted()) {
/*  79 */               valueStart = i + 1;
/*  80 */               searchingFor = SearchingFor.LAST_QUOTE; break;
/*     */             } 
/*  82 */             valueStart = i;
/*  83 */             searchingFor = SearchingFor.END_OF_VALUE;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case LAST_QUOTE:
/*  88 */           if (headerChars[i] == '\\') {
/*  89 */             escapeCount++;
/*  90 */             containsEscapes = true; break;
/*  91 */           }  if (headerChars[i] == '"' && escapeCount % 2 == 0) {
/*  92 */             String value = String.valueOf(headerChars, valueStart, i - valueStart);
/*  93 */             if (containsEscapes) {
/*  94 */               StringBuilder sb = new StringBuilder();
/*  95 */               boolean lastEscape = false;
/*  96 */               for (int j = 0; j < value.length(); j++) {
/*  97 */                 char c = value.charAt(j);
/*  98 */                 if (c == '\\' && !lastEscape) {
/*  99 */                   lastEscape = true;
/*     */                 } else {
/* 101 */                   lastEscape = false;
/* 102 */                   sb.append(c);
/*     */                 } 
/*     */               } 
/* 105 */               value = sb.toString();
/* 106 */               containsEscapes = false;
/*     */             } 
/* 108 */             response.put((E)headerToken, value);
/*     */             
/* 110 */             searchingFor = SearchingFor.START_OF_NAME;
/* 111 */             escapeCount = 0; break;
/*     */           } 
/* 113 */           escapeCount = 0;
/*     */           break;
/*     */         
/*     */         case END_OF_VALUE:
/* 117 */           if (headerChars[i] == ',' || Character.isWhitespace(headerChars[i])) {
/* 118 */             String value = String.valueOf(headerChars, valueStart, i - valueStart);
/* 119 */             response.put((E)headerToken, value);
/*     */             
/* 121 */             searchingFor = SearchingFor.START_OF_NAME;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 127 */     if (searchingFor == SearchingFor.END_OF_VALUE) {
/*     */       
/* 129 */       String value = String.valueOf(headerChars, valueStart, headerChars.length - valueStart);
/* 130 */       response.put((E)headerToken, value);
/* 131 */     } else if (searchingFor != SearchingFor.START_OF_NAME) {
/*     */       
/* 133 */       throw UndertowMessages.MESSAGES.invalidHeader();
/*     */     } 
/*     */     
/* 136 */     return response;
/*     */   }
/*     */   
/*     */   enum SearchingFor {
/* 140 */     START_OF_NAME, EQUALS_SIGN, START_OF_VALUE, LAST_QUOTE, END_OF_VALUE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\HeaderTokenParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */