/*     */ package org.h2.util.json;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import org.h2.util.ByteStack;
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
/*     */ public final class JSONStringTarget
/*     */   extends JSONTarget<String>
/*     */ {
/*  20 */   static final char[] HEX = "0123456789abcdef".toCharArray();
/*     */ 
/*     */   
/*     */   static final byte OBJECT = 1;
/*     */ 
/*     */   
/*     */   static final byte ARRAY = 2;
/*     */ 
/*     */   
/*     */   private final StringBuilder builder;
/*     */ 
/*     */   
/*     */   private final ByteStack stack;
/*     */ 
/*     */   
/*     */   private final boolean asciiPrintableOnly;
/*     */ 
/*     */   
/*     */   private boolean needSeparator;
/*     */ 
/*     */   
/*     */   private boolean afterName;
/*     */ 
/*     */   
/*     */   public static StringBuilder encodeString(StringBuilder paramStringBuilder, String paramString, boolean paramBoolean) {
/*  45 */     paramStringBuilder.append('"'); byte b; int i;
/*  46 */     for (b = 0, i = paramString.length(); b < i; b++) {
/*  47 */       char c = paramString.charAt(b);
/*  48 */       switch (c) {
/*     */         case '\b':
/*  50 */           paramStringBuilder.append("\\b");
/*     */           break;
/*     */         case '\t':
/*  53 */           paramStringBuilder.append("\\t");
/*     */           break;
/*     */         case '\f':
/*  56 */           paramStringBuilder.append("\\f");
/*     */           break;
/*     */         case '\n':
/*  59 */           paramStringBuilder.append("\\n");
/*     */           break;
/*     */         case '\r':
/*  62 */           paramStringBuilder.append("\\r");
/*     */           break;
/*     */         case '"':
/*  65 */           paramStringBuilder.append("\\\"");
/*     */           break;
/*     */         case '\'':
/*  68 */           if (paramBoolean) {
/*  69 */             paramStringBuilder.append("\\u0027"); break;
/*     */           } 
/*  71 */           paramStringBuilder.append('\'');
/*     */           break;
/*     */         
/*     */         case '\\':
/*  75 */           paramStringBuilder.append("\\\\");
/*     */           break;
/*     */         default:
/*  78 */           if (c < ' ') {
/*  79 */             paramStringBuilder.append("\\u00")
/*  80 */               .append(HEX[c >>> 4 & 0xF])
/*  81 */               .append(HEX[c & 0xF]); break;
/*  82 */           }  if (!paramBoolean || c <= '') {
/*  83 */             paramStringBuilder.append(c); break;
/*     */           } 
/*  85 */           paramStringBuilder.append("\\u")
/*  86 */             .append(HEX[c >>> 12 & 0xF])
/*  87 */             .append(HEX[c >>> 8 & 0xF])
/*  88 */             .append(HEX[c >>> 4 & 0xF])
/*  89 */             .append(HEX[c & 0xF]);
/*     */           break;
/*     */       } 
/*     */     } 
/*  93 */     return paramStringBuilder.append('"');
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
/*     */   public JSONStringTarget() {
/* 110 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONStringTarget(boolean paramBoolean) {
/* 121 */     this.builder = new StringBuilder();
/* 122 */     this.stack = new ByteStack();
/* 123 */     this.asciiPrintableOnly = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void startObject() {
/* 128 */     beforeValue();
/* 129 */     this.afterName = false;
/* 130 */     this.stack.push((byte)1);
/* 131 */     this.builder.append('{');
/*     */   }
/*     */ 
/*     */   
/*     */   public void endObject() {
/* 136 */     if (this.afterName || this.stack.poll(-1) != 1) {
/* 137 */       throw new IllegalStateException();
/*     */     }
/* 139 */     this.builder.append('}');
/* 140 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void startArray() {
/* 145 */     beforeValue();
/* 146 */     this.afterName = false;
/* 147 */     this.stack.push((byte)2);
/* 148 */     this.builder.append('[');
/*     */   }
/*     */ 
/*     */   
/*     */   public void endArray() {
/* 153 */     if (this.stack.poll(-1) != 2) {
/* 154 */       throw new IllegalStateException();
/*     */     }
/* 156 */     this.builder.append(']');
/* 157 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void member(String paramString) {
/* 162 */     if (this.afterName || this.stack.peek(-1) != 1) {
/* 163 */       throw new IllegalStateException();
/*     */     }
/* 165 */     this.afterName = true;
/* 166 */     beforeValue();
/* 167 */     encodeString(this.builder, paramString, this.asciiPrintableOnly).append(':');
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNull() {
/* 172 */     beforeValue();
/* 173 */     this.builder.append("null");
/* 174 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueFalse() {
/* 179 */     beforeValue();
/* 180 */     this.builder.append("false");
/* 181 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueTrue() {
/* 186 */     beforeValue();
/* 187 */     this.builder.append("true");
/* 188 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNumber(BigDecimal paramBigDecimal) {
/* 193 */     beforeValue();
/* 194 */     String str = paramBigDecimal.toString();
/* 195 */     int i = str.indexOf('E');
/* 196 */     if (i >= 0 && str.charAt(++i) == '+') {
/* 197 */       this.builder.append(str, 0, i).append(str, i + 1, str.length());
/*     */     } else {
/* 199 */       this.builder.append(str);
/*     */     } 
/* 201 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueString(String paramString) {
/* 206 */     beforeValue();
/* 207 */     encodeString(this.builder, paramString, this.asciiPrintableOnly);
/* 208 */     afterValue();
/*     */   }
/*     */   
/*     */   private void beforeValue() {
/* 212 */     if (!this.afterName && this.stack.peek(-1) == 1) {
/* 213 */       throw new IllegalStateException();
/*     */     }
/* 215 */     if (this.needSeparator) {
/* 216 */       if (this.stack.isEmpty()) {
/* 217 */         throw new IllegalStateException();
/*     */       }
/* 219 */       this.needSeparator = false;
/* 220 */       this.builder.append(',');
/*     */     } 
/*     */   }
/*     */   
/*     */   private void afterValue() {
/* 225 */     this.needSeparator = true;
/* 226 */     this.afterName = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPropertyExpected() {
/* 231 */     return (!this.afterName && this.stack.peek(-1) == 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValueSeparatorExpected() {
/* 236 */     return this.needSeparator;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getResult() {
/* 241 */     if (!this.stack.isEmpty() || this.builder.length() == 0) {
/* 242 */       throw new IllegalStateException();
/*     */     }
/* 244 */     return this.builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONStringTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */