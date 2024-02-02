/*     */ package cn.hutool.extra.compress;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.extra.compress.archiver.Archiver;
/*     */ import cn.hutool.extra.compress.archiver.SevenZArchiver;
/*     */ import cn.hutool.extra.compress.archiver.StreamArchiver;
/*     */ import cn.hutool.extra.compress.extractor.Extractor;
/*     */ import cn.hutool.extra.compress.extractor.SevenZExtractor;
/*     */ import cn.hutool.extra.compress.extractor.StreamExtractor;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.commons.compress.compressors.CompressorException;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorStreamFactory;
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
/*     */ public class CompressUtil
/*     */ {
/*     */   public static CompressorOutputStream getOut(String compressorName, OutputStream out) {
/*     */     try {
/*  52 */       return (new CompressorStreamFactory()).createCompressorOutputStream(compressorName, out);
/*  53 */     } catch (CompressorException e) {
/*  54 */       throw new CompressException(e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CompressorInputStream getIn(String compressorName, InputStream in) {
/*  77 */     in = IoUtil.toMarkSupportStream(in);
/*     */     try {
/*  79 */       if (StrUtil.isBlank(compressorName)) {
/*  80 */         compressorName = CompressorStreamFactory.detect(in);
/*     */       }
/*  82 */       return (new CompressorStreamFactory()).createCompressorInputStream(compressorName, in);
/*  83 */     } catch (CompressorException e) {
/*  84 */       throw new CompressException(e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Archiver createArchiver(Charset charset, String archiverName, File file) {
/* 105 */     if ("7z".equalsIgnoreCase(archiverName)) {
/* 106 */       return (Archiver)new SevenZArchiver(file);
/*     */     }
/* 108 */     return (Archiver)StreamArchiver.create(charset, archiverName, file);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Archiver createArchiver(Charset charset, String archiverName, OutputStream out) {
/* 128 */     if ("7z".equalsIgnoreCase(archiverName)) {
/* 129 */       return (Archiver)new SevenZArchiver(out);
/*     */     }
/* 131 */     return (Archiver)StreamArchiver.create(charset, archiverName, out);
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
/*     */ 
/*     */   
/*     */   public static Extractor createExtractor(Charset charset, File file) {
/* 150 */     return createExtractor(charset, (String)null, file);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Extractor createExtractor(Charset charset, String archiverName, File file) {
/* 170 */     if ("7z".equalsIgnoreCase(archiverName)) {
/* 171 */       return (Extractor)new SevenZExtractor(file);
/*     */     }
/*     */     try {
/* 174 */       return (Extractor)new StreamExtractor(charset, archiverName, file);
/* 175 */     } catch (CompressException e) {
/* 176 */       Throwable cause = e.getCause();
/* 177 */       if (cause instanceof org.apache.commons.compress.archivers.StreamingNotSupportedException && cause.getMessage().contains("7z")) {
/* 178 */         return (Extractor)new SevenZExtractor(file);
/*     */       }
/* 180 */       throw e;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Extractor createExtractor(Charset charset, InputStream in) {
/* 200 */     return createExtractor(charset, (String)null, in);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Extractor createExtractor(Charset charset, String archiverName, InputStream in) {
/* 220 */     if ("7z".equalsIgnoreCase(archiverName)) {
/* 221 */       return (Extractor)new SevenZExtractor(in);
/*     */     }
/*     */     
/*     */     try {
/* 225 */       return (Extractor)new StreamExtractor(charset, archiverName, in);
/* 226 */     } catch (CompressException e) {
/* 227 */       Throwable cause = e.getCause();
/* 228 */       if (cause instanceof org.apache.commons.compress.archivers.StreamingNotSupportedException && cause.getMessage().contains("7z")) {
/* 229 */         return (Extractor)new SevenZExtractor(in);
/*     */       }
/* 231 */       throw e;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\compress\CompressUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */