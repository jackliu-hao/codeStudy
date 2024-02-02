/*     */ package com.mysql.cj.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EscapeTokenizer
/*     */ {
/*     */   private static final char CHR_ESCAPE = '\\';
/*     */   private static final char CHR_SGL_QUOTE = '\'';
/*     */   private static final char CHR_DBL_QUOTE = '"';
/*     */   private static final char CHR_LF = '\n';
/*     */   private static final char CHR_CR = '\r';
/*     */   private static final char CHR_COMMENT = '-';
/*     */   private static final char CHR_BEGIN_TOKEN = '{';
/*     */   private static final char CHR_END_TOKEN = '}';
/*     */   private static final char CHR_VARIABLE = '@';
/*  46 */   private String source = null;
/*  47 */   private int sourceLength = 0;
/*  48 */   private int pos = 0;
/*     */   
/*     */   private boolean emittingEscapeCode = false;
/*     */   private boolean sawVariableUse = false;
/*  52 */   private int bracesLevel = 0;
/*     */   private boolean inQuotes = false;
/*  54 */   private char quoteChar = Character.MIN_VALUE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EscapeTokenizer(String source) {
/*  63 */     this.source = source;
/*  64 */     this.sourceLength = source.length();
/*  65 */     this.pos = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean hasMoreTokens() {
/*  74 */     return (this.pos < this.sourceLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String nextToken() {
/*  83 */     StringBuilder tokenBuf = new StringBuilder();
/*  84 */     boolean backslashEscape = false;
/*     */     
/*  86 */     if (this.emittingEscapeCode) {
/*     */       
/*  88 */       tokenBuf.append("{");
/*  89 */       this.emittingEscapeCode = false;
/*     */     }  while (true) {
/*     */       char c;
/*  92 */       if (this.pos < this.sourceLength)
/*  93 */       { c = this.source.charAt(this.pos);
/*     */ 
/*     */         
/*  96 */         if (c == '\\') {
/*  97 */           tokenBuf.append(c);
/*  98 */           backslashEscape = !backslashEscape;
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 103 */         if ((c == '\'' || c == '"') && !backslashEscape) {
/* 104 */           tokenBuf.append(c);
/* 105 */           if (this.inQuotes) {
/* 106 */             if (c == this.quoteChar)
/*     */             {
/* 108 */               if (this.pos + 1 < this.sourceLength && this.source.charAt(this.pos + 1) == this.quoteChar) {
/* 109 */                 tokenBuf.append(c);
/* 110 */                 this.pos++;
/*     */               } else {
/* 112 */                 this.inQuotes = false;
/*     */               } 
/*     */             }
/*     */           } else {
/* 116 */             this.inQuotes = true;
/* 117 */             this.quoteChar = c;
/*     */           } 
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 123 */         if (c == '\n' || c == '\r') {
/* 124 */           tokenBuf.append(c);
/* 125 */           backslashEscape = false;
/*     */           
/*     */           continue;
/*     */         } 
/* 129 */         if (!this.inQuotes && !backslashEscape)
/*     */         
/* 131 */         { if (c == '-')
/* 132 */           { tokenBuf.append(c);
/*     */             
/* 134 */             if (this.pos + 1 < this.sourceLength && this.source.charAt(this.pos + 1) == '-')
/*     */             {
/* 136 */               while (++this.pos < this.sourceLength && c != '\n' && c != '\r') {
/* 137 */                 c = this.source.charAt(this.pos);
/* 138 */                 tokenBuf.append(c);
/*     */               } 
/* 140 */               this.pos--;
/*     */             
/*     */             }
/*     */             
/*     */              }
/*     */           
/* 146 */           else if (c == '{')
/* 147 */           { this.bracesLevel++;
/* 148 */             if (this.bracesLevel == 1) {
/* 149 */               this.emittingEscapeCode = true;
/* 150 */               this.pos++;
/* 151 */               return tokenBuf.toString();
/*     */             } 
/* 153 */             tokenBuf.append(c);
/*     */ 
/*     */             
/*     */              }
/*     */           
/* 158 */           else if (c == '}')
/* 159 */           { tokenBuf.append(c);
/* 160 */             this.bracesLevel--;
/* 161 */             if (this.bracesLevel == 0) {
/* 162 */               this.pos++;
/* 163 */               return tokenBuf.toString();
/*     */             }
/*     */              }
/*     */           
/*     */           else
/*     */           
/* 169 */           { if (c == '@') {
/* 170 */               this.sawVariableUse = true;
/*     */             }
/*     */ 
/*     */             
/* 174 */             tokenBuf.append(c);
/* 175 */             backslashEscape = false; }  continue; }  } else { break; }  tokenBuf.append(c); backslashEscape = false;
/*     */       this.pos++;
/*     */     } 
/* 178 */     return tokenBuf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean sawVariableUse() {
/* 188 */     return this.sawVariableUse;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\EscapeTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */