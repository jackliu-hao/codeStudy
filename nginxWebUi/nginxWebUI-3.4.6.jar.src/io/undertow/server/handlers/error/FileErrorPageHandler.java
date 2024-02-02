/*     */ package io.undertow.server.handlers.error;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.DefaultResponseListener;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.MimeMappings;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
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
/*     */ public class FileErrorPageHandler
/*     */   implements HttpHandler
/*     */ {
/*  64 */   private static final Logger log = Logger.getLogger("io.undertow.server.error.file");
/*  65 */   private volatile HttpHandler next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*     */ 
/*     */   
/*     */   private volatile Set<Integer> responseCodes;
/*     */ 
/*     */   
/*     */   private volatile Path file;
/*     */   
/*     */   private final MimeMappings mimeMappings;
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public FileErrorPageHandler(File file, Integer... responseCodes) {
/*  78 */     this(file.toPath(), responseCodes);
/*     */   }
/*     */   
/*     */   public FileErrorPageHandler(Path file, Integer... responseCodes) {
/*  82 */     this.file = file;
/*  83 */     this.responseCodes = new HashSet<>(Arrays.asList(responseCodes));
/*  84 */     this.mimeMappings = MimeMappings.DEFAULT;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public FileErrorPageHandler(HttpHandler next, File file, Integer... responseCodes) {
/*  89 */     this(next, file.toPath(), responseCodes);
/*     */   }
/*     */   
/*     */   public FileErrorPageHandler(HttpHandler next, Path file, Integer... responseCodes) {
/*  93 */     this(next, file, MimeMappings.DEFAULT, responseCodes);
/*     */   }
/*     */   
/*     */   public FileErrorPageHandler(HttpHandler next, Path file, MimeMappings mimeMappings, Integer... responseCodes) {
/*  97 */     this.next = next;
/*  98 */     this.file = file;
/*  99 */     this.responseCodes = new HashSet<>(Arrays.asList(responseCodes));
/* 100 */     this.mimeMappings = mimeMappings;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 105 */     exchange.addDefaultResponseListener(new DefaultResponseListener()
/*     */         {
/*     */           public boolean handleDefaultResponse(HttpServerExchange exchange) {
/* 108 */             Set<Integer> codes = FileErrorPageHandler.this.responseCodes;
/* 109 */             if (!exchange.isResponseStarted() && codes.contains(Integer.valueOf(exchange.getStatusCode()))) {
/* 110 */               FileErrorPageHandler.this.serveFile(exchange);
/* 111 */               return true;
/*     */             } 
/* 113 */             return false;
/*     */           }
/*     */         });
/*     */     
/* 117 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private void serveFile(final HttpServerExchange exchange) {
/* 121 */     String fileName = this.file.toString();
/* 122 */     int index = fileName.lastIndexOf(".");
/* 123 */     if (index > 0) {
/* 124 */       String contentType = this.mimeMappings.getMimeType(fileName.substring(index + 1));
/* 125 */       if (contentType != null) {
/* 126 */         exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
/*     */       }
/*     */     } 
/* 129 */     exchange.dispatch(new Runnable() {
/*     */           public void run() {
/*     */             final FileChannel fileChannel;
/*     */             long size;
/*     */             try {
/*     */               try {
/* 135 */                 fileChannel = FileChannel.open(FileErrorPageHandler.this.file, new OpenOption[] { StandardOpenOption.READ });
/* 136 */               } catch (FileNotFoundException e) {
/* 137 */                 UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 138 */                 exchange.endExchange();
/*     */                 return;
/*     */               } 
/* 141 */             } catch (IOException e) {
/* 142 */               UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 143 */               exchange.endExchange();
/*     */               
/*     */               return;
/*     */             } 
/*     */             try {
/* 148 */               size = Files.size(FileErrorPageHandler.this.file);
/* 149 */             } catch (IOException e) {
/* 150 */               throw new RuntimeException(e);
/*     */             } 
/* 152 */             exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, size);
/* 153 */             StreamSinkChannel response = exchange.getResponseChannel();
/* 154 */             exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */                 {
/*     */                   public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/* 157 */                     IoUtils.safeClose(fileChannel);
/* 158 */                     nextListener.proceed();
/*     */                   }
/*     */                 });
/*     */             
/*     */             try {
/* 163 */               FileErrorPageHandler.log.tracef("Serving file %s (blocking)", fileChannel);
/* 164 */               Channels.transferBlocking(response, fileChannel, 0L, Files.size(FileErrorPageHandler.this.file));
/* 165 */               FileErrorPageHandler.log.tracef("Finished serving %s, shutting down (blocking)", fileChannel);
/* 166 */               response.shutdownWrites();
/* 167 */               FileErrorPageHandler.log.tracef("Finished serving %s, flushing (blocking)", fileChannel);
/* 168 */               Channels.flushBlocking((SuspendableWriteChannel)response);
/* 169 */               FileErrorPageHandler.log.tracef("Finished serving %s (complete)", fileChannel);
/* 170 */               exchange.endExchange();
/* 171 */             } catch (IOException ignored) {
/* 172 */               FileErrorPageHandler.log.tracef("Failed to serve %s: %s", fileChannel, ignored);
/* 173 */               exchange.endExchange();
/* 174 */               IoUtils.safeClose((Closeable)response);
/*     */             } finally {
/* 176 */               IoUtils.safeClose(fileChannel);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/* 183 */     return this.next;
/*     */   }
/*     */   
/*     */   public FileErrorPageHandler setNext(HttpHandler next) {
/* 187 */     Handlers.handlerNotNull(next);
/* 188 */     this.next = next;
/* 189 */     return this;
/*     */   }
/*     */   
/*     */   public Set<Integer> getResponseCodes() {
/* 193 */     return Collections.unmodifiableSet(this.responseCodes);
/*     */   }
/*     */   
/*     */   public FileErrorPageHandler setResponseCodes(Set<Integer> responseCodes) {
/* 197 */     if (responseCodes == null) {
/* 198 */       this.responseCodes = Collections.emptySet();
/*     */     } else {
/* 200 */       this.responseCodes = new HashSet<>(responseCodes);
/*     */     } 
/* 202 */     return this;
/*     */   }
/*     */   
/*     */   public FileErrorPageHandler setResponseCodes(Integer... responseCodes) {
/* 206 */     this.responseCodes = new HashSet<>(Arrays.asList(responseCodes));
/* 207 */     return this;
/*     */   }
/*     */   
/*     */   public Path getFile() {
/* 211 */     return this.file;
/*     */   }
/*     */   
/*     */   public FileErrorPageHandler setFile(Path file) {
/* 215 */     this.file = file;
/* 216 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 221 */     return "response-codes( file='" + this.file.toString() + "', response-codes={ " + (String)this.responseCodes.stream().map(s -> s.toString()).collect(Collectors.joining(", ")) + " } )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 228 */       return "error-file";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 233 */       Map<String, Class<?>> params = new HashMap<>();
/* 234 */       params.put("file", String.class);
/* 235 */       params.put("response-codes", Integer[].class);
/* 236 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 241 */       return new HashSet<>(Arrays.asList(new String[] { "file", "response-codes" }));
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 246 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 251 */       return new FileErrorPageHandler.Wrapper((String)config.get("file"), (Integer[])config.get("response-codes"));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final String file;
/*     */     private final Integer[] responseCodes;
/*     */     
/*     */     private Wrapper(String file, Integer[] responseCodes) {
/* 262 */       this.file = file;
/* 263 */       this.responseCodes = responseCodes;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 269 */       return new FileErrorPageHandler(handler, Paths.get(this.file, new String[0]), this.responseCodes);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\error\FileErrorPageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */