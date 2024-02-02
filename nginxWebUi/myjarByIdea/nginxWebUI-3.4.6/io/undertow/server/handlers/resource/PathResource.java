package io.undertow.server.handlers.resource;

import io.undertow.UndertowLogger;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.DateUtils;
import io.undertow.util.ETag;
import io.undertow.util.MimeMappings;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.xnio.IoUtils;

public class PathResource implements RangeAwareResource {
   private final Path file;
   private final String path;
   private final ETag eTag;
   private final PathResourceManager manager;

   public PathResource(Path file, PathResourceManager manager, String path, ETag eTag) {
      this.file = file;
      this.path = path;
      this.manager = manager;
      this.eTag = eTag;
   }

   public PathResource(Path file, PathResourceManager manager, String path) {
      this(file, manager, path, (ETag)null);
   }

   public String getPath() {
      return this.path;
   }

   public Date getLastModified() {
      try {
         return Files.isSymbolicLink(this.file) && Files.notExists(this.file, new LinkOption[0]) ? null : new Date(Files.getLastModifiedTime(this.file).toMillis());
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public String getLastModifiedString() {
      return DateUtils.toDateString(this.getLastModified());
   }

   public ETag getETag() {
      return this.eTag;
   }

   public String getName() {
      return this.file.getFileName() != null ? this.file.getFileName().toString() : this.file.toString();
   }

   public boolean isDirectory() {
      return Files.isDirectory(this.file, new LinkOption[0]);
   }

   public List<Resource> list() {
      List<Resource> resources = new ArrayList();

      try {
         DirectoryStream<Path> stream = Files.newDirectoryStream(this.file);
         Throwable var3 = null;

         try {
            Iterator var4 = stream.iterator();

            while(var4.hasNext()) {
               Path child = (Path)var4.next();
               resources.add(new PathResource(child, this.manager, this.path + this.file.getFileSystem().getSeparator() + child.getFileName().toString()));
            }
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (stream != null) {
               if (var3 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  stream.close();
               }
            }

         }

         return resources;
      } catch (IOException var16) {
         throw new RuntimeException(var16);
      }
   }

   public String getContentType(MimeMappings mimeMappings) {
      String fileName = this.file.getFileName().toString();
      int index = fileName.lastIndexOf(46);
      return index != -1 && index != fileName.length() - 1 ? mimeMappings.getMimeType(fileName.substring(index + 1)) : null;
   }

   public void serve(Sender sender, HttpServerExchange exchange, IoCallback callback) {
      this.serveImpl(sender, exchange, -1L, -1L, callback, false);
   }

   public void serveRange(Sender sender, HttpServerExchange exchange, long start, long end, IoCallback callback) {
      this.serveImpl(sender, exchange, start, end, callback, true);
   }

   private void serveImpl(final Sender sender, final HttpServerExchange exchange, final long start, final long end, final IoCallback callback, final boolean range) {
      Object task;
      try {
         class TransferTask extends BaseFileTask {
            TransferTask() {
               super(range, start, exchange, callback, sender);
            }

            public void run() {
               if (this.openFile()) {
                  sender.transferFrom(this.fileChannel, new IoCallback() {
                     public void onComplete(HttpServerExchange exchangex, Sender senderx) {
                        try {
                           IoUtils.safeClose((Closeable)TransferTask.this.fileChannel);
                        } finally {
                           callback.onComplete(exchangex, senderx);
                        }

                     }

                     public void onException(HttpServerExchange exchangex, Sender senderx, IOException exception) {
                        try {
                           IoUtils.safeClose((Closeable)TransferTask.this.fileChannel);
                        } finally {
                           callback.onException(exchangex, senderx, exception);
                        }

                     }
                  });
               }
            }
         }


         class ServerTask extends BaseFileTask implements IoCallback {
            private PooledByteBuffer pooled;
            long remaining = end - start + 1L;

            ServerTask() {
               super(range, start, exchange, callback, sender);
            }

            public void run() {
               if (range && this.remaining == 0L) {
                  if (this.pooled != null) {
                     this.pooled.close();
                     this.pooled = null;
                  }

                  IoUtils.safeClose((Closeable)this.fileChannel);
                  callback.onComplete(exchange, sender);
               } else {
                  if (this.fileChannel == null) {
                     if (!this.openFile()) {
                        return;
                     }

                     this.pooled = exchange.getConnection().getByteBufferPool().allocate();
                  }

                  if (this.pooled != null) {
                     ByteBuffer buffer = this.pooled.getBuffer();

                     try {
                        buffer.clear();
                        int res = this.fileChannel.read(buffer);
                        if (res == -1) {
                           this.pooled.close();
                           IoUtils.safeClose((Closeable)this.fileChannel);
                           callback.onComplete(exchange, sender);
                           return;
                        }

                        buffer.flip();
                        if (range) {
                           if ((long)buffer.remaining() > this.remaining) {
                              buffer.limit((int)((long)buffer.position() + this.remaining));
                           }

                           this.remaining -= (long)buffer.remaining();
                        }

                        sender.send((ByteBuffer)buffer, (IoCallback)this);
                     } catch (IOException var3) {
                        this.onException(exchange, sender, var3);
                     }
                  }

               }
            }

            public void onComplete(HttpServerExchange exchangex, Sender senderx) {
               if (exchangex.isInIoThread()) {
                  exchangex.dispatch((Runnable)this);
               } else {
                  this.run();
               }

            }

            public void onException(HttpServerExchange exchangex, Sender senderx, IOException exception) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
               if (this.pooled != null) {
                  this.pooled.close();
                  this.pooled = null;
               }

               IoUtils.safeClose((Closeable)this.fileChannel);
               if (!exchangex.isResponseStarted()) {
                  exchangex.setStatusCode(500);
               }

               callback.onException(exchangex, senderx, exception);
            }
         }

         task = this.manager.getTransferMinSize() <= Files.size(this.file) && !range ? new TransferTask() : new ServerTask();
      } catch (IOException var11) {
         throw new RuntimeException(var11);
      }

      if (exchange.isInIoThread()) {
         exchange.dispatch((Runnable)task);
      } else {
         abstract class BaseFileTask implements Runnable {
            protected volatile FileChannel fileChannel;
            // $FF: synthetic field
            final boolean val$range;
            // $FF: synthetic field
            final long val$start;
            // $FF: synthetic field
            final HttpServerExchange val$exchange;
            // $FF: synthetic field
            final IoCallback val$callback;
            // $FF: synthetic field
            final Sender val$sender;

            BaseFileTask(boolean var2, long var3, HttpServerExchange var5, IoCallback var6, Sender var7) {
               this.val$range = var2;
               this.val$start = var3;
               this.val$exchange = var5;
               this.val$callback = var6;
               this.val$sender = var7;
            }

            protected boolean openFile() {
               try {
                  this.fileChannel = FileChannel.open(PathResource.this.file, StandardOpenOption.READ);
                  if (this.val$range) {
                     this.fileChannel.position(this.val$start);
                  }

                  return true;
               } catch (NoSuchFileException var2) {
                  this.val$exchange.setStatusCode(404);
                  this.val$callback.onException(this.val$exchange, this.val$sender, var2);
                  return false;
               } catch (IOException var3) {
                  this.val$exchange.setStatusCode(500);
                  this.val$callback.onException(this.val$exchange, this.val$sender, var3);
                  return false;
               }
            }
         }

         ((BaseFileTask)task).run();
      }

   }

   public Long getContentLength() {
      try {
         return Files.isSymbolicLink(this.file) && Files.notExists(this.file, new LinkOption[0]) ? null : Files.size(this.file);
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public String getCacheKey() {
      return this.file.toString();
   }

   public File getFile() {
      return this.file.toFile();
   }

   public Path getFilePath() {
      return this.file;
   }

   public File getResourceManagerRoot() {
      return this.manager.getBasePath().toFile();
   }

   public Path getResourceManagerRootPath() {
      return this.manager.getBasePath();
   }

   public URL getUrl() {
      try {
         return this.file.toUri().toURL();
      } catch (MalformedURLException var2) {
         throw new RuntimeException(var2);
      }
   }

   public boolean isRangeSupported() {
      return true;
   }
}
