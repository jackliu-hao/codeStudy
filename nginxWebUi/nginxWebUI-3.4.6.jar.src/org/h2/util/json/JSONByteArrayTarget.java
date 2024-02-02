/*     */ package org.h2.util.json;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ 
/*     */ public final class JSONByteArrayTarget
/*     */   extends JSONTarget<byte[]>
/*     */ {
/*  23 */   private static final byte[] NULL_BYTES = "null".getBytes(StandardCharsets.ISO_8859_1);
/*     */   
/*  25 */   private static final byte[] FALSE_BYTES = "false".getBytes(StandardCharsets.ISO_8859_1);
/*     */   
/*  27 */   private static final byte[] TRUE_BYTES = "true".getBytes(StandardCharsets.ISO_8859_1);
/*     */   
/*  29 */   private static final byte[] U00_BYTES = "\\u00".getBytes(StandardCharsets.ISO_8859_1);
/*     */   
/*     */   private final ByteArrayOutputStream baos;
/*     */   
/*     */   private final ByteStack stack;
/*     */   
/*     */   private boolean needSeparator;
/*     */   
/*     */   private boolean afterName;
/*     */ 
/*     */   
/*     */   public static ByteArrayOutputStream encodeString(ByteArrayOutputStream paramByteArrayOutputStream, String paramString) {
/*  41 */     paramByteArrayOutputStream.write(34); byte b; int i;
/*  42 */     for (b = 0, i = paramString.length(); b < i; b++) {
/*  43 */       char c = paramString.charAt(b);
/*  44 */       switch (c) {
/*     */         case '\b':
/*  46 */           paramByteArrayOutputStream.write(92);
/*  47 */           paramByteArrayOutputStream.write(98);
/*     */           break;
/*     */         case '\t':
/*  50 */           paramByteArrayOutputStream.write(92);
/*  51 */           paramByteArrayOutputStream.write(116);
/*     */           break;
/*     */         case '\f':
/*  54 */           paramByteArrayOutputStream.write(92);
/*  55 */           paramByteArrayOutputStream.write(102);
/*     */           break;
/*     */         case '\n':
/*  58 */           paramByteArrayOutputStream.write(92);
/*  59 */           paramByteArrayOutputStream.write(110);
/*     */           break;
/*     */         case '\r':
/*  62 */           paramByteArrayOutputStream.write(92);
/*  63 */           paramByteArrayOutputStream.write(114);
/*     */           break;
/*     */         case '"':
/*  66 */           paramByteArrayOutputStream.write(92);
/*  67 */           paramByteArrayOutputStream.write(34);
/*     */           break;
/*     */         case '\\':
/*  70 */           paramByteArrayOutputStream.write(92);
/*  71 */           paramByteArrayOutputStream.write(92);
/*     */           break;
/*     */         default:
/*  74 */           if (c >= ' ') {
/*  75 */             if (c < '') {
/*  76 */               paramByteArrayOutputStream.write(c); break;
/*  77 */             }  if (c < 'ࠀ') {
/*  78 */               paramByteArrayOutputStream.write(0xC0 | c >> 6);
/*  79 */               paramByteArrayOutputStream.write(0x80 | c & 0x3F); break;
/*  80 */             }  if (!Character.isSurrogate(c)) {
/*  81 */               paramByteArrayOutputStream.write(0xE0 | c >> 12);
/*  82 */               paramByteArrayOutputStream.write(0x80 | c >> 6 & 0x3F);
/*  83 */               paramByteArrayOutputStream.write(0x80 | c & 0x3F); break;
/*     */             } 
/*     */             char c1;
/*  86 */             if (!Character.isHighSurrogate(c) || ++b >= i || 
/*  87 */               !Character.isLowSurrogate(c1 = paramString.charAt(b))) {
/*  88 */               throw new IllegalArgumentException();
/*     */             }
/*  90 */             int j = Character.toCodePoint(c, c1);
/*  91 */             paramByteArrayOutputStream.write(0xF0 | j >> 18);
/*  92 */             paramByteArrayOutputStream.write(0x80 | j >> 12 & 0x3F);
/*  93 */             paramByteArrayOutputStream.write(0x80 | j >> 6 & 0x3F);
/*  94 */             paramByteArrayOutputStream.write(0x80 | j & 0x3F);
/*     */             break;
/*     */           } 
/*  97 */           paramByteArrayOutputStream.write(U00_BYTES, 0, 4);
/*  98 */           paramByteArrayOutputStream.write(JSONStringTarget.HEX[c >>> 4 & 0xF]);
/*  99 */           paramByteArrayOutputStream.write(JSONStringTarget.HEX[c & 0xF]);
/*     */           break;
/*     */       } 
/*     */     } 
/* 103 */     paramByteArrayOutputStream.write(34);
/* 104 */     return paramByteArrayOutputStream;
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
/*     */   public JSONByteArrayTarget() {
/* 119 */     this.baos = new ByteArrayOutputStream();
/* 120 */     this.stack = new ByteStack();
/*     */   }
/*     */ 
/*     */   
/*     */   public void startObject() {
/* 125 */     beforeValue();
/* 126 */     this.afterName = false;
/* 127 */     this.stack.push((byte)1);
/* 128 */     this.baos.write(123);
/*     */   }
/*     */ 
/*     */   
/*     */   public void endObject() {
/* 133 */     if (this.afterName || this.stack.poll(-1) != 1) {
/* 134 */       throw new IllegalStateException();
/*     */     }
/* 136 */     this.baos.write(125);
/* 137 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void startArray() {
/* 142 */     beforeValue();
/* 143 */     this.afterName = false;
/* 144 */     this.stack.push((byte)2);
/* 145 */     this.baos.write(91);
/*     */   }
/*     */ 
/*     */   
/*     */   public void endArray() {
/* 150 */     if (this.stack.poll(-1) != 2) {
/* 151 */       throw new IllegalStateException();
/*     */     }
/* 153 */     this.baos.write(93);
/* 154 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void member(String paramString) {
/* 159 */     if (this.afterName || this.stack.peek(-1) != 1) {
/* 160 */       throw new IllegalStateException();
/*     */     }
/* 162 */     this.afterName = true;
/* 163 */     beforeValue();
/* 164 */     encodeString(this.baos, paramString).write(58);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNull() {
/* 169 */     beforeValue();
/* 170 */     this.baos.write(NULL_BYTES, 0, 4);
/* 171 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueFalse() {
/* 176 */     beforeValue();
/* 177 */     this.baos.write(FALSE_BYTES, 0, 5);
/* 178 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueTrue() {
/* 183 */     beforeValue();
/* 184 */     this.baos.write(TRUE_BYTES, 0, 4);
/* 185 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNumber(BigDecimal paramBigDecimal) {
/* 190 */     beforeValue();
/* 191 */     String str = paramBigDecimal.toString();
/* 192 */     int i = str.indexOf('E');
/* 193 */     byte[] arrayOfByte = str.getBytes(StandardCharsets.ISO_8859_1);
/* 194 */     if (i >= 0 && str.charAt(++i) == '+') {
/* 195 */       this.baos.write(arrayOfByte, 0, i);
/* 196 */       this.baos.write(arrayOfByte, i + 1, arrayOfByte.length - i - 1);
/*     */     } else {
/* 198 */       this.baos.write(arrayOfByte, 0, arrayOfByte.length);
/*     */     } 
/* 200 */     afterValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueString(String paramString) {
/* 205 */     beforeValue();
/* 206 */     encodeString(this.baos, paramString);
/* 207 */     afterValue();
/*     */   }
/*     */   
/*     */   private void beforeValue() {
/* 211 */     if (!this.afterName && this.stack.peek(-1) == 1) {
/* 212 */       throw new IllegalStateException();
/*     */     }
/* 214 */     if (this.needSeparator) {
/* 215 */       if (this.stack.isEmpty()) {
/* 216 */         throw new IllegalStateException();
/*     */       }
/* 218 */       this.needSeparator = false;
/* 219 */       this.baos.write(44);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void afterValue() {
/* 224 */     this.needSeparator = true;
/* 225 */     this.afterName = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPropertyExpected() {
/* 230 */     return (!this.afterName && this.stack.peek(-1) == 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValueSeparatorExpected() {
/* 235 */     return this.needSeparator;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getResult() {
/* 240 */     if (!this.stack.isEmpty() || this.baos.size() == 0) {
/* 241 */       throw new IllegalStateException();
/*     */     }
/* 243 */     return this.baos.toByteArray();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONByteArrayTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */