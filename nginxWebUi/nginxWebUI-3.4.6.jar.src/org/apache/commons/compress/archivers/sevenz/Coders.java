/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.SequenceInputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.DeflaterOutputStream;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.InflaterInputStream;
/*     */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;
/*     */ import org.tukaani.xz.ARMOptions;
/*     */ import org.tukaani.xz.ARMThumbOptions;
/*     */ import org.tukaani.xz.FilterOptions;
/*     */ import org.tukaani.xz.FinishableOutputStream;
/*     */ import org.tukaani.xz.FinishableWrapperOutputStream;
/*     */ import org.tukaani.xz.IA64Options;
/*     */ import org.tukaani.xz.PowerPCOptions;
/*     */ import org.tukaani.xz.SPARCOptions;
/*     */ import org.tukaani.xz.X86Options;
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
/*     */ class Coders
/*     */ {
/*  47 */   private static final Map<SevenZMethod, CoderBase> CODER_MAP = new HashMap<SevenZMethod, CoderBase>()
/*     */     {
/*     */       private static final long serialVersionUID = 1664829131806520867L;
/*     */     };
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
/*     */   static CoderBase findByMethod(SevenZMethod method) {
/*  68 */     return CODER_MAP.get(method);
/*     */   }
/*     */ 
/*     */   
/*     */   static InputStream addDecoder(String archiveName, InputStream is, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/*  73 */     CoderBase cb = findByMethod(SevenZMethod.byId(coder.decompressionMethodId));
/*  74 */     if (cb == null) {
/*  75 */       throw new IOException("Unsupported compression method " + 
/*  76 */           Arrays.toString(coder.decompressionMethodId) + " used in " + archiveName);
/*     */     }
/*     */     
/*  79 */     return cb.decode(archiveName, is, uncompressedLength, coder, password, maxMemoryLimitInKb);
/*     */   }
/*     */ 
/*     */   
/*     */   static OutputStream addEncoder(OutputStream out, SevenZMethod method, Object options) throws IOException {
/*  84 */     CoderBase cb = findByMethod(method);
/*  85 */     if (cb == null) {
/*  86 */       throw new IOException("Unsupported compression method " + method);
/*     */     }
/*  88 */     return cb.encode(out, options);
/*     */   }
/*     */   static class CopyDecoder extends CoderBase { CopyDecoder() {
/*  91 */       super(new Class[0]);
/*     */     }
/*     */     
/*     */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/*  95 */       return in;
/*     */     }
/*     */     
/*     */     OutputStream encode(OutputStream out, Object options) {
/*  99 */       return out;
/*     */     } }
/*     */   
/*     */   static class BCJDecoder extends CoderBase { private final FilterOptions opts;
/*     */     
/*     */     BCJDecoder(FilterOptions opts) {
/* 105 */       super(new Class[0]);
/* 106 */       this.opts = opts;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/*     */       try {
/* 113 */         return this.opts.getInputStream(in);
/* 114 */       } catch (AssertionError e) {
/* 115 */         throw new IOException("BCJ filter used in " + archiveName + " needs XZ for Java > 1.4 - see https://commons.apache.org/proper/commons-compress/limitations.html#7Z", e);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     OutputStream encode(OutputStream out, Object options) {
/* 125 */       return (OutputStream)new FlushShieldFilterOutputStream((OutputStream)this.opts.getOutputStream((FinishableOutputStream)new FinishableWrapperOutputStream(out)));
/*     */     } }
/*     */ 
/*     */   
/*     */   static class DeflateDecoder extends CoderBase {
/* 130 */     private static final byte[] ONE_ZERO_BYTE = new byte[1];
/*     */     DeflateDecoder() {
/* 132 */       super(new Class[] { Number.class });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/* 140 */       Inflater inflater = new Inflater(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 146 */       InflaterInputStream inflaterInputStream = new InflaterInputStream(new SequenceInputStream(in, new ByteArrayInputStream(ONE_ZERO_BYTE)), inflater);
/*     */       
/* 148 */       return new DeflateDecoderInputStream(inflaterInputStream, inflater);
/*     */     }
/*     */     
/*     */     OutputStream encode(OutputStream out, Object options) {
/* 152 */       int level = numberOptionOrDefault(options, 9);
/* 153 */       Deflater deflater = new Deflater(level, true);
/* 154 */       DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(out, deflater);
/* 155 */       return new DeflateDecoderOutputStream(deflaterOutputStream, deflater);
/*     */     }
/*     */     
/*     */     static class DeflateDecoderInputStream
/*     */       extends InputStream
/*     */     {
/*     */       final InflaterInputStream inflaterInputStream;
/*     */       Inflater inflater;
/*     */       
/*     */       public DeflateDecoderInputStream(InflaterInputStream inflaterInputStream, Inflater inflater) {
/* 165 */         this.inflaterInputStream = inflaterInputStream;
/* 166 */         this.inflater = inflater;
/*     */       }
/*     */ 
/*     */       
/*     */       public int read() throws IOException {
/* 171 */         return this.inflaterInputStream.read();
/*     */       }
/*     */ 
/*     */       
/*     */       public int read(byte[] b, int off, int len) throws IOException {
/* 176 */         return this.inflaterInputStream.read(b, off, len);
/*     */       }
/*     */ 
/*     */       
/*     */       public int read(byte[] b) throws IOException {
/* 181 */         return this.inflaterInputStream.read(b);
/*     */       }
/*     */ 
/*     */       
/*     */       public void close() throws IOException {
/*     */         try {
/* 187 */           this.inflaterInputStream.close();
/*     */         } finally {
/* 189 */           this.inflater.end();
/*     */         } 
/*     */       }
/*     */     }
/*     */     
/*     */     static class DeflateDecoderOutputStream
/*     */       extends OutputStream
/*     */     {
/*     */       final DeflaterOutputStream deflaterOutputStream;
/*     */       Deflater deflater;
/*     */       
/*     */       public DeflateDecoderOutputStream(DeflaterOutputStream deflaterOutputStream, Deflater deflater) {
/* 201 */         this.deflaterOutputStream = deflaterOutputStream;
/* 202 */         this.deflater = deflater;
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(int b) throws IOException {
/* 207 */         this.deflaterOutputStream.write(b);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(byte[] b) throws IOException {
/* 212 */         this.deflaterOutputStream.write(b);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(byte[] b, int off, int len) throws IOException {
/* 217 */         this.deflaterOutputStream.write(b, off, len);
/*     */       }
/*     */ 
/*     */       
/*     */       public void close() throws IOException {
/*     */         try {
/* 223 */           this.deflaterOutputStream.close();
/*     */         } finally {
/* 225 */           this.deflater.end();
/*     */         } 
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static class Deflate64Decoder extends CoderBase {
/*     */     Deflate64Decoder() {
/* 233 */       super(new Class[] { Number.class });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/* 241 */       return (InputStream)new Deflate64CompressorInputStream(in);
/*     */     }
/*     */   }
/*     */   
/*     */   static class BZIP2Decoder extends CoderBase {
/*     */     BZIP2Decoder() {
/* 247 */       super(new Class[] { Number.class });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/* 254 */       return (InputStream)new BZip2CompressorInputStream(in);
/*     */     }
/*     */ 
/*     */     
/*     */     OutputStream encode(OutputStream out, Object options) throws IOException {
/* 259 */       int blockSize = numberOptionOrDefault(options, 9);
/* 260 */       return (OutputStream)new BZip2CompressorOutputStream(out, blockSize);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\Coders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */