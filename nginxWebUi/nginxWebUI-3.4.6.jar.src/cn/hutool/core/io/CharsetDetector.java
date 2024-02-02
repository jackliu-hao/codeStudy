/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.CharacterCodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
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
/*     */ public class CharsetDetector
/*     */ {
/*     */   private static final Charset[] DEFAULT_CHARSETS;
/*     */   
/*     */   static {
/*  28 */     String[] names = { "UTF-8", "GBK", "GB2312", "GB18030", "UTF-16BE", "UTF-16LE", "UTF-16", "BIG5", "UNICODE", "US-ASCII" };
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
/*  39 */     DEFAULT_CHARSETS = (Charset[])Convert.convert(Charset[].class, names);
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
/*     */   public static Charset detect(File file, Charset... charsets) {
/*  51 */     return detect(FileUtil.getInputStream(file), charsets);
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
/*     */   public static Charset detect(InputStream in, Charset... charsets) {
/*  63 */     return detect(8192, in, charsets);
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
/*     */   public static Charset detect(int bufferSize, InputStream in, Charset... charsets) {
/*  77 */     if (ArrayUtil.isEmpty((Object[])charsets)) {
/*  78 */       charsets = DEFAULT_CHARSETS;
/*     */     }
/*     */     
/*  81 */     byte[] buffer = new byte[bufferSize];
/*     */     try {
/*  83 */       while (in.read(buffer) > -1) {
/*  84 */         for (Charset charset : charsets) {
/*  85 */           CharsetDecoder decoder = charset.newDecoder();
/*  86 */           if (identify(buffer, decoder)) {
/*  87 */             return charset;
/*     */           }
/*     */         } 
/*     */       } 
/*  91 */     } catch (IOException e) {
/*  92 */       throw new IORuntimeException(e);
/*     */     } finally {
/*  94 */       IoUtil.close(in);
/*     */     } 
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
/*     */     try {
/* 108 */       decoder.decode(ByteBuffer.wrap(bytes));
/* 109 */     } catch (CharacterCodingException e) {
/* 110 */       return false;
/*     */     } 
/* 112 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\CharsetDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */