package org.apache.commons.compress.archivers.zip;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;

public class ParallelScatterZipCreator {
   private final Deque<ScatterZipOutputStream> streams;
   private final ExecutorService es;
   private final ScatterGatherBackingStoreSupplier backingStoreSupplier;
   private final Deque<Future<? extends ScatterZipOutputStream>> futures;
   private final long startedAt;
   private long compressionDoneAt;
   private long scatterDoneAt;
   private final int compressionLevel;
   private final ThreadLocal<ScatterZipOutputStream> tlScatterStreams;

   private ScatterZipOutputStream createDeferred(ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier) throws IOException {
      ScatterGatherBackingStore bs = scatterGatherBackingStoreSupplier.get();
      StreamCompressor sc = StreamCompressor.create(this.compressionLevel, bs);
      return new ScatterZipOutputStream(bs, sc);
   }

   public ParallelScatterZipCreator() {
      this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
   }

   public ParallelScatterZipCreator(ExecutorService executorService) {
      this(executorService, new DefaultBackingStoreSupplier());
   }

   public ParallelScatterZipCreator(ExecutorService executorService, ScatterGatherBackingStoreSupplier backingStoreSupplier) {
      this(executorService, backingStoreSupplier, -1);
   }

   public ParallelScatterZipCreator(ExecutorService executorService, ScatterGatherBackingStoreSupplier backingStoreSupplier, int compressionLevel) throws IllegalArgumentException {
      this.streams = new ConcurrentLinkedDeque();
      this.futures = new ConcurrentLinkedDeque();
      this.startedAt = System.currentTimeMillis();
      this.tlScatterStreams = new ThreadLocal<ScatterZipOutputStream>() {
         protected ScatterZipOutputStream initialValue() {
            try {
               ScatterZipOutputStream scatterStream = ParallelScatterZipCreator.this.createDeferred(ParallelScatterZipCreator.this.backingStoreSupplier);
               ParallelScatterZipCreator.this.streams.add(scatterStream);
               return scatterStream;
            } catch (IOException var2) {
               throw new RuntimeException(var2);
            }
         }
      };
      if ((compressionLevel < 0 || compressionLevel > 9) && compressionLevel != -1) {
         throw new IllegalArgumentException("Compression level is expected between -1~9");
      } else {
         this.backingStoreSupplier = backingStoreSupplier;
         this.es = executorService;
         this.compressionLevel = compressionLevel;
      }
   }

   public void addArchiveEntry(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source) {
      this.submitStreamAwareCallable(this.createCallable(zipArchiveEntry, source));
   }

   public void addArchiveEntry(ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
      this.submitStreamAwareCallable(this.createCallable(zipArchiveEntryRequestSupplier));
   }

   public final void submit(Callable<? extends Object> callable) {
      this.submitStreamAwareCallable(() -> {
         callable.call();
         return (ScatterZipOutputStream)this.tlScatterStreams.get();
      });
   }

   public final void submitStreamAwareCallable(Callable<? extends ScatterZipOutputStream> callable) {
      this.futures.add(this.es.submit(callable));
   }

   public final Callable<ScatterZipOutputStream> createCallable(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source) {
      int method = zipArchiveEntry.getMethod();
      if (method == -1) {
         throw new IllegalArgumentException("Method must be set on zipArchiveEntry: " + zipArchiveEntry);
      } else {
         ZipArchiveEntryRequest zipArchiveEntryRequest = ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, source);
         return () -> {
            ScatterZipOutputStream scatterStream = (ScatterZipOutputStream)this.tlScatterStreams.get();
            scatterStream.addArchiveEntry(zipArchiveEntryRequest);
            return scatterStream;
         };
      }
   }

   public final Callable<ScatterZipOutputStream> createCallable(ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
      return () -> {
         ScatterZipOutputStream scatterStream = (ScatterZipOutputStream)this.tlScatterStreams.get();
         scatterStream.addArchiveEntry(zipArchiveEntryRequestSupplier.get());
         return scatterStream;
      };
   }

   public void writeTo(ZipArchiveOutputStream targetStream) throws IOException, InterruptedException, ExecutionException {
      try {
         Iterator var2;
         Future future;
         try {
            var2 = this.futures.iterator();

            while(var2.hasNext()) {
               future = (Future)var2.next();
               future.get();
            }
         } finally {
            this.es.shutdown();
         }

         this.es.awaitTermination(60000L, TimeUnit.SECONDS);
         this.compressionDoneAt = System.currentTimeMillis();
         var2 = this.futures.iterator();

         while(var2.hasNext()) {
            future = (Future)var2.next();
            ScatterZipOutputStream scatterStream = (ScatterZipOutputStream)future.get();
            scatterStream.zipEntryWriter().writeNextZipEntry(targetStream);
         }

         var2 = this.streams.iterator();

         while(var2.hasNext()) {
            ScatterZipOutputStream scatterStream = (ScatterZipOutputStream)var2.next();
            scatterStream.close();
         }

         this.scatterDoneAt = System.currentTimeMillis();
      } finally {
         this.closeAll();
      }
   }

   public ScatterStatistics getStatisticsMessage() {
      return new ScatterStatistics(this.compressionDoneAt - this.startedAt, this.scatterDoneAt - this.compressionDoneAt);
   }

   private void closeAll() {
      Iterator var1 = this.streams.iterator();

      while(var1.hasNext()) {
         ScatterZipOutputStream scatterStream = (ScatterZipOutputStream)var1.next();

         try {
            scatterStream.close();
         } catch (IOException var4) {
         }
      }

   }

   private static class DefaultBackingStoreSupplier implements ScatterGatherBackingStoreSupplier {
      final AtomicInteger storeNum;

      private DefaultBackingStoreSupplier() {
         this.storeNum = new AtomicInteger(0);
      }

      public ScatterGatherBackingStore get() throws IOException {
         File tempFile = File.createTempFile("parallelscatter", "n" + this.storeNum.incrementAndGet());
         return new FileBasedScatterGatherBackingStore(tempFile);
      }

      // $FF: synthetic method
      DefaultBackingStoreSupplier(Object x0) {
         this();
      }
   }
}
