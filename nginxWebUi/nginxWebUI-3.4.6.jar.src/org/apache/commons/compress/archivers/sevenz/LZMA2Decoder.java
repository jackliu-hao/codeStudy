/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.commons.compress.MemoryLimitException;
/*     */ import org.tukaani.xz.FinishableOutputStream;
/*     */ import org.tukaani.xz.FinishableWrapperOutputStream;
/*     */ import org.tukaani.xz.LZMA2InputStream;
/*     */ import org.tukaani.xz.LZMA2Options;
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
/*     */ class LZMA2Decoder
/*     */   extends CoderBase
/*     */ {
/*     */   LZMA2Decoder() {
/*  32 */     super(new Class[] { LZMA2Options.class, Number.class });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/*     */     try {
/*  39 */       int dictionarySize = getDictionarySize(coder);
/*  40 */       int memoryUsageInKb = LZMA2InputStream.getMemoryUsage(dictionarySize);
/*  41 */       if (memoryUsageInKb > maxMemoryLimitInKb) {
/*  42 */         throw new MemoryLimitException(memoryUsageInKb, maxMemoryLimitInKb);
/*     */       }
/*  44 */       return (InputStream)new LZMA2InputStream(in, dictionarySize);
/*  45 */     } catch (IllegalArgumentException ex) {
/*  46 */       throw new IOException(ex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   OutputStream encode(OutputStream out, Object opts) throws IOException {
/*  53 */     LZMA2Options options = getOptions(opts);
/*  54 */     FinishableWrapperOutputStream finishableWrapperOutputStream = new FinishableWrapperOutputStream(out);
/*  55 */     return (OutputStream)options.getOutputStream((FinishableOutputStream)finishableWrapperOutputStream);
/*     */   }
/*     */ 
/*     */   
/*     */   byte[] getOptionsAsProperties(Object opts) {
/*  60 */     int dictSize = getDictSize(opts);
/*  61 */     int lead = Integer.numberOfLeadingZeros(dictSize);
/*  62 */     int secondBit = (dictSize >>> 30 - lead) - 2;
/*  63 */     return new byte[] { (byte)((19 - lead) * 2 + secondBit) };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object getOptionsFromCoder(Coder coder, InputStream in) throws IOException {
/*  71 */     return Integer.valueOf(getDictionarySize(coder));
/*     */   }
/*     */   
/*     */   private int getDictSize(Object opts) {
/*  75 */     if (opts instanceof LZMA2Options) {
/*  76 */       return ((LZMA2Options)opts).getDictSize();
/*     */     }
/*  78 */     return numberOptionOrDefault(opts);
/*     */   }
/*     */   
/*     */   private int getDictionarySize(Coder coder) throws IOException {
/*  82 */     if (coder.properties == null) {
/*  83 */       throw new IOException("Missing LZMA2 properties");
/*     */     }
/*  85 */     if (coder.properties.length < 1) {
/*  86 */       throw new IOException("LZMA2 properties too short");
/*     */     }
/*  88 */     int dictionarySizeBits = 0xFF & coder.properties[0];
/*  89 */     if ((dictionarySizeBits & 0xFFFFFFC0) != 0) {
/*  90 */       throw new IOException("Unsupported LZMA2 property bits");
/*     */     }
/*  92 */     if (dictionarySizeBits > 40) {
/*  93 */       throw new IOException("Dictionary larger than 4GiB maximum size");
/*     */     }
/*  95 */     if (dictionarySizeBits == 40) {
/*  96 */       return -1;
/*     */     }
/*  98 */     return (0x2 | dictionarySizeBits & 0x1) << dictionarySizeBits / 2 + 11;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\LZMA2Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */