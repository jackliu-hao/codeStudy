/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.commons.compress.MemoryLimitException;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
/*     */ import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;
/*     */ import org.tukaani.xz.LZMA2Options;
/*     */ import org.tukaani.xz.LZMAInputStream;
/*     */ import org.tukaani.xz.LZMAOutputStream;
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
/*     */ class LZMADecoder
/*     */   extends CoderBase
/*     */ {
/*     */   LZMADecoder() {
/*  33 */     super(new Class[] { LZMA2Options.class, Number.class });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/*  39 */     if (coder.properties == null) {
/*  40 */       throw new IOException("Missing LZMA properties");
/*     */     }
/*  42 */     if (coder.properties.length < 1) {
/*  43 */       throw new IOException("LZMA properties too short");
/*     */     }
/*  45 */     byte propsByte = coder.properties[0];
/*  46 */     int dictSize = getDictionarySize(coder);
/*  47 */     if (dictSize > 2147483632) {
/*  48 */       throw new IOException("Dictionary larger than 4GiB maximum size used in " + archiveName);
/*     */     }
/*  50 */     int memoryUsageInKb = LZMAInputStream.getMemoryUsage(dictSize, propsByte);
/*  51 */     if (memoryUsageInKb > maxMemoryLimitInKb) {
/*  52 */       throw new MemoryLimitException(memoryUsageInKb, maxMemoryLimitInKb);
/*     */     }
/*  54 */     return (InputStream)new LZMAInputStream(in, uncompressedLength, propsByte, dictSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   OutputStream encode(OutputStream out, Object opts) throws IOException {
/*  62 */     return (OutputStream)new FlushShieldFilterOutputStream((OutputStream)new LZMAOutputStream(out, getOptions(opts), false));
/*     */   }
/*     */ 
/*     */   
/*     */   byte[] getOptionsAsProperties(Object opts) throws IOException {
/*  67 */     LZMA2Options options = getOptions(opts);
/*  68 */     byte props = (byte)((options.getPb() * 5 + options.getLp()) * 9 + options.getLc());
/*  69 */     int dictSize = options.getDictSize();
/*  70 */     byte[] o = new byte[5];
/*  71 */     o[0] = props;
/*  72 */     ByteUtils.toLittleEndian(o, dictSize, 1, 4);
/*  73 */     return o;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getOptionsFromCoder(Coder coder, InputStream in) throws IOException {
/*  78 */     if (coder.properties == null) {
/*  79 */       throw new IOException("Missing LZMA properties");
/*     */     }
/*  81 */     if (coder.properties.length < 1) {
/*  82 */       throw new IOException("LZMA properties too short");
/*     */     }
/*  84 */     byte propsByte = coder.properties[0];
/*  85 */     int props = propsByte & 0xFF;
/*  86 */     int pb = props / 45;
/*  87 */     props -= pb * 9 * 5;
/*  88 */     int lp = props / 9;
/*  89 */     int lc = props - lp * 9;
/*  90 */     LZMA2Options opts = new LZMA2Options();
/*  91 */     opts.setPb(pb);
/*  92 */     opts.setLcLp(lc, lp);
/*  93 */     opts.setDictSize(getDictionarySize(coder));
/*  94 */     return opts;
/*     */   }
/*     */   
/*     */   private int getDictionarySize(Coder coder) throws IllegalArgumentException {
/*  98 */     return (int)ByteUtils.fromLittleEndian(coder.properties, 1, 4);
/*     */   }
/*     */   
/*     */   private LZMA2Options getOptions(Object opts) throws IOException {
/* 102 */     if (opts instanceof LZMA2Options) {
/* 103 */       return (LZMA2Options)opts;
/*     */     }
/* 105 */     LZMA2Options options = new LZMA2Options();
/* 106 */     options.setDictSize(numberOptionOrDefault(opts));
/* 107 */     return options;
/*     */   }
/*     */   
/*     */   private int numberOptionOrDefault(Object opts) {
/* 111 */     return numberOptionOrDefault(opts, 8388608);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\LZMADecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */