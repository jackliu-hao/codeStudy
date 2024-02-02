/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Deque;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ConcurrentLinkedDeque;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
/*     */ import org.apache.commons.compress.parallel.InputStreamSupplier;
/*     */ import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
/*     */ import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;
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
/*     */ public class ParallelScatterZipCreator
/*     */ {
/*  54 */   private final Deque<ScatterZipOutputStream> streams = new ConcurrentLinkedDeque<>();
/*     */   private final ExecutorService es;
/*     */   private final ScatterGatherBackingStoreSupplier backingStoreSupplier;
/*  57 */   private final Deque<Future<? extends ScatterZipOutputStream>> futures = new ConcurrentLinkedDeque<>();
/*     */   
/*  59 */   private final long startedAt = System.currentTimeMillis();
/*     */   private long compressionDoneAt;
/*     */   private long scatterDoneAt;
/*     */   private final int compressionLevel;
/*     */   
/*     */   private static class DefaultBackingStoreSupplier implements ScatterGatherBackingStoreSupplier {
/*  65 */     final AtomicInteger storeNum = new AtomicInteger(0);
/*     */ 
/*     */     
/*     */     public ScatterGatherBackingStore get() throws IOException {
/*  69 */       File tempFile = File.createTempFile("parallelscatter", "n" + this.storeNum.incrementAndGet());
/*  70 */       return (ScatterGatherBackingStore)new FileBasedScatterGatherBackingStore(tempFile);
/*     */     }
/*     */     
/*     */     private DefaultBackingStoreSupplier() {} }
/*     */   
/*     */   private ScatterZipOutputStream createDeferred(ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier) throws IOException {
/*  76 */     ScatterGatherBackingStore bs = scatterGatherBackingStoreSupplier.get();
/*     */     
/*  78 */     StreamCompressor sc = StreamCompressor.create(this.compressionLevel, bs);
/*  79 */     return new ScatterZipOutputStream(bs, sc);
/*     */   }
/*     */   
/*  82 */   private final ThreadLocal<ScatterZipOutputStream> tlScatterStreams = new ThreadLocal<ScatterZipOutputStream>()
/*     */     {
/*     */       protected ScatterZipOutputStream initialValue() {
/*     */         try {
/*  86 */           ScatterZipOutputStream scatterStream = ParallelScatterZipCreator.this.createDeferred(ParallelScatterZipCreator.this.backingStoreSupplier);
/*  87 */           ParallelScatterZipCreator.this.streams.add(scatterStream);
/*  88 */           return scatterStream;
/*  89 */         } catch (IOException e) {
/*  90 */           throw new RuntimeException(e);
/*     */         } 
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParallelScatterZipCreator() {
/* 100 */     this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParallelScatterZipCreator(ExecutorService executorService) {
/* 110 */     this(executorService, new DefaultBackingStoreSupplier(null));
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
/*     */   public ParallelScatterZipCreator(ExecutorService executorService, ScatterGatherBackingStoreSupplier backingStoreSupplier) {
/* 122 */     this(executorService, backingStoreSupplier, -1);
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
/*     */   public ParallelScatterZipCreator(ExecutorService executorService, ScatterGatherBackingStoreSupplier backingStoreSupplier, int compressionLevel) throws IllegalArgumentException {
/* 139 */     if ((compressionLevel < 0 || compressionLevel > 9) && compressionLevel != -1)
/*     */     {
/* 141 */       throw new IllegalArgumentException("Compression level is expected between -1~9");
/*     */     }
/*     */     
/* 144 */     this.backingStoreSupplier = backingStoreSupplier;
/* 145 */     this.es = executorService;
/* 146 */     this.compressionLevel = compressionLevel;
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
/*     */   public void addArchiveEntry(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source) {
/* 160 */     submitStreamAwareCallable(createCallable(zipArchiveEntry, source));
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
/*     */   public void addArchiveEntry(ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
/* 173 */     submitStreamAwareCallable(createCallable(zipArchiveEntryRequestSupplier));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void submit(Callable<? extends Object> callable) {
/* 184 */     submitStreamAwareCallable(() -> {
/*     */           callable.call();
/*     */           return this.tlScatterStreams.get();
/*     */         });
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
/*     */   public final void submitStreamAwareCallable(Callable<? extends ScatterZipOutputStream> callable) {
/* 199 */     this.futures.add(this.es.submit(callable));
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
/*     */   
/*     */   public final Callable<ScatterZipOutputStream> createCallable(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source) {
/* 222 */     int method = zipArchiveEntry.getMethod();
/* 223 */     if (method == -1) {
/* 224 */       throw new IllegalArgumentException("Method must be set on zipArchiveEntry: " + zipArchiveEntry);
/*     */     }
/* 226 */     ZipArchiveEntryRequest zipArchiveEntryRequest = ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, source);
/* 227 */     return () -> {
/*     */         ScatterZipOutputStream scatterStream = this.tlScatterStreams.get();
/*     */         scatterStream.addArchiveEntry(zipArchiveEntryRequest);
/*     */         return scatterStream;
/*     */       };
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
/*     */   public final Callable<ScatterZipOutputStream> createCallable(ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
/* 251 */     return () -> {
/*     */         ScatterZipOutputStream scatterStream = this.tlScatterStreams.get();
/*     */         scatterStream.addArchiveEntry(zipArchiveEntryRequestSupplier.get());
/*     */         return scatterStream;
/*     */       };
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
/*     */   
/*     */   public void writeTo(ZipArchiveOutputStream targetStream) throws IOException, InterruptedException, ExecutionException {
/*     */     try {
/*     */       try {
/* 280 */         for (Future<?> future : this.futures) {
/* 281 */           future.get();
/*     */         }
/*     */       } finally {
/* 284 */         this.es.shutdown();
/*     */       } 
/*     */       
/* 287 */       this.es.awaitTermination(60000L, TimeUnit.SECONDS);
/*     */ 
/*     */       
/* 290 */       this.compressionDoneAt = System.currentTimeMillis();
/*     */       
/* 292 */       for (Future<? extends ScatterZipOutputStream> future : this.futures) {
/* 293 */         ScatterZipOutputStream scatterStream = future.get();
/* 294 */         scatterStream.zipEntryWriter().writeNextZipEntry(targetStream);
/*     */       } 
/*     */       
/* 297 */       for (ScatterZipOutputStream scatterStream : this.streams) {
/* 298 */         scatterStream.close();
/*     */       }
/*     */       
/* 301 */       this.scatterDoneAt = System.currentTimeMillis();
/*     */     } finally {
/* 303 */       closeAll();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ScatterStatistics getStatisticsMessage() {
/* 313 */     return new ScatterStatistics(this.compressionDoneAt - this.startedAt, this.scatterDoneAt - this.compressionDoneAt);
/*     */   }
/*     */   
/*     */   private void closeAll() {
/* 317 */     for (ScatterZipOutputStream scatterStream : this.streams) {
/*     */       try {
/* 319 */         scatterStream.close();
/* 320 */       } catch (IOException iOException) {}
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ParallelScatterZipCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */