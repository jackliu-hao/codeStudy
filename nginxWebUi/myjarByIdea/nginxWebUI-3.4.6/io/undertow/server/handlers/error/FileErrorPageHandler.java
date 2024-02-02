package io.undertow.server.handlers.error;

import io.undertow.Handlers;
import io.undertow.UndertowLogger;
import io.undertow.server.DefaultResponseListener;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.Headers;
import io.undertow.util.MimeMappings;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.xnio.IoUtils;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;

public class FileErrorPageHandler implements HttpHandler {
   private static final Logger log = Logger.getLogger("io.undertow.server.error.file");
   private volatile HttpHandler next;
   private volatile Set<Integer> responseCodes;
   private volatile Path file;
   private final MimeMappings mimeMappings;

   /** @deprecated */
   @Deprecated
   public FileErrorPageHandler(File file, Integer... responseCodes) {
      this(file.toPath(), responseCodes);
   }

   public FileErrorPageHandler(Path file, Integer... responseCodes) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.file = file;
      this.responseCodes = new HashSet(Arrays.asList(responseCodes));
      this.mimeMappings = MimeMappings.DEFAULT;
   }

   /** @deprecated */
   @Deprecated
   public FileErrorPageHandler(HttpHandler next, File file, Integer... responseCodes) {
      this(next, file.toPath(), responseCodes);
   }

   public FileErrorPageHandler(HttpHandler next, Path file, Integer... responseCodes) {
      this(next, file, MimeMappings.DEFAULT, responseCodes);
   }

   public FileErrorPageHandler(HttpHandler next, Path file, MimeMappings mimeMappings, Integer... responseCodes) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.next = next;
      this.file = file;
      this.responseCodes = new HashSet(Arrays.asList(responseCodes));
      this.mimeMappings = mimeMappings;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.addDefaultResponseListener(new DefaultResponseListener() {
         public boolean handleDefaultResponse(HttpServerExchange exchange) {
            Set<Integer> codes = FileErrorPageHandler.this.responseCodes;
            if (!exchange.isResponseStarted() && codes.contains(exchange.getStatusCode())) {
               FileErrorPageHandler.this.serveFile(exchange);
               return true;
            } else {
               return false;
            }
         }
      });
      this.next.handleRequest(exchange);
   }

   private void serveFile(final HttpServerExchange exchange) {
      String fileName = this.file.toString();
      int index = fileName.lastIndexOf(".");
      if (index > 0) {
         String contentType = this.mimeMappings.getMimeType(fileName.substring(index + 1));
         if (contentType != null) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
         }
      }

      exchange.dispatch(new Runnable() {
         public void run() {
            final FileChannel fileChannel;
            try {
               try {
                  fileChannel = FileChannel.open(FileErrorPageHandler.this.file, StandardOpenOption.READ);
               } catch (FileNotFoundException var15) {
                  UndertowLogger.REQUEST_IO_LOGGER.ioException(var15);
                  exchange.endExchange();
                  return;
               }
            } catch (IOException var16) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(var16);
               exchange.endExchange();
               return;
            }

            long size;
            try {
               size = Files.size(FileErrorPageHandler.this.file);
            } catch (IOException var14) {
               throw new RuntimeException(var14);
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, size);
            StreamSinkChannel response = exchange.getResponseChannel();
            exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
               public void exchangeEvent(HttpServerExchange exchangex, ExchangeCompletionListener.NextListener nextListener) {
                  IoUtils.safeClose((Closeable)fileChannel);
                  nextListener.proceed();
               }
            });

            try {
               FileErrorPageHandler.log.tracef("Serving file %s (blocking)", (Object)fileChannel);
               Channels.transferBlocking(response, fileChannel, 0L, Files.size(FileErrorPageHandler.this.file));
               FileErrorPageHandler.log.tracef("Finished serving %s, shutting down (blocking)", (Object)fileChannel);
               response.shutdownWrites();
               FileErrorPageHandler.log.tracef("Finished serving %s, flushing (blocking)", (Object)fileChannel);
               Channels.flushBlocking(response);
               FileErrorPageHandler.log.tracef("Finished serving %s (complete)", (Object)fileChannel);
               exchange.endExchange();
            } catch (IOException var12) {
               FileErrorPageHandler.log.tracef((String)"Failed to serve %s: %s", (Object)fileChannel, (Object)var12);
               exchange.endExchange();
               IoUtils.safeClose((Closeable)response);
            } finally {
               IoUtils.safeClose((Closeable)fileChannel);
            }

         }
      });
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public FileErrorPageHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }

   public Set<Integer> getResponseCodes() {
      return Collections.unmodifiableSet(this.responseCodes);
   }

   public FileErrorPageHandler setResponseCodes(Set<Integer> responseCodes) {
      if (responseCodes == null) {
         this.responseCodes = Collections.emptySet();
      } else {
         this.responseCodes = new HashSet(responseCodes);
      }

      return this;
   }

   public FileErrorPageHandler setResponseCodes(Integer... responseCodes) {
      this.responseCodes = new HashSet(Arrays.asList(responseCodes));
      return this;
   }

   public Path getFile() {
      return this.file;
   }

   public FileErrorPageHandler setFile(Path file) {
      this.file = file;
      return this;
   }

   public String toString() {
      return "response-codes( file='" + this.file.toString() + "', response-codes={ " + (String)this.responseCodes.stream().map((s) -> {
         return s.toString();
      }).collect(Collectors.joining(", ")) + " } )";
   }

   private static class Wrapper implements HandlerWrapper {
      private final String file;
      private final Integer[] responseCodes;

      private Wrapper(String file, Integer[] responseCodes) {
         this.file = file;
         this.responseCodes = responseCodes;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new FileErrorPageHandler(handler, Paths.get(this.file), this.responseCodes);
      }

      // $FF: synthetic method
      Wrapper(String x0, Integer[] x1, Object x2) {
         this(x0, x1);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "error-file";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("file", String.class);
         params.put("response-codes", Integer[].class);
         return params;
      }

      public Set<String> requiredParameters() {
         return new HashSet(Arrays.asList("file", "response-codes"));
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((String)config.get("file"), (Integer[])((Integer[])config.get("response-codes")));
      }
   }
}
