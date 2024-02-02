/*     */ package org.h2.util.json;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public final class JSONBytesSource
/*     */   extends JSONTextSource
/*     */ {
/*     */   private final byte[] bytes;
/*     */   private final int length;
/*     */   private int index;
/*     */   
/*     */   public static <R> R parse(byte[] paramArrayOfbyte, JSONTarget<R> paramJSONTarget) {
/*  29 */     int i = paramArrayOfbyte.length;
/*  30 */     Charset charset = null;
/*  31 */     if (i >= 4) {
/*  32 */       byte b1 = paramArrayOfbyte[0];
/*  33 */       byte b2 = paramArrayOfbyte[1];
/*  34 */       byte b3 = paramArrayOfbyte[2];
/*  35 */       byte b4 = paramArrayOfbyte[3];
/*  36 */       switch (b1) {
/*     */         case -2:
/*  38 */           if (b2 == -1) {
/*  39 */             charset = StandardCharsets.UTF_16BE;
/*     */           }
/*     */           break;
/*     */         case -1:
/*  43 */           if (b2 == -2) {
/*  44 */             if (b3 == 0 && b4 == 0) {
/*  45 */               charset = Charset.forName("UTF-32LE"); break;
/*     */             } 
/*  47 */             charset = StandardCharsets.UTF_16LE;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 0:
/*  52 */           if (b2 != 0) {
/*  53 */             charset = StandardCharsets.UTF_16BE; break;
/*  54 */           }  if ((b3 == 0 && b4 != 0) || (b3 == -2 && b4 == -1)) {
/*  55 */             charset = Charset.forName("UTF-32BE");
/*     */           }
/*     */           break;
/*     */         default:
/*  59 */           if (b2 == 0) {
/*  60 */             if (b3 == 0 && b4 == 0) {
/*  61 */               charset = Charset.forName("UTF-32LE"); break;
/*     */             } 
/*  63 */             charset = StandardCharsets.UTF_16LE;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     
/*  68 */     } else if (i >= 2) {
/*  69 */       byte b1 = paramArrayOfbyte[0];
/*  70 */       byte b2 = paramArrayOfbyte[1];
/*  71 */       if (b1 != 0) {
/*  72 */         if (b2 == 0) {
/*  73 */           charset = StandardCharsets.UTF_16LE;
/*     */         }
/*  75 */       } else if (b2 != 0) {
/*  76 */         charset = StandardCharsets.UTF_16BE;
/*     */       } 
/*     */     } 
/*  79 */     ((charset == null) ? new JSONBytesSource(paramArrayOfbyte, paramJSONTarget) : new JSONStringSource(new String(paramArrayOfbyte, charset), paramJSONTarget))
/*  80 */       .parse();
/*  81 */     return paramJSONTarget.getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] normalize(byte[] paramArrayOfbyte) {
/*  92 */     return parse(paramArrayOfbyte, new JSONByteArrayTarget());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   JSONBytesSource(byte[] paramArrayOfbyte, JSONTarget<?> paramJSONTarget) {
/* 102 */     super(paramJSONTarget);
/* 103 */     this.bytes = paramArrayOfbyte;
/* 104 */     this.length = paramArrayOfbyte.length;
/*     */     
/* 106 */     if (nextChar() != 65279) {
/* 107 */       this.index = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   int nextCharAfterWhitespace() {
/* 113 */     int i = this.index;
/* 114 */     while (i < this.length) {
/* 115 */       byte b = this.bytes[i++];
/* 116 */       switch (b) {
/*     */         case 9:
/*     */         case 10:
/*     */         case 13:
/*     */         case 32:
/*     */           continue;
/*     */       } 
/* 123 */       if (b < 0) {
/* 124 */         throw new IllegalArgumentException();
/*     */       }
/* 126 */       this.index = i;
/* 127 */       return b;
/*     */     } 
/*     */     
/* 130 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   void readKeyword1(String paramString) {
/* 135 */     int i = paramString.length() - 1;
/* 136 */     if (this.index + i > this.length)
/* 137 */       throw new IllegalArgumentException();  int j;
/*     */     byte b;
/* 139 */     for (j = this.index, b = 1; b <= i; j++, b++) {
/* 140 */       if (this.bytes[j] != paramString.charAt(b)) {
/* 141 */         throw new IllegalArgumentException();
/*     */       }
/*     */     } 
/* 144 */     this.index += i;
/*     */   }
/*     */ 
/*     */   
/*     */   void parseNumber(boolean paramBoolean) {
/* 149 */     int i = this.index;
/* 150 */     int j = i - 1;
/* 151 */     i = skipInt(i, paramBoolean);
/* 152 */     if (i < this.length)
/* 153 */     { byte b = this.bytes[i];
/* 154 */       if (b == 46)
/* 155 */       { i = skipInt(i + 1, false);
/* 156 */         if (i >= this.length)
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
/* 172 */           this.target.valueNumber(new BigDecimal(new String(this.bytes, j, i - j)));
/* 173 */           this.index = i; return; }  b = this.bytes[i]; }  if (b == 69 || b == 101) { if (++i >= this.length) throw new IllegalArgumentException();  b = this.bytes[i]; if (b == 43 || b == 45) i++;  i = skipInt(i, false); }  }  this.target.valueNumber(new BigDecimal(new String(this.bytes, j, i - j))); this.index = i;
/*     */   }
/*     */   
/*     */   private int skipInt(int paramInt, boolean paramBoolean) {
/* 177 */     while (paramInt < this.length) {
/* 178 */       byte b = this.bytes[paramInt];
/* 179 */       if (b >= 48 && b <= 57) {
/* 180 */         paramBoolean = true;
/* 181 */         paramInt++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 186 */     if (!paramBoolean) {
/* 187 */       throw new IllegalArgumentException();
/*     */     }
/* 189 */     return paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   int nextChar() {
/* 194 */     if (this.index >= this.length) {
/* 195 */       throw new IllegalArgumentException();
/*     */     }
/* 197 */     int i = this.bytes[this.index++] & 0xFF;
/* 198 */     if (i >= 128) {
/* 199 */       if (i >= 224) {
/* 200 */         if (i >= 240) {
/* 201 */           if (this.index + 2 >= this.length) {
/* 202 */             throw new IllegalArgumentException();
/*     */           }
/* 204 */           int j = this.bytes[this.index++] & 0xFF;
/* 205 */           int k = this.bytes[this.index++] & 0xFF;
/* 206 */           int m = this.bytes[this.index++] & 0xFF;
/* 207 */           i = ((i & 0xF) << 18) + ((j & 0x3F) << 12) + ((k & 0x3F) << 6) + (m & 0x3F);
/* 208 */           if (i < 65536 || i > 1114111 || (j & 0xC0) != 128 || (k & 0xC0) != 128 || (m & 0xC0) != 128)
/*     */           {
/* 210 */             throw new IllegalArgumentException();
/*     */           }
/*     */         } else {
/* 213 */           if (this.index + 1 >= this.length) {
/* 214 */             throw new IllegalArgumentException();
/*     */           }
/* 216 */           int j = this.bytes[this.index++] & 0xFF;
/* 217 */           int k = this.bytes[this.index++] & 0xFF;
/* 218 */           i = ((i & 0xF) << 12) + ((j & 0x3F) << 6) + (k & 0x3F);
/* 219 */           if (i < 2048 || (j & 0xC0) != 128 || (k & 0xC0) != 128) {
/* 220 */             throw new IllegalArgumentException();
/*     */           }
/*     */         } 
/*     */       } else {
/* 224 */         if (this.index >= this.length) {
/* 225 */           throw new IllegalArgumentException();
/*     */         }
/* 227 */         int j = this.bytes[this.index++] & 0xFF;
/* 228 */         i = ((i & 0x1F) << 6) + (j & 0x3F);
/* 229 */         if (i < 128 || (j & 0xC0) != 128) {
/* 230 */           throw new IllegalArgumentException();
/*     */         }
/*     */       } 
/*     */     }
/* 234 */     return i;
/*     */   }
/*     */   
/*     */   char readHex() {
/*     */     int i;
/* 239 */     if (this.index + 3 >= this.length) {
/* 240 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*     */     try {
/* 244 */       i = Integer.parseInt(new String(this.bytes, this.index, 4), 16);
/* 245 */     } catch (NumberFormatException numberFormatException) {
/* 246 */       throw new IllegalArgumentException();
/*     */     } 
/* 248 */     this.index += 4;
/* 249 */     return (char)i;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 254 */     return new String(this.bytes, 0, this.index, StandardCharsets.UTF_8) + "[*]" + new String(this.bytes, this.index, this.length, StandardCharsets.UTF_8);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONBytesSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */