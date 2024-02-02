package io.undertow.server.handlers.encoding;

import io.undertow.UndertowLogger;
import io.undertow.predicate.Predicate;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.CachingResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.util.ImmediateConduitFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.xnio.IoUtils;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.Channels;
import org.xnio.channels.Configurable;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.WriteReadyHandler;

public class ContentEncodedResourceManager {
   private final Path encodedResourcesRoot;
   private final CachingResourceManager encoded;
   private final ContentEncodingRepository contentEncodingRepository;
   private final int minResourceSize;
   private final int maxResourceSize;
   private final Predicate encodingAllowed;
   private final ConcurrentMap<LockKey, Object> fileLocks = new ConcurrentHashMap();

   public ContentEncodedResourceManager(Path encodedResourcesRoot, CachingResourceManager encodedResourceManager, ContentEncodingRepository contentEncodingRepository, int minResourceSize, int maxResourceSize, Predicate encodingAllowed) {
      this.encodedResourcesRoot = encodedResourcesRoot;
      this.encoded = encodedResourceManager;
      this.contentEncodingRepository = contentEncodingRepository;
      this.minResourceSize = minResourceSize;
      this.maxResourceSize = maxResourceSize;
      this.encodingAllowed = encodingAllowed;
   }

   public ContentEncodedResource getResource(Resource resource, HttpServerExchange exchange) throws IOException {
      String path = resource.getPath();
      Path file = resource.getFilePath();
      if (file == null) {
         return null;
      } else if (this.minResourceSize > 0 && resource.getContentLength() < (long)this.minResourceSize || this.maxResourceSize > 0 && resource.getContentLength() > (long)this.maxResourceSize || this.encodingAllowed != null && !this.encodingAllowed.resolve(exchange)) {
         return null;
      } else {
         AllowedContentEncodings encodings = this.contentEncodingRepository.getContentEncodings(exchange);
         if (encodings != null && !encodings.isNoEncodingsAllowed()) {
            EncodingMapping encoding = encodings.getEncoding();
            if (encoding != null && !encoding.getName().equals("identity")) {
               String newPath = path + ".undertow.encoding." + encoding.getName();
               Resource preCompressed = this.encoded.getResource(newPath);
               if (preCompressed != null) {
                  return new ContentEncodedResource(preCompressed, encoding.getName());
               } else {
                  LockKey key = new LockKey(path, encoding.getName());
                  if (this.fileLocks.putIfAbsent(key, this) != null) {
                     return null;
                  } else {
                     FileChannel targetFileChannel = null;
                     FileChannel sourceFileChannel = null;

                     ContentEncodedResource var12;
                     try {
                        preCompressed = this.encoded.getResource(newPath);
                        if (preCompressed == null) {
                           Path finalTarget = this.encodedResourcesRoot.resolve(newPath);
                           Path tempTarget = this.encodedResourcesRoot.resolve(newPath);
                           OutputStream tmp = Files.newOutputStream(tempTarget);

                           try {
                              tmp.close();
                           } finally {
                              IoUtils.safeClose((Closeable)tmp);
                           }

                           targetFileChannel = FileChannel.open(tempTarget, StandardOpenOption.READ, StandardOpenOption.WRITE);
                           sourceFileChannel = FileChannel.open(file, StandardOpenOption.READ);
                           StreamSinkConduit conduit = (StreamSinkConduit)encoding.getEncoding().getResponseWrapper().wrap(new ImmediateConduitFactory(new FileConduitTarget(targetFileChannel, exchange)), exchange);
                           ConduitStreamSinkChannel targetChannel = new ConduitStreamSinkChannel((Configurable)null, conduit);
                           long transferred = sourceFileChannel.transferTo(0L, resource.getContentLength(), targetChannel);
                           targetChannel.shutdownWrites();
                           Channels.flushBlocking(targetChannel);
                           if (transferred != resource.getContentLength()) {
                              UndertowLogger.REQUEST_LOGGER.failedToWritePreCachedFile();
                           }

                           Files.move(tempTarget, finalTarget);
                           this.encoded.invalidate(newPath);
                           Resource encodedResource = this.encoded.getResource(newPath);
                           ContentEncodedResource var20 = new ContentEncodedResource(encodedResource, encoding.getName());
                           return var20;
                        }

                        var12 = new ContentEncodedResource(preCompressed, encoding.getName());
                     } finally {
                        IoUtils.safeClose((Closeable)targetFileChannel);
                        IoUtils.safeClose((Closeable)sourceFileChannel);
                        this.fileLocks.remove(key);
                     }

                     return var12;
                  }
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   private static final class FileConduitTarget implements StreamSinkConduit {
      private final FileChannel fileChannel;
      private final HttpServerExchange exchange;
      private WriteReadyHandler writeReadyHandler;
      private boolean writesResumed;

      private FileConduitTarget(FileChannel fileChannel, HttpServerExchange exchange) {
         this.writesResumed = false;
         this.fileChannel = fileChannel;
         this.exchange = exchange;
      }

      public long transferFrom(FileChannel fileChannel, long l, long l2) throws IOException {
         return this.fileChannel.transferFrom(fileChannel, l, l2);
      }

      public long transferFrom(StreamSourceChannel streamSourceChannel, long l, ByteBuffer byteBuffer) throws IOException {
         return IoUtils.transfer(streamSourceChannel, l, byteBuffer, this.fileChannel);
      }

      public int write(ByteBuffer byteBuffer) throws IOException {
         return this.fileChannel.write(byteBuffer);
      }

      public long write(ByteBuffer[] byteBuffers, int i, int i2) throws IOException {
         return this.fileChannel.write(byteBuffers, i, i2);
      }

      public int writeFinal(ByteBuffer src) throws IOException {
         return Conduits.writeFinalBasic(this, src);
      }

      public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
         return Conduits.writeFinalBasic(this, srcs, offset, length);
      }

      public void terminateWrites() throws IOException {
         this.fileChannel.close();
      }

      public boolean isWriteShutdown() {
         return !this.fileChannel.isOpen();
      }

      public void resumeWrites() {
         this.wakeupWrites();
      }

      public void suspendWrites() {
         this.writesResumed = false;
      }

      public void wakeupWrites() {
         if (this.writeReadyHandler != null) {
            this.writesResumed = true;

            while(this.writesResumed && this.writeReadyHandler != null) {
               this.writeReadyHandler.writeReady();
            }
         }

      }

      public boolean isWriteResumed() {
         return this.writesResumed;
      }

      public void awaitWritable() throws IOException {
      }

      public void awaitWritable(long l, TimeUnit timeUnit) throws IOException {
      }

      public XnioIoThread getWriteThread() {
         return this.exchange.getIoThread();
      }

      public void setWriteReadyHandler(WriteReadyHandler writeReadyHandler) {
         this.writeReadyHandler = writeReadyHandler;
      }

      public void truncateWrites() throws IOException {
         this.fileChannel.close();
      }

      public boolean flush() throws IOException {
         return true;
      }

      public XnioWorker getWorker() {
         return this.exchange.getConnection().getWorker();
      }

      // $FF: synthetic method
      FileConduitTarget(FileChannel x0, HttpServerExchange x1, Object x2) {
         this(x0, x1);
      }
   }

   private static final class LockKey {
      private final String path;
      private final String encoding;

      private LockKey(String path, String encoding) {
         this.path = path;
         this.encoding = encoding;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            LockKey lockKey = (LockKey)o;
            if (this.encoding != null) {
               if (!this.encoding.equals(lockKey.encoding)) {
                  return false;
               }
            } else if (lockKey.encoding != null) {
               return false;
            }

            if (this.path != null) {
               if (this.path.equals(lockKey.path)) {
                  return true;
               }
            } else if (lockKey.path == null) {
               return true;
            }

            return false;
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.path != null ? this.path.hashCode() : 0;
         result = 31 * result + (this.encoding != null ? this.encoding.hashCode() : 0);
         return result;
      }

      // $FF: synthetic method
      LockKey(String x0, String x1, Object x2) {
         this(x0, x1);
      }
   }
}
