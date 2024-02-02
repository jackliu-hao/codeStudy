/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.zip.DeflaterOutputStream;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import java.util.zip.InflaterInputStream;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ import org.h2.compress.CompressDeflate;
/*     */ import org.h2.compress.CompressLZF;
/*     */ import org.h2.compress.CompressNo;
/*     */ import org.h2.compress.Compressor;
/*     */ import org.h2.compress.LZFInputStream;
/*     */ import org.h2.compress.LZFOutputStream;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
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
/*     */ public class CompressTool
/*     */ {
/*     */   private static final int MAX_BUFFER_SIZE = 393216;
/*     */   private byte[] buffer;
/*     */   
/*     */   private byte[] getBuffer(int paramInt) {
/*  46 */     if (paramInt > 393216) {
/*  47 */       return Utils.newBytes(paramInt);
/*     */     }
/*  49 */     if (this.buffer == null || this.buffer.length < paramInt) {
/*  50 */       this.buffer = Utils.newBytes(paramInt);
/*     */     }
/*  52 */     return this.buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CompressTool getInstance() {
/*  63 */     return new CompressTool();
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
/*     */   public byte[] compress(byte[] paramArrayOfbyte, String paramString) {
/*  75 */     int i = paramArrayOfbyte.length;
/*  76 */     if (paramArrayOfbyte.length < 5) {
/*  77 */       paramString = "NO";
/*     */     }
/*  79 */     Compressor compressor = getCompressor(paramString);
/*  80 */     byte[] arrayOfByte = getBuffer(((i < 100) ? (i + 100) : i) * 2);
/*  81 */     int j = compress(paramArrayOfbyte, paramArrayOfbyte.length, compressor, arrayOfByte);
/*  82 */     return Utils.copyBytes(arrayOfByte, j);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int compress(byte[] paramArrayOfbyte1, int paramInt, Compressor paramCompressor, byte[] paramArrayOfbyte2) {
/*  87 */     paramArrayOfbyte2[0] = (byte)paramCompressor.getAlgorithm();
/*  88 */     int i = 1 + writeVariableInt(paramArrayOfbyte2, 1, paramInt);
/*  89 */     int j = paramCompressor.compress(paramArrayOfbyte1, 0, paramInt, paramArrayOfbyte2, i);
/*  90 */     if (j > paramInt + i || j <= 0) {
/*  91 */       paramArrayOfbyte2[0] = 0;
/*  92 */       System.arraycopy(paramArrayOfbyte1, 0, paramArrayOfbyte2, i, paramInt);
/*  93 */       j = paramInt + i;
/*     */     } 
/*  95 */     return j;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] expand(byte[] paramArrayOfbyte) {
/* 105 */     if (paramArrayOfbyte.length == 0) {
/* 106 */       throw DbException.get(90104);
/*     */     }
/* 108 */     byte b = paramArrayOfbyte[0];
/* 109 */     Compressor compressor = getCompressor(b);
/*     */     try {
/* 111 */       int i = readVariableInt(paramArrayOfbyte, 1);
/* 112 */       int j = 1 + getVariableIntLength(i);
/* 113 */       byte[] arrayOfByte = Utils.newBytes(i);
/* 114 */       compressor.expand(paramArrayOfbyte, j, paramArrayOfbyte.length - j, arrayOfByte, 0, i);
/* 115 */       return arrayOfByte;
/* 116 */     } catch (Exception exception) {
/* 117 */       throw DbException.get(90104, exception, new String[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void expand(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
/* 128 */     byte b = paramArrayOfbyte1[0];
/* 129 */     Compressor compressor = getCompressor(b);
/*     */     try {
/* 131 */       int i = readVariableInt(paramArrayOfbyte1, 1);
/* 132 */       int j = 1 + getVariableIntLength(i);
/* 133 */       compressor.expand(paramArrayOfbyte1, j, paramArrayOfbyte1.length - j, paramArrayOfbyte2, paramInt, i);
/* 134 */     } catch (Exception exception) {
/* 135 */       throw DbException.get(90104, exception, new String[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int readVariableInt(byte[] paramArrayOfbyte, int paramInt) {
/* 147 */     int i = paramArrayOfbyte[paramInt++] & 0xFF;
/* 148 */     if (i < 128) {
/* 149 */       return i;
/*     */     }
/* 151 */     if (i < 192) {
/* 152 */       return ((i & 0x3F) << 8) + (paramArrayOfbyte[paramInt] & 0xFF);
/*     */     }
/* 154 */     if (i < 224) {
/* 155 */       return ((i & 0x1F) << 16) + ((paramArrayOfbyte[paramInt++] & 0xFF) << 8) + (paramArrayOfbyte[paramInt] & 0xFF);
/*     */     }
/*     */ 
/*     */     
/* 159 */     if (i < 240) {
/* 160 */       return ((i & 0xF) << 24) + ((paramArrayOfbyte[paramInt++] & 0xFF) << 16) + ((paramArrayOfbyte[paramInt++] & 0xFF) << 8) + (paramArrayOfbyte[paramInt] & 0xFF);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 165 */     return Bits.readInt(paramArrayOfbyte, paramInt);
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
/*     */   public static int writeVariableInt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 178 */     if (paramInt2 < 0) {
/* 179 */       paramArrayOfbyte[paramInt1++] = -16;
/* 180 */       Bits.writeInt(paramArrayOfbyte, paramInt1, paramInt2);
/* 181 */       return 5;
/* 182 */     }  if (paramInt2 < 128) {
/* 183 */       paramArrayOfbyte[paramInt1] = (byte)paramInt2;
/* 184 */       return 1;
/* 185 */     }  if (paramInt2 < 16384) {
/* 186 */       paramArrayOfbyte[paramInt1++] = (byte)(0x80 | paramInt2 >> 8);
/* 187 */       paramArrayOfbyte[paramInt1] = (byte)paramInt2;
/* 188 */       return 2;
/* 189 */     }  if (paramInt2 < 2097152) {
/* 190 */       paramArrayOfbyte[paramInt1++] = (byte)(0xC0 | paramInt2 >> 16);
/* 191 */       paramArrayOfbyte[paramInt1++] = (byte)(paramInt2 >> 8);
/* 192 */       paramArrayOfbyte[paramInt1] = (byte)paramInt2;
/* 193 */       return 3;
/* 194 */     }  if (paramInt2 < 268435456) {
/* 195 */       Bits.writeInt(paramArrayOfbyte, paramInt1, paramInt2 | 0xE0000000);
/* 196 */       return 4;
/*     */     } 
/* 198 */     paramArrayOfbyte[paramInt1++] = -16;
/* 199 */     Bits.writeInt(paramArrayOfbyte, paramInt1, paramInt2);
/* 200 */     return 5;
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
/*     */   public static int getVariableIntLength(int paramInt) {
/* 212 */     if (paramInt < 0)
/* 213 */       return 5; 
/* 214 */     if (paramInt < 128)
/* 215 */       return 1; 
/* 216 */     if (paramInt < 16384)
/* 217 */       return 2; 
/* 218 */     if (paramInt < 2097152)
/* 219 */       return 3; 
/* 220 */     if (paramInt < 268435456) {
/* 221 */       return 4;
/*     */     }
/* 223 */     return 5;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Compressor getCompressor(String paramString) {
/* 228 */     if (paramString == null) {
/* 229 */       paramString = "LZF";
/*     */     }
/* 231 */     int i = paramString.indexOf(' ');
/* 232 */     String str = null;
/* 233 */     if (i > 0) {
/* 234 */       str = paramString.substring(i + 1);
/* 235 */       paramString = paramString.substring(0, i);
/*     */     } 
/* 237 */     int j = getCompressAlgorithm(paramString);
/* 238 */     Compressor compressor = getCompressor(j);
/* 239 */     compressor.setOptions(str);
/* 240 */     return compressor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getCompressAlgorithm(String paramString) {
/* 249 */     paramString = StringUtils.toUpperEnglish(paramString);
/* 250 */     if ("NO".equals(paramString))
/* 251 */       return 0; 
/* 252 */     if ("LZF".equals(paramString))
/* 253 */       return 1; 
/* 254 */     if ("DEFLATE".equals(paramString)) {
/* 255 */       return 2;
/*     */     }
/* 257 */     throw DbException.get(90103, paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Compressor getCompressor(int paramInt) {
/* 264 */     switch (paramInt) {
/*     */       case 0:
/* 266 */         return (Compressor)new CompressNo();
/*     */       case 1:
/* 268 */         return (Compressor)new CompressLZF();
/*     */       case 2:
/* 270 */         return (Compressor)new CompressDeflate();
/*     */     } 
/* 272 */     throw DbException.get(90103, 
/*     */         
/* 274 */         Integer.toString(paramInt));
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
/*     */   public static OutputStream wrapOutputStream(OutputStream paramOutputStream, String paramString1, String paramString2) {
/*     */     try {
/*     */       LZFOutputStream lZFOutputStream;
/* 288 */       if ("GZIP".equals(paramString1)) {
/* 289 */         paramOutputStream = new GZIPOutputStream(paramOutputStream);
/* 290 */       } else if ("ZIP".equals(paramString1)) {
/* 291 */         ZipOutputStream zipOutputStream = new ZipOutputStream(paramOutputStream);
/* 292 */         zipOutputStream.putNextEntry(new ZipEntry(paramString2));
/* 293 */         paramOutputStream = zipOutputStream;
/* 294 */       } else if ("DEFLATE".equals(paramString1)) {
/* 295 */         paramOutputStream = new DeflaterOutputStream(paramOutputStream);
/* 296 */       } else if ("LZF".equals(paramString1)) {
/* 297 */         lZFOutputStream = new LZFOutputStream(paramOutputStream);
/* 298 */       } else if (paramString1 != null) {
/* 299 */         throw DbException.get(90103, paramString1);
/*     */       } 
/*     */ 
/*     */       
/* 303 */       return (OutputStream)lZFOutputStream;
/* 304 */     } catch (IOException iOException) {
/* 305 */       throw DbException.convertIOException(iOException, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static InputStream wrapInputStream(InputStream paramInputStream, String paramString1, String paramString2) {
/*     */     try {
/*     */       LZFInputStream lZFInputStream;
/* 319 */       if ("GZIP".equals(paramString1)) {
/* 320 */         paramInputStream = new GZIPInputStream(paramInputStream);
/* 321 */       } else if ("ZIP".equals(paramString1)) {
/* 322 */         ZipEntry zipEntry; ZipInputStream zipInputStream = new ZipInputStream(paramInputStream);
/*     */         do {
/* 324 */           zipEntry = zipInputStream.getNextEntry();
/* 325 */           if (zipEntry == null) {
/* 326 */             return null;
/*     */           }
/* 328 */         } while (!paramString2.equals(zipEntry.getName()));
/*     */ 
/*     */ 
/*     */         
/* 332 */         paramInputStream = zipInputStream;
/* 333 */       } else if ("DEFLATE".equals(paramString1)) {
/* 334 */         paramInputStream = new InflaterInputStream(paramInputStream);
/* 335 */       } else if ("LZF".equals(paramString1)) {
/* 336 */         lZFInputStream = new LZFInputStream(paramInputStream);
/* 337 */       } else if (paramString1 != null) {
/* 338 */         throw DbException.get(90103, paramString1);
/*     */       } 
/*     */ 
/*     */       
/* 342 */       return (InputStream)lZFInputStream;
/* 343 */     } catch (IOException iOException) {
/* 344 */       throw DbException.convertIOException(iOException, null);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\CompressTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */