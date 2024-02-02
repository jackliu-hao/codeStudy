/*     */ package org.h2.util.json;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JSONStringSource
/*     */   extends JSONTextSource
/*     */ {
/*     */   private final String string;
/*     */   private final int length;
/*     */   private int index;
/*     */   
/*     */   public static <R> R parse(String paramString, JSONTarget<R> paramJSONTarget) {
/*  29 */     (new JSONStringSource(paramString, paramJSONTarget)).parse();
/*  30 */     return paramJSONTarget.getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] normalize(String paramString) {
/*  41 */     return parse(paramString, new JSONByteArrayTarget());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   JSONStringSource(String paramString, JSONTarget<?> paramJSONTarget) {
/*  51 */     super(paramJSONTarget);
/*  52 */     this.string = paramString;
/*  53 */     this.length = paramString.length();
/*  54 */     if (this.length == 0) {
/*  55 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*  58 */     if (paramString.charAt(this.index) == '﻿') {
/*  59 */       this.index++;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   int nextCharAfterWhitespace() {
/*  65 */     int i = this.index;
/*  66 */     while (i < this.length) {
/*  67 */       char c = this.string.charAt(i++);
/*  68 */       switch (c) {
/*     */         case '\t':
/*     */         case '\n':
/*     */         case '\r':
/*     */         case ' ':
/*     */           continue;
/*     */       } 
/*  75 */       this.index = i;
/*  76 */       return c;
/*     */     } 
/*     */     
/*  79 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   void readKeyword1(String paramString) {
/*  84 */     int i = paramString.length() - 1;
/*  85 */     if (!this.string.regionMatches(this.index, paramString, 1, i)) {
/*  86 */       throw new IllegalArgumentException();
/*     */     }
/*  88 */     this.index += i;
/*     */   }
/*     */ 
/*     */   
/*     */   void parseNumber(boolean paramBoolean) {
/*  93 */     int i = this.index;
/*  94 */     int j = i - 1;
/*  95 */     i = skipInt(i, paramBoolean);
/*  96 */     if (i < this.length)
/*  97 */     { char c = this.string.charAt(i);
/*  98 */       if (c == '.')
/*  99 */       { i = skipInt(i + 1, false);
/* 100 */         if (i >= this.length)
/*     */         
/*     */         { 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 116 */           this.target.valueNumber(new BigDecimal(this.string.substring(j, i)));
/* 117 */           this.index = i; return; }  c = this.string.charAt(i); }  if (c == 'E' || c == 'e') { if (++i >= this.length) throw new IllegalArgumentException();  c = this.string.charAt(i); if (c == '+' || c == '-') i++;  i = skipInt(i, false); }  }  this.target.valueNumber(new BigDecimal(this.string.substring(j, i))); this.index = i;
/*     */   }
/*     */   
/*     */   private int skipInt(int paramInt, boolean paramBoolean) {
/* 121 */     while (paramInt < this.length) {
/* 122 */       char c = this.string.charAt(paramInt);
/* 123 */       if (c >= '0' && c <= '9') {
/* 124 */         paramBoolean = true;
/* 125 */         paramInt++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 130 */     if (!paramBoolean) {
/* 131 */       throw new IllegalArgumentException();
/*     */     }
/* 133 */     return paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   int nextChar() {
/* 138 */     if (this.index >= this.length) {
/* 139 */       throw new IllegalArgumentException();
/*     */     }
/* 141 */     return this.string.charAt(this.index++);
/*     */   }
/*     */ 
/*     */   
/*     */   char readHex() {
/* 146 */     if (this.index + 3 >= this.length) {
/* 147 */       throw new IllegalArgumentException();
/*     */     }
/*     */     try {
/* 150 */       return (char)Integer.parseInt(this.string.substring(this.index, this.index += 4), 16);
/* 151 */     } catch (NumberFormatException numberFormatException) {
/* 152 */       throw new IllegalArgumentException();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 158 */     return StringUtils.addAsterisk(this.string, this.index);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONStringSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */