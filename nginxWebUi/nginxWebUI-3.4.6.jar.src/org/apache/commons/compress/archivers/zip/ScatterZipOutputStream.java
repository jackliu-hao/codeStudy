/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
/*     */ import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
/*     */ import org.apache.commons.compress.utils.BoundedInputStream;
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
/*     */ public class ScatterZipOutputStream
/*     */   implements Closeable
/*     */ {
/*  51 */   private final Queue<CompressedEntry> items = new ConcurrentLinkedQueue<>();
/*     */   private final ScatterGatherBackingStore backingStore;
/*     */   private final StreamCompressor streamCompressor;
/*  54 */   private final AtomicBoolean isClosed = new AtomicBoolean();
/*     */   private ZipEntryWriter zipEntryWriter;
/*     */   
/*     */   private static class CompressedEntry {
/*     */     final ZipArchiveEntryRequest zipArchiveEntryRequest;
/*     */     final long crc;
/*     */     final long compressedSize;
/*     */     final long size;
/*     */     
/*     */     public CompressedEntry(ZipArchiveEntryRequest zipArchiveEntryRequest, long crc, long compressedSize, long size) {
/*  64 */       this.zipArchiveEntryRequest = zipArchiveEntryRequest;
/*  65 */       this.crc = crc;
/*  66 */       this.compressedSize = compressedSize;
/*  67 */       this.size = size;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ZipArchiveEntry transferToArchiveEntry() {
/*  77 */       ZipArchiveEntry entry = this.zipArchiveEntryRequest.getZipArchiveEntry();
/*  78 */       entry.setCompressedSize(this.compressedSize);
/*  79 */       entry.setSize(this.size);
/*  80 */       entry.setCrc(this.crc);
/*  81 */       entry.setMethod(this.zipArchiveEntryRequest.getMethod());
/*  82 */       return entry;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ScatterZipOutputStream(ScatterGatherBackingStore backingStore, StreamCompressor streamCompressor) {
/*  88 */     this.backingStore = backingStore;
/*  89 */     this.streamCompressor = streamCompressor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addArchiveEntry(ZipArchiveEntryRequest zipArchiveEntryRequest) throws IOException {
/*  99 */     try (InputStream payloadStream = zipArchiveEntryRequest.getPayloadStream()) {
/* 100 */       this.streamCompressor.deflate(payloadStream, zipArchiveEntryRequest.getMethod());
/*     */     } 
/* 102 */     this.items.add(new CompressedEntry(zipArchiveEntryRequest, this.streamCompressor.getCrc32(), this.streamCompressor
/* 103 */           .getBytesWrittenForLastEntry(), this.streamCompressor.getBytesRead()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(ZipArchiveOutputStream target) throws IOException {
/* 114 */     this.backingStore.closeForWriting();
/* 115 */     try (InputStream data = this.backingStore.getInputStream()) {
/* 116 */       for (CompressedEntry compressedEntry : this.items) {
/* 117 */         try (BoundedInputStream rawStream = new BoundedInputStream(data, compressedEntry.compressedSize)) {
/*     */           
/* 119 */           target.addRawArchiveEntry(compressedEntry.transferToArchiveEntry(), (InputStream)rawStream);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class ZipEntryWriter implements Closeable {
/*     */     private final Iterator<ScatterZipOutputStream.CompressedEntry> itemsIterator;
/*     */     private final InputStream itemsIteratorData;
/*     */     
/*     */     public ZipEntryWriter(ScatterZipOutputStream scatter) throws IOException {
/* 130 */       scatter.backingStore.closeForWriting();
/* 131 */       this.itemsIterator = scatter.items.iterator();
/* 132 */       this.itemsIteratorData = scatter.backingStore.getInputStream();
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 137 */       if (this.itemsIteratorData != null) {
/* 138 */         this.itemsIteratorData.close();
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeNextZipEntry(ZipArchiveOutputStream target) throws IOException {
/* 143 */       ScatterZipOutputStream.CompressedEntry compressedEntry = this.itemsIterator.next();
/* 144 */       try (BoundedInputStream rawStream = new BoundedInputStream(this.itemsIteratorData, compressedEntry.compressedSize)) {
/* 145 */         target.addRawArchiveEntry(compressedEntry.transferToArchiveEntry(), (InputStream)rawStream);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipEntryWriter zipEntryWriter() throws IOException {
/* 156 */     if (this.zipEntryWriter == null) {
/* 157 */       this.zipEntryWriter = new ZipEntryWriter(this);
/*     */     }
/* 159 */     return this.zipEntryWriter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 168 */     if (!this.isClosed.compareAndSet(false, true)) {
/*     */       return;
/*     */     }
/*     */     try {
/* 172 */       if (this.zipEntryWriter != null) {
/* 173 */         this.zipEntryWriter.close();
/*     */       }
/* 175 */       this.backingStore.close();
/*     */     } finally {
/* 177 */       this.streamCompressor.close();
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
/*     */   public static ScatterZipOutputStream fileBased(File file) throws FileNotFoundException {
/* 189 */     return fileBased(file, -1);
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
/*     */   public static ScatterZipOutputStream fileBased(File file, int compressionLevel) throws FileNotFoundException {
/* 201 */     FileBasedScatterGatherBackingStore fileBasedScatterGatherBackingStore = new FileBasedScatterGatherBackingStore(file);
/*     */     
/* 203 */     StreamCompressor sc = StreamCompressor.create(compressionLevel, (ScatterGatherBackingStore)fileBasedScatterGatherBackingStore);
/* 204 */     return new ScatterZipOutputStream((ScatterGatherBackingStore)fileBasedScatterGatherBackingStore, sc);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ScatterZipOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */