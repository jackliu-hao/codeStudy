package io.undertow.server.handlers.resource;

import io.undertow.UndertowLogger;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.DateUtils;
import io.undertow.util.ETag;
import io.undertow.util.MimeMappings;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xnio.IoUtils;

public class URLResource implements Resource, RangeAwareResource {
   private final URL url;
   private final String path;
   private boolean connectionOpened;
   private Date lastModified;
   private Long contentLength;

   /** @deprecated */
   @Deprecated
   public URLResource(URL url, URLConnection connection, String path) {
      this(url, path);
   }

   public URLResource(URL url, String path) {
      this.connectionOpened = false;
      this.url = url;
      this.path = path;
   }

   public String getPath() {
      return this.path;
   }

   public Date getLastModified() {
      this.openConnection();
      return this.lastModified;
   }

   private void openConnection() {
      if (!this.connectionOpened) {
         this.connectionOpened = true;
         URLConnection connection = null;

         try {
            try {
               connection = this.url.openConnection();
            } catch (IOException var12) {
               this.lastModified = null;
               this.contentLength = null;
               return;
            }

            if (this.url.getProtocol().equals("jar")) {
               connection.setUseCaches(false);
               URL jar = ((JarURLConnection)connection).getJarFileURL();
               this.lastModified = new Date((new File(jar.getFile())).lastModified());
            } else {
               this.lastModified = new Date(connection.getLastModified());
            }

            this.contentLength = connection.getContentLengthLong();
         } finally {
            if (connection != null) {
               try {
                  IoUtils.safeClose((Closeable)connection.getInputStream());
               } catch (IOException var11) {
               }
            }

         }
      }
   }

   public String getLastModifiedString() {
      return DateUtils.toDateString(this.getLastModified());
   }

   public ETag getETag() {
      return null;
   }

   public String getName() {
      String path = this.url.getPath();
      if (path.endsWith("/")) {
         path = path.substring(0, path.length() - 1);
      }

      int sepIndex = path.lastIndexOf("/");
      if (sepIndex != -1) {
         path = path.substring(sepIndex + 1);
      }

      return path;
   }

   public boolean isDirectory() {
      Path file = this.getFilePath();
      if (file != null) {
         return Files.isDirectory(file, new LinkOption[0]);
      } else {
         return this.url.getPath().endsWith("/");
      }
   }

   public List<Resource> list() {
      List<Resource> result = new LinkedList();
      Path file = this.getFilePath();

      try {
         if (file != null) {
            DirectoryStream<Path> stream = Files.newDirectoryStream(file);
            Throwable var4 = null;

            try {
               Iterator var5 = stream.iterator();

               while(var5.hasNext()) {
                  Path child = (Path)var5.next();
                  result.add(new URLResource(child.toUri().toURL(), child.toString()));
               }
            } catch (Throwable var15) {
               var4 = var15;
               throw var15;
            } finally {
               if (stream != null) {
                  if (var4 != null) {
                     try {
                        stream.close();
                     } catch (Throwable var14) {
                        var4.addSuppressed(var14);
                     }
                  } else {
                     stream.close();
                  }
               }

            }
         }

         return result;
      } catch (IOException var17) {
         throw new RuntimeException(var17);
      }
   }

   public String getContentType(MimeMappings mimeMappings) {
      String fileName = this.getName();
      int index = fileName.lastIndexOf(46);
      return index != -1 && index != fileName.length() - 1 ? mimeMappings.getMimeType(fileName.substring(index + 1)) : null;
   }

   public void serve(Sender sender, HttpServerExchange exchange, IoCallback completionCallback) {
      this.serveImpl(sender, exchange, -1L, -1L, false, completionCallback);
   }

   public void serveImpl(final Sender sender, final HttpServerExchange exchange, final long start, final long end, final boolean range, final IoCallback completionCallback) {
      class ServerTask implements Runnable, IoCallback {
         private InputStream inputStream;
         private byte[] buffer;
         long toSkip = start;
         long remaining = end - start + 1L;

         public void run() {
            if (range && this.remaining == 0L) {
               IoUtils.safeClose((Closeable)this.inputStream);
               completionCallback.onComplete(exchange, sender);
            } else {
               if (this.inputStream == null) {
                  try {
                     this.inputStream = URLResource.this.url.openStream();
                  } catch (IOException var4) {
                     exchange.setStatusCode(500);
                     return;
                  }

                  this.buffer = new byte[1024];
               }

               try {
                  int res = this.inputStream.read(this.buffer);
                  if (res == -1) {
                     IoUtils.safeClose((Closeable)this.inputStream);
                     completionCallback.onComplete(exchange, sender);
                     return;
                  }

                  int bufferStart = 0;
                  int length = res;
                  if (range && this.toSkip > 0L) {
                     while(this.toSkip > (long)res) {
                        this.toSkip -= (long)res;
                        res = this.inputStream.read(this.buffer);
                        if (res == -1) {
                           IoUtils.safeClose((Closeable)this.inputStream);
                           completionCallback.onComplete(exchange, sender);
                           return;
                        }
                     }

                     bufferStart = (int)this.toSkip;
                     length = (int)((long)length - this.toSkip);
                     this.toSkip = 0L;
                  }

                  if (range && (long)length > this.remaining) {
                     length = (int)this.remaining;
                  }

                  sender.send((ByteBuffer)ByteBuffer.wrap(this.buffer, bufferStart, length), (IoCallback)this);
               } catch (IOException var5) {
                  this.onException(exchange, sender, var5);
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
            IoUtils.safeClose((Closeable)this.inputStream);
            if (!exchangex.isResponseStarted()) {
               exchangex.setStatusCode(500);
            }

            completionCallback.onException(exchangex, senderx, exception);
         }
      }

      ServerTask serveTask = new ServerTask();
      if (exchange.isInIoThread()) {
         exchange.dispatch((Runnable)serveTask);
      } else {
         serveTask.run();
      }

   }

   public Long getContentLength() {
      this.openConnection();
      return this.contentLength;
   }

   public String getCacheKey() {
      return this.url.toString();
   }

   public File getFile() {
      Path path = this.getFilePath();
      return path != null ? path.toFile() : null;
   }

   public Path getFilePath() {
      if (this.url.getProtocol().equals("file")) {
         try {
            return Paths.get(this.url.toURI());
         } catch (URISyntaxException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   public File getResourceManagerRoot() {
      return null;
   }

   public Path getResourceManagerRootPath() {
      return null;
   }

   public URL getUrl() {
      return this.url;
   }

   public void serveRange(Sender sender, HttpServerExchange exchange, long start, long end, IoCallback completionCallback) {
      this.serveImpl(sender, exchange, start, end, true, completionCallback);
   }

   public boolean isRangeSupported() {
      return true;
   }
}
