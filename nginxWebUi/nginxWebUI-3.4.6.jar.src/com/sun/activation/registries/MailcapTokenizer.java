/*     */ package com.sun.activation.registries;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MailcapTokenizer
/*     */ {
/*     */   public static final int UNKNOWN_TOKEN = 0;
/*     */   public static final int START_TOKEN = 1;
/*     */   public static final int STRING_TOKEN = 2;
/*     */   public static final int EOI_TOKEN = 5;
/*     */   public static final int SLASH_TOKEN = 47;
/*     */   public static final int SEMICOLON_TOKEN = 59;
/*     */   public static final int EQUALS_TOKEN = 61;
/*     */   private String data;
/*     */   private int dataIndex;
/*     */   private int dataLength;
/*     */   private int currentToken;
/*     */   private String currentTokenValue;
/*     */   private boolean isAutoquoting;
/*     */   private char autoquoteChar;
/*     */   
/*     */   public MailcapTokenizer(String inputString) {
/*  50 */     this.data = inputString;
/*  51 */     this.dataIndex = 0;
/*  52 */     this.dataLength = inputString.length();
/*     */     
/*  54 */     this.currentToken = 1;
/*  55 */     this.currentTokenValue = "";
/*     */     
/*  57 */     this.isAutoquoting = false;
/*  58 */     this.autoquoteChar = ';';
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
/*     */   public void setIsAutoquoting(boolean value) {
/*  72 */     this.isAutoquoting = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAutoquoteChar(char value) {
/*  79 */     this.autoquoteChar = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCurrentToken() {
/*  88 */     return this.currentToken;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String nameForToken(int token) {
/*  95 */     String name = "really unknown";
/*     */     
/*  97 */     switch (token) {
/*     */       case 0:
/*  99 */         name = "unknown";
/*     */         break;
/*     */       case 1:
/* 102 */         name = "start";
/*     */         break;
/*     */       case 2:
/* 105 */         name = "string";
/*     */         break;
/*     */       case 5:
/* 108 */         name = "EOI";
/*     */         break;
/*     */       case 47:
/* 111 */         name = "'/'";
/*     */         break;
/*     */       case 59:
/* 114 */         name = "';'";
/*     */         break;
/*     */       case 61:
/* 117 */         name = "'='";
/*     */         break;
/*     */     } 
/*     */     
/* 121 */     return name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCurrentTokenValue() {
/* 130 */     return this.currentTokenValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nextToken() {
/* 139 */     if (this.dataIndex < this.dataLength) {
/*     */       
/* 141 */       while (this.dataIndex < this.dataLength && isWhiteSpaceChar(this.data.charAt(this.dataIndex)))
/*     */       {
/* 143 */         this.dataIndex++;
/*     */       }
/*     */       
/* 146 */       if (this.dataIndex < this.dataLength) {
/*     */         
/* 148 */         char c = this.data.charAt(this.dataIndex);
/* 149 */         if (this.isAutoquoting) {
/* 150 */           if (!isAutoquoteSpecialChar(c)) {
/* 151 */             processAutoquoteToken();
/* 152 */           } else if (c == ';' || c == '=') {
/* 153 */             this.currentToken = c;
/* 154 */             this.currentTokenValue = (new Character(c)).toString();
/* 155 */             this.dataIndex++;
/*     */           } else {
/* 157 */             this.currentToken = 0;
/* 158 */             this.currentTokenValue = (new Character(c)).toString();
/* 159 */             this.dataIndex++;
/*     */           }
/*     */         
/* 162 */         } else if (isStringTokenChar(c)) {
/* 163 */           processStringToken();
/* 164 */         } else if (c == '/' || c == ';' || c == '=') {
/* 165 */           this.currentToken = c;
/* 166 */           this.currentTokenValue = (new Character(c)).toString();
/* 167 */           this.dataIndex++;
/*     */         } else {
/* 169 */           this.currentToken = 0;
/* 170 */           this.currentTokenValue = (new Character(c)).toString();
/* 171 */           this.dataIndex++;
/*     */         } 
/*     */       } else {
/*     */         
/* 175 */         this.currentToken = 5;
/* 176 */         this.currentTokenValue = null;
/*     */       } 
/*     */     } else {
/* 179 */       this.currentToken = 5;
/* 180 */       this.currentTokenValue = null;
/*     */     } 
/*     */     
/* 183 */     return this.currentToken;
/*     */   }
/*     */ 
/*     */   
/*     */   private void processStringToken() {
/* 188 */     int initialIndex = this.dataIndex;
/*     */ 
/*     */     
/* 191 */     while (this.dataIndex < this.dataLength && isStringTokenChar(this.data.charAt(this.dataIndex)))
/*     */     {
/* 193 */       this.dataIndex++;
/*     */     }
/*     */     
/* 196 */     this.currentToken = 2;
/* 197 */     this.currentTokenValue = this.data.substring(initialIndex, this.dataIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   private void processAutoquoteToken() {
/* 202 */     int initialIndex = this.dataIndex;
/*     */ 
/*     */     
/* 205 */     boolean foundTerminator = false;
/* 206 */     while (this.dataIndex < this.dataLength && !foundTerminator) {
/* 207 */       char c = this.data.charAt(this.dataIndex);
/* 208 */       if (c != this.autoquoteChar) {
/* 209 */         this.dataIndex++; continue;
/*     */       } 
/* 211 */       foundTerminator = true;
/*     */     } 
/*     */ 
/*     */     
/* 215 */     this.currentToken = 2;
/* 216 */     this.currentTokenValue = fixEscapeSequences(this.data.substring(initialIndex, this.dataIndex));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isSpecialChar(char c) {
/* 221 */     boolean lAnswer = false;
/*     */     
/* 223 */     switch (c) {
/*     */       case '"':
/*     */       case '(':
/*     */       case ')':
/*     */       case ',':
/*     */       case '/':
/*     */       case ':':
/*     */       case ';':
/*     */       case '<':
/*     */       case '=':
/*     */       case '>':
/*     */       case '?':
/*     */       case '@':
/*     */       case '[':
/*     */       case '\\':
/*     */       case ']':
/* 239 */         lAnswer = true;
/*     */         break;
/*     */     } 
/*     */     
/* 243 */     return lAnswer;
/*     */   }
/*     */   
/*     */   public static boolean isAutoquoteSpecialChar(char c) {
/* 247 */     boolean lAnswer = false;
/*     */     
/* 249 */     switch (c) {
/*     */       case ';':
/*     */       case '=':
/* 252 */         lAnswer = true;
/*     */         break;
/*     */     } 
/*     */     
/* 256 */     return lAnswer;
/*     */   }
/*     */   
/*     */   public static boolean isControlChar(char c) {
/* 260 */     return Character.isISOControl(c);
/*     */   }
/*     */   
/*     */   public static boolean isWhiteSpaceChar(char c) {
/* 264 */     return Character.isWhitespace(c);
/*     */   }
/*     */   
/*     */   public static boolean isStringTokenChar(char c) {
/* 268 */     return (!isSpecialChar(c) && !isControlChar(c) && !isWhiteSpaceChar(c));
/*     */   }
/*     */   
/*     */   private static String fixEscapeSequences(String inputString) {
/* 272 */     int inputLength = inputString.length();
/* 273 */     StringBuffer buffer = new StringBuffer();
/* 274 */     buffer.ensureCapacity(inputLength);
/*     */     
/* 276 */     for (int i = 0; i < inputLength; i++) {
/* 277 */       char currentChar = inputString.charAt(i);
/* 278 */       if (currentChar != '\\') {
/* 279 */         buffer.append(currentChar);
/*     */       }
/* 281 */       else if (i < inputLength - 1) {
/* 282 */         char nextChar = inputString.charAt(i + 1);
/* 283 */         buffer.append(nextChar);
/*     */ 
/*     */         
/* 286 */         i++;
/*     */       } else {
/* 288 */         buffer.append(currentChar);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 293 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\registries\MailcapTokenizer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */