/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.store.fs.FileUtils;
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
/*     */ public class IOUtils
/*     */ {
/*     */   public static void closeSilently(AutoCloseable paramAutoCloseable) {
/*  44 */     if (paramAutoCloseable != null) {
/*     */       try {
/*  46 */         trace("closeSilently", null, paramAutoCloseable);
/*  47 */         paramAutoCloseable.close();
/*  48 */       } catch (Exception exception) {}
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void skipFully(InputStream paramInputStream, long paramLong) throws IOException {
/*     */     try {
/*  65 */       while (paramLong > 0L) {
/*  66 */         long l = paramInputStream.skip(paramLong);
/*  67 */         if (l <= 0L) {
/*  68 */           throw new EOFException();
/*     */         }
/*  70 */         paramLong -= l;
/*     */       } 
/*  72 */     } catch (Exception exception) {
/*  73 */       throw DataUtils.convertToIOException(exception);
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
/*     */ 
/*     */   
/*     */   public static void skipFully(Reader paramReader, long paramLong) throws IOException {
/*     */     try {
/*  88 */       while (paramLong > 0L) {
/*  89 */         long l = paramReader.skip(paramLong);
/*  90 */         if (l <= 0L) {
/*  91 */           throw new EOFException();
/*     */         }
/*  93 */         paramLong -= l;
/*     */       } 
/*  95 */     } catch (Exception exception) {
/*  96 */       throw DataUtils.convertToIOException(exception);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static long copyAndClose(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
/*     */     try {
/* 112 */       long l = copyAndCloseInput(paramInputStream, paramOutputStream);
/* 113 */       paramOutputStream.close();
/* 114 */       return l;
/* 115 */     } catch (Exception exception) {
/* 116 */       throw DataUtils.convertToIOException(exception);
/*     */     } finally {
/* 118 */       closeSilently(paramOutputStream);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static long copyAndCloseInput(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
/*     */     try {
/* 134 */       return copy(paramInputStream, paramOutputStream);
/* 135 */     } catch (Exception exception) {
/* 136 */       throw DataUtils.convertToIOException(exception);
/*     */     } finally {
/* 138 */       closeSilently(paramInputStream);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static long copy(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
/* 153 */     return copy(paramInputStream, paramOutputStream, Long.MAX_VALUE);
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
/*     */   public static long copy(InputStream paramInputStream, OutputStream paramOutputStream, long paramLong) throws IOException {
/*     */     try {
/* 169 */       long l = 0L;
/* 170 */       int i = (int)Math.min(paramLong, 4096L);
/* 171 */       byte[] arrayOfByte = new byte[i];
/* 172 */       while (paramLong > 0L) {
/* 173 */         i = paramInputStream.read(arrayOfByte, 0, i);
/* 174 */         if (i < 0) {
/*     */           break;
/*     */         }
/* 177 */         if (paramOutputStream != null) {
/* 178 */           paramOutputStream.write(arrayOfByte, 0, i);
/*     */         }
/* 180 */         l += i;
/* 181 */         paramLong -= i;
/* 182 */         i = (int)Math.min(paramLong, 4096L);
/*     */       } 
/* 184 */       return l;
/* 185 */     } catch (Exception exception) {
/* 186 */       throw DataUtils.convertToIOException(exception);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long copyAndCloseInput(Reader paramReader, Writer paramWriter, long paramLong) throws IOException {
/*     */     try {
/* 203 */       long l = 0L;
/* 204 */       int i = (int)Math.min(paramLong, 4096L);
/* 205 */       char[] arrayOfChar = new char[i];
/* 206 */       while (paramLong > 0L) {
/* 207 */         i = paramReader.read(arrayOfChar, 0, i);
/* 208 */         if (i < 0) {
/*     */           break;
/*     */         }
/* 211 */         if (paramWriter != null) {
/* 212 */           paramWriter.write(arrayOfChar, 0, i);
/*     */         }
/* 214 */         l += i;
/* 215 */         paramLong -= i;
/* 216 */         i = (int)Math.min(paramLong, 4096L);
/*     */       } 
/* 218 */       return l;
/* 219 */     } catch (Exception exception) {
/* 220 */       throw DataUtils.convertToIOException(exception);
/*     */     } finally {
/* 222 */       paramReader.close();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] readBytesAndClose(InputStream paramInputStream, int paramInt) throws IOException {
/*     */     try {
/* 238 */       if (paramInt <= 0) {
/* 239 */         paramInt = Integer.MAX_VALUE;
/*     */       }
/* 241 */       int i = Math.min(4096, paramInt);
/* 242 */       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(i);
/* 243 */       copy(paramInputStream, byteArrayOutputStream, paramInt);
/* 244 */       return byteArrayOutputStream.toByteArray();
/* 245 */     } catch (Exception exception) {
/* 246 */       throw DataUtils.convertToIOException(exception);
/*     */     } finally {
/* 248 */       paramInputStream.close();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static String readStringAndClose(Reader paramReader, int paramInt) throws IOException {
/*     */     try {
/* 264 */       if (paramInt <= 0) {
/* 265 */         paramInt = Integer.MAX_VALUE;
/*     */       }
/* 267 */       int i = Math.min(4096, paramInt);
/* 268 */       StringWriter stringWriter = new StringWriter(i);
/* 269 */       copyAndCloseInput(paramReader, stringWriter, paramInt);
/* 270 */       return stringWriter.toString();
/*     */     } finally {
/* 272 */       paramReader.close();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int readFully(InputStream paramInputStream, byte[] paramArrayOfbyte, int paramInt) throws IOException {
/*     */     try {
/* 290 */       int i = 0, j = Math.min(paramInt, paramArrayOfbyte.length);
/* 291 */       while (j > 0) {
/* 292 */         int k = paramInputStream.read(paramArrayOfbyte, i, j);
/* 293 */         if (k < 0) {
/*     */           break;
/*     */         }
/* 296 */         i += k;
/* 297 */         j -= k;
/*     */       } 
/* 299 */       return i;
/* 300 */     } catch (Exception exception) {
/* 301 */       throw DataUtils.convertToIOException(exception);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int readFully(Reader paramReader, char[] paramArrayOfchar, int paramInt) throws IOException {
/*     */     try {
/* 319 */       int i = 0, j = Math.min(paramInt, paramArrayOfchar.length);
/* 320 */       while (j > 0) {
/* 321 */         int k = paramReader.read(paramArrayOfchar, i, j);
/* 322 */         if (k < 0) {
/*     */           break;
/*     */         }
/* 325 */         i += k;
/* 326 */         j -= k;
/*     */       } 
/* 328 */       return i;
/* 329 */     } catch (Exception exception) {
/* 330 */       throw DataUtils.convertToIOException(exception);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Reader getReader(InputStream paramInputStream) {
/* 345 */     return (paramInputStream == null) ? null : new BufferedReader(new InputStreamReader(paramInputStream, StandardCharsets.UTF_8));
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
/*     */   public static Writer getBufferedWriter(OutputStream paramOutputStream) {
/* 357 */     return (paramOutputStream == null) ? null : new BufferedWriter(new OutputStreamWriter(paramOutputStream, StandardCharsets.UTF_8));
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
/*     */   public static Reader getAsciiReader(InputStream paramInputStream) {
/* 369 */     return (paramInputStream == null) ? null : new InputStreamReader(paramInputStream, StandardCharsets.US_ASCII);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void trace(String paramString1, String paramString2, Object paramObject) {
/* 380 */     if (SysProperties.TRACE_IO) {
/* 381 */       System.out.println("IOUtils." + paramString1 + ' ' + paramString2 + ' ' + paramObject);
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
/*     */   
/*     */   public static InputStream getInputStreamFromString(String paramString) {
/* 394 */     if (paramString == null) {
/* 395 */       return null;
/*     */     }
/* 397 */     return new ByteArrayInputStream(paramString.getBytes(StandardCharsets.UTF_8));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copyFiles(String paramString1, String paramString2) throws IOException {
/* 408 */     InputStream inputStream = FileUtils.newInputStream(paramString1);
/* 409 */     OutputStream outputStream = FileUtils.newOutputStream(paramString2, false);
/* 410 */     copyAndClose(inputStream, outputStream);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String nameSeparatorsToNative(String paramString) {
/* 420 */     return (File.separatorChar == '/') ? paramString.replace('\\', '/') : paramString.replace('/', '\\');
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\IOUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */