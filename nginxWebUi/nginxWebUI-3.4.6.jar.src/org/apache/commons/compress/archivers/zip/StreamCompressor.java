/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.Deflater;
/*     */ import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class StreamCompressor
/*     */   implements Closeable
/*     */ {
/*     */   private static final int DEFLATER_BLOCK_SIZE = 8192;
/*     */   private final Deflater def;
/*  53 */   private final CRC32 crc = new CRC32();
/*     */   
/*     */   private long writtenToOutputStreamForLastEntry;
/*     */   
/*     */   private long sourcePayloadLength;
/*     */   private long totalWrittenToOutputStream;
/*     */   private static final int BUFFER_SIZE = 4096;
/*  60 */   private final byte[] outputBuffer = new byte[4096];
/*  61 */   private final byte[] readerBuf = new byte[4096];
/*     */   
/*     */   StreamCompressor(Deflater deflater) {
/*  64 */     this.def = deflater;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static StreamCompressor create(OutputStream os, Deflater deflater) {
/*  75 */     return new OutputStreamCompressor(deflater, os);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static StreamCompressor create(OutputStream os) {
/*  85 */     return create(os, new Deflater(-1, true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static StreamCompressor create(DataOutput os, Deflater deflater) {
/*  96 */     return new DataOutputCompressor(deflater, os);
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
/*     */   static StreamCompressor create(SeekableByteChannel os, Deflater deflater) {
/* 108 */     return new SeekableByteChannelCompressor(deflater, os);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StreamCompressor create(int compressionLevel, ScatterGatherBackingStore bs) {
/* 119 */     Deflater deflater = new Deflater(compressionLevel, true);
/* 120 */     return new ScatterGatherBackingStoreCompressor(deflater, bs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StreamCompressor create(ScatterGatherBackingStore bs) {
/* 130 */     return create(-1, bs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCrc32() {
/* 140 */     return this.crc.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 149 */     return this.sourcePayloadLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getBytesWrittenForLastEntry() {
/* 158 */     return this.writtenToOutputStreamForLastEntry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalBytesWritten() {
/* 167 */     return this.totalWrittenToOutputStream;
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
/*     */   public void deflate(InputStream source, int method) throws IOException {
/* 180 */     reset();
/*     */     
/*     */     int length;
/* 183 */     while ((length = source.read(this.readerBuf, 0, this.readerBuf.length)) >= 0) {
/* 184 */       write(this.readerBuf, 0, length, method);
/*     */     }
/* 186 */     if (method == 8) {
/* 187 */       flushDeflater();
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
/*     */   long write(byte[] b, int offset, int length, int method) throws IOException {
/* 202 */     long current = this.writtenToOutputStreamForLastEntry;
/* 203 */     this.crc.update(b, offset, length);
/* 204 */     if (method == 8) {
/* 205 */       writeDeflated(b, offset, length);
/*     */     } else {
/* 207 */       writeCounted(b, offset, length);
/*     */     } 
/* 209 */     this.sourcePayloadLength += length;
/* 210 */     return this.writtenToOutputStreamForLastEntry - current;
/*     */   }
/*     */ 
/*     */   
/*     */   void reset() {
/* 215 */     this.crc.reset();
/* 216 */     this.def.reset();
/* 217 */     this.sourcePayloadLength = 0L;
/* 218 */     this.writtenToOutputStreamForLastEntry = 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 223 */     this.def.end();
/*     */   }
/*     */   
/*     */   void flushDeflater() throws IOException {
/* 227 */     this.def.finish();
/* 228 */     while (!this.def.finished()) {
/* 229 */       deflate();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeDeflated(byte[] b, int offset, int length) throws IOException {
/* 235 */     if (length > 0 && !this.def.finished()) {
/* 236 */       if (length <= 8192) {
/* 237 */         this.def.setInput(b, offset, length);
/* 238 */         deflateUntilInputIsNeeded();
/*     */       } else {
/* 240 */         int fullblocks = length / 8192;
/* 241 */         for (int i = 0; i < fullblocks; i++) {
/* 242 */           this.def.setInput(b, offset + i * 8192, 8192);
/*     */           
/* 244 */           deflateUntilInputIsNeeded();
/*     */         } 
/* 246 */         int done = fullblocks * 8192;
/* 247 */         if (done < length) {
/* 248 */           this.def.setInput(b, offset + done, length - done);
/* 249 */           deflateUntilInputIsNeeded();
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void deflateUntilInputIsNeeded() throws IOException {
/* 256 */     while (!this.def.needsInput()) {
/* 257 */       deflate();
/*     */     }
/*     */   }
/*     */   
/*     */   void deflate() throws IOException {
/* 262 */     int len = this.def.deflate(this.outputBuffer, 0, this.outputBuffer.length);
/* 263 */     if (len > 0) {
/* 264 */       writeCounted(this.outputBuffer, 0, len);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeCounted(byte[] data) throws IOException {
/* 269 */     writeCounted(data, 0, data.length);
/*     */   }
/*     */   
/*     */   public void writeCounted(byte[] data, int offset, int length) throws IOException {
/* 273 */     writeOut(data, offset, length);
/* 274 */     this.writtenToOutputStreamForLastEntry += length;
/* 275 */     this.totalWrittenToOutputStream += length;
/*     */   }
/*     */   
/*     */   protected abstract void writeOut(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
/*     */   
/*     */   private static final class ScatterGatherBackingStoreCompressor extends StreamCompressor {
/*     */     private final ScatterGatherBackingStore bs;
/*     */     
/*     */     public ScatterGatherBackingStoreCompressor(Deflater deflater, ScatterGatherBackingStore bs) {
/* 284 */       super(deflater);
/* 285 */       this.bs = bs;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void writeOut(byte[] data, int offset, int length) throws IOException {
/* 291 */       this.bs.writeOut(data, offset, length);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class OutputStreamCompressor extends StreamCompressor {
/*     */     private final OutputStream os;
/*     */     
/*     */     public OutputStreamCompressor(Deflater deflater, OutputStream os) {
/* 299 */       super(deflater);
/* 300 */       this.os = os;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void writeOut(byte[] data, int offset, int length) throws IOException {
/* 306 */       this.os.write(data, offset, length);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class DataOutputCompressor extends StreamCompressor {
/*     */     private final DataOutput raf;
/*     */     
/*     */     public DataOutputCompressor(Deflater deflater, DataOutput raf) {
/* 314 */       super(deflater);
/* 315 */       this.raf = raf;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void writeOut(byte[] data, int offset, int length) throws IOException {
/* 321 */       this.raf.write(data, offset, length);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class SeekableByteChannelCompressor
/*     */     extends StreamCompressor {
/*     */     private final SeekableByteChannel channel;
/*     */     
/*     */     public SeekableByteChannelCompressor(Deflater deflater, SeekableByteChannel channel) {
/* 330 */       super(deflater);
/* 331 */       this.channel = channel;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void writeOut(byte[] data, int offset, int length) throws IOException {
/* 337 */       this.channel.write(ByteBuffer.wrap(data, offset, length));
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\StreamCompressor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */