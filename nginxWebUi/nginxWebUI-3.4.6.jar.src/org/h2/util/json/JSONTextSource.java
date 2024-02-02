/*     */ package org.h2.util.json;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JSONTextSource
/*     */ {
/*     */   final JSONTarget<?> target;
/*     */   private final StringBuilder builder;
/*     */   
/*     */   JSONTextSource(JSONTarget<?> paramJSONTarget) {
/*  21 */     this.target = paramJSONTarget;
/*  22 */     this.builder = new StringBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void parse() {
/*  29 */     boolean bool = false; int i;
/*  30 */     while ((i = nextCharAfterWhitespace()) >= 0) {
/*  31 */       String str; if (i == 125 || i == 93) {
/*  32 */         if (bool) {
/*  33 */           throw new IllegalArgumentException();
/*     */         }
/*  35 */         if (i == 125) {
/*  36 */           this.target.endObject(); continue;
/*     */         } 
/*  38 */         this.target.endArray();
/*     */         
/*     */         continue;
/*     */       } 
/*  42 */       if (i == 44) {
/*  43 */         if (bool || !this.target.isValueSeparatorExpected()) {
/*  44 */           throw new IllegalArgumentException();
/*     */         }
/*  46 */         bool = true;
/*     */         continue;
/*     */       } 
/*  49 */       if (bool != this.target.isValueSeparatorExpected()) {
/*  50 */         throw new IllegalArgumentException();
/*     */       }
/*  52 */       bool = false;
/*  53 */       switch (i) {
/*     */         case 102:
/*  55 */           readKeyword1("false");
/*  56 */           this.target.valueFalse();
/*     */           continue;
/*     */         case 110:
/*  59 */           readKeyword1("null");
/*  60 */           this.target.valueNull();
/*     */           continue;
/*     */         case 116:
/*  63 */           readKeyword1("true");
/*  64 */           this.target.valueTrue();
/*     */           continue;
/*     */         case 123:
/*  67 */           this.target.startObject();
/*     */           continue;
/*     */         case 91:
/*  70 */           this.target.startArray();
/*     */           continue;
/*     */         case 34:
/*  73 */           str = readString();
/*  74 */           if (this.target.isPropertyExpected()) {
/*  75 */             if (nextCharAfterWhitespace() != 58) {
/*  76 */               throw new IllegalArgumentException();
/*     */             }
/*  78 */             this.target.member(str); continue;
/*     */           } 
/*  80 */           this.target.valueString(str);
/*     */           continue;
/*     */ 
/*     */         
/*     */         case 45:
/*  85 */           parseNumber(false);
/*     */           continue;
/*     */         case 48:
/*     */         case 49:
/*     */         case 50:
/*     */         case 51:
/*     */         case 52:
/*     */         case 53:
/*     */         case 54:
/*     */         case 55:
/*     */         case 56:
/*     */         case 57:
/*  97 */           parseNumber(true);
/*     */           continue;
/*     */       } 
/* 100 */       throw new IllegalArgumentException();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract int nextCharAfterWhitespace();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void readKeyword1(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void parseNumber(boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract int nextChar();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract char readHex();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String readString() {
/* 142 */     this.builder.setLength(0);
/* 143 */     boolean bool = false;
/*     */     while (true) {
/* 145 */       int i = nextChar();
/* 146 */       switch (i) {
/*     */         case 34:
/* 148 */           if (bool) {
/* 149 */             throw new IllegalArgumentException();
/*     */           }
/* 151 */           return this.builder.toString();
/*     */         case 92:
/* 153 */           i = nextChar();
/* 154 */           switch (i) {
/*     */             case 34:
/*     */             case 47:
/*     */             case 92:
/* 158 */               appendNonSurrogate((char)i, bool);
/*     */               continue;
/*     */             case 98:
/* 161 */               appendNonSurrogate('\b', bool);
/*     */               continue;
/*     */             case 102:
/* 164 */               appendNonSurrogate('\f', bool);
/*     */               continue;
/*     */             case 110:
/* 167 */               appendNonSurrogate('\n', bool);
/*     */               continue;
/*     */             case 114:
/* 170 */               appendNonSurrogate('\r', bool);
/*     */               continue;
/*     */             case 116:
/* 173 */               appendNonSurrogate('\t', bool);
/*     */               continue;
/*     */             case 117:
/* 176 */               bool = appendChar(readHex(), bool);
/*     */               continue;
/*     */           } 
/* 179 */           throw new IllegalArgumentException();
/*     */       } 
/*     */ 
/*     */       
/* 183 */       if (Character.isBmpCodePoint(i)) {
/* 184 */         bool = appendChar((char)i, bool); continue;
/*     */       } 
/* 186 */       if (bool) {
/* 187 */         throw new IllegalArgumentException();
/*     */       }
/* 189 */       this.builder.appendCodePoint(i);
/* 190 */       bool = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void appendNonSurrogate(char paramChar, boolean paramBoolean) {
/* 197 */     if (paramBoolean) {
/* 198 */       throw new IllegalArgumentException();
/*     */     }
/* 200 */     this.builder.append(paramChar);
/*     */   }
/*     */   
/*     */   private boolean appendChar(char paramChar, boolean paramBoolean) {
/* 204 */     if (paramBoolean != Character.isLowSurrogate(paramChar)) {
/* 205 */       throw new IllegalArgumentException();
/*     */     }
/* 207 */     if (paramBoolean) {
/* 208 */       paramBoolean = false;
/* 209 */     } else if (Character.isHighSurrogate(paramChar)) {
/* 210 */       paramBoolean = true;
/*     */     } 
/* 212 */     this.builder.append(paramChar);
/* 213 */     return paramBoolean;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONTextSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */