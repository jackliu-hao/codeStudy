/*     */ package ch.qos.logback.core.pattern.parser;
/*     */ 
/*     */ import ch.qos.logback.core.pattern.util.IEscapeUtil;
/*     */ import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
/*     */ import ch.qos.logback.core.pattern.util.RestrictedEscapeUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class TokenStream
/*     */ {
/*     */   final String pattern;
/*     */   final int patternLength;
/*     */   final IEscapeUtil escapeUtil;
/*     */   
/*     */   enum TokenizerState
/*     */   {
/*  50 */     LITERAL_STATE, FORMAT_MODIFIER_STATE, KEYWORD_STATE, OPTION_STATE, RIGHT_PARENTHESIS_STATE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   final IEscapeUtil optionEscapeUtil = (IEscapeUtil)new RestrictedEscapeUtil();
/*     */   
/*  59 */   TokenizerState state = TokenizerState.LITERAL_STATE;
/*  60 */   int pointer = 0;
/*     */ 
/*     */   
/*     */   TokenStream(String pattern) {
/*  64 */     this(pattern, (IEscapeUtil)new RegularEscapeUtil());
/*     */   }
/*     */   
/*     */   TokenStream(String pattern, IEscapeUtil escapeUtil) {
/*  68 */     if (pattern == null || pattern.length() == 0) {
/*  69 */       throw new IllegalArgumentException("null or empty pattern string not allowed");
/*     */     }
/*  71 */     this.pattern = pattern;
/*  72 */     this.patternLength = pattern.length();
/*  73 */     this.escapeUtil = escapeUtil;
/*     */   }
/*     */   
/*     */   List tokenize() throws ScanException {
/*  77 */     List<Token> tokenList = new ArrayList<Token>();
/*  78 */     StringBuffer buf = new StringBuffer();
/*     */     
/*  80 */     while (this.pointer < this.patternLength) {
/*  81 */       char c = this.pattern.charAt(this.pointer);
/*  82 */       this.pointer++;
/*     */       
/*  84 */       switch (this.state) {
/*     */         case LITERAL_STATE:
/*  86 */           handleLiteralState(c, tokenList, buf);
/*     */         
/*     */         case FORMAT_MODIFIER_STATE:
/*  89 */           handleFormatModifierState(c, tokenList, buf);
/*     */         
/*     */         case OPTION_STATE:
/*  92 */           processOption(c, tokenList, buf);
/*     */         
/*     */         case KEYWORD_STATE:
/*  95 */           handleKeywordState(c, tokenList, buf);
/*     */         
/*     */         case RIGHT_PARENTHESIS_STATE:
/*  98 */           handleRightParenthesisState(c, tokenList, buf);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 106 */     switch (this.state) {
/*     */       case LITERAL_STATE:
/* 108 */         addValuedToken(1000, buf, tokenList);
/*     */         break;
/*     */       case KEYWORD_STATE:
/* 111 */         tokenList.add(new Token(1004, buf.toString()));
/*     */         break;
/*     */       case RIGHT_PARENTHESIS_STATE:
/* 114 */         tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
/*     */         break;
/*     */       
/*     */       case FORMAT_MODIFIER_STATE:
/*     */       case OPTION_STATE:
/* 119 */         throw new ScanException("Unexpected end of pattern string");
/*     */     } 
/*     */     
/* 122 */     return tokenList;
/*     */   }
/*     */   
/*     */   private void handleRightParenthesisState(char c, List<Token> tokenList, StringBuffer buf) {
/* 126 */     tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
/* 127 */     switch (c) {
/*     */       case ')':
/*     */         return;
/*     */       case '{':
/* 131 */         this.state = TokenizerState.OPTION_STATE;
/*     */       
/*     */       case '\\':
/* 134 */         escape("%{}", buf);
/* 135 */         this.state = TokenizerState.LITERAL_STATE;
/*     */     } 
/*     */     
/* 138 */     buf.append(c);
/* 139 */     this.state = TokenizerState.LITERAL_STATE;
/*     */   }
/*     */ 
/*     */   
/*     */   private void processOption(char c, List<Token> tokenList, StringBuffer buf) throws ScanException {
/* 144 */     OptionTokenizer ot = new OptionTokenizer(this);
/* 145 */     ot.tokenize(c, tokenList);
/*     */   }
/*     */   
/*     */   private void handleFormatModifierState(char c, List<Token> tokenList, StringBuffer buf) {
/* 149 */     if (c == '(') {
/* 150 */       addValuedToken(1002, buf, tokenList);
/* 151 */       tokenList.add(Token.BARE_COMPOSITE_KEYWORD_TOKEN);
/* 152 */       this.state = TokenizerState.LITERAL_STATE;
/* 153 */     } else if (Character.isJavaIdentifierStart(c)) {
/* 154 */       addValuedToken(1002, buf, tokenList);
/* 155 */       this.state = TokenizerState.KEYWORD_STATE;
/* 156 */       buf.append(c);
/*     */     } else {
/* 158 */       buf.append(c);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleLiteralState(char c, List<Token> tokenList, StringBuffer buf) {
/* 163 */     switch (c) {
/*     */       case '\\':
/* 165 */         escape("%()", buf);
/*     */         return;
/*     */       
/*     */       case '%':
/* 169 */         addValuedToken(1000, buf, tokenList);
/* 170 */         tokenList.add(Token.PERCENT_TOKEN);
/* 171 */         this.state = TokenizerState.FORMAT_MODIFIER_STATE;
/*     */         return;
/*     */       
/*     */       case ')':
/* 175 */         addValuedToken(1000, buf, tokenList);
/* 176 */         this.state = TokenizerState.RIGHT_PARENTHESIS_STATE;
/*     */         return;
/*     */     } 
/*     */     
/* 180 */     buf.append(c);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void handleKeywordState(char c, List<Token> tokenList, StringBuffer buf) {
/* 186 */     if (Character.isJavaIdentifierPart(c)) {
/* 187 */       buf.append(c);
/* 188 */     } else if (c == '{') {
/* 189 */       addValuedToken(1004, buf, tokenList);
/* 190 */       this.state = TokenizerState.OPTION_STATE;
/* 191 */     } else if (c == '(') {
/* 192 */       addValuedToken(1005, buf, tokenList);
/* 193 */       this.state = TokenizerState.LITERAL_STATE;
/* 194 */     } else if (c == '%') {
/* 195 */       addValuedToken(1004, buf, tokenList);
/* 196 */       tokenList.add(Token.PERCENT_TOKEN);
/* 197 */       this.state = TokenizerState.FORMAT_MODIFIER_STATE;
/* 198 */     } else if (c == ')') {
/* 199 */       addValuedToken(1004, buf, tokenList);
/* 200 */       this.state = TokenizerState.RIGHT_PARENTHESIS_STATE;
/*     */     } else {
/* 202 */       addValuedToken(1004, buf, tokenList);
/* 203 */       if (c == '\\') {
/* 204 */         if (this.pointer < this.patternLength) {
/* 205 */           char next = this.pattern.charAt(this.pointer++);
/* 206 */           this.escapeUtil.escape("%()", buf, next, this.pointer);
/*     */         } 
/*     */       } else {
/* 209 */         buf.append(c);
/*     */       } 
/* 211 */       this.state = TokenizerState.LITERAL_STATE;
/*     */     } 
/*     */   }
/*     */   
/*     */   void escape(String escapeChars, StringBuffer buf) {
/* 216 */     if (this.pointer < this.patternLength) {
/* 217 */       char next = this.pattern.charAt(this.pointer++);
/* 218 */       this.escapeUtil.escape(escapeChars, buf, next, this.pointer);
/*     */     } 
/*     */   }
/*     */   
/*     */   void optionEscape(String escapeChars, StringBuffer buf) {
/* 223 */     if (this.pointer < this.patternLength) {
/* 224 */       char next = this.pattern.charAt(this.pointer++);
/* 225 */       this.optionEscapeUtil.escape(escapeChars, buf, next, this.pointer);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addValuedToken(int type, StringBuffer buf, List<Token> tokenList) {
/* 230 */     if (buf.length() > 0) {
/* 231 */       tokenList.add(new Token(type, buf.toString()));
/* 232 */       buf.setLength(0);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\parser\TokenStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */