/*     */ package io.undertow.server.handlers.resource;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.Predicates;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.server.handlers.cache.ResponseCache;
/*     */ import io.undertow.server.handlers.encoding.ContentEncodedResource;
/*     */ import io.undertow.server.handlers.encoding.ContentEncodedResourceManager;
/*     */ import io.undertow.util.ByteRange;
/*     */ import io.undertow.util.CanonicalPathUtils;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.ETagUtils;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.MimeMappings;
/*     */ import io.undertow.util.RedirectBuilder;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class ResourceHandler
/*     */   implements HttpHandler
/*     */ {
/*  68 */   private static final Set<HttpString> KNOWN_METHODS = new HashSet<>();
/*     */   
/*     */   static {
/*  71 */     KNOWN_METHODS.add(Methods.OPTIONS);
/*  72 */     KNOWN_METHODS.add(Methods.GET);
/*  73 */     KNOWN_METHODS.add(Methods.HEAD);
/*  74 */     KNOWN_METHODS.add(Methods.POST);
/*  75 */     KNOWN_METHODS.add(Methods.PUT);
/*  76 */     KNOWN_METHODS.add(Methods.DELETE);
/*  77 */     KNOWN_METHODS.add(Methods.TRACE);
/*  78 */     KNOWN_METHODS.add(Methods.CONNECT);
/*     */   }
/*     */   
/*  81 */   private final List<String> welcomeFiles = new CopyOnWriteArrayList<>(new String[] { "index.html", "index.htm", "default.html", "default.htm" });
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean directoryListingEnabled = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean canonicalizePaths = true;
/*     */ 
/*     */ 
/*     */   
/*  95 */   private volatile MimeMappings mimeMappings = MimeMappings.DEFAULT;
/*  96 */   private volatile Predicate cachable = Predicates.truePredicate();
/*  97 */   private volatile Predicate allowed = Predicates.truePredicate();
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile ResourceSupplier resourceSupplier;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile ResourceManager resourceManager;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile Integer cacheTime;
/*     */ 
/*     */   
/*     */   private volatile ContentEncodedResourceManager contentEncodedResourceManager;
/*     */ 
/*     */   
/*     */   private final HttpHandler next;
/*     */ 
/*     */ 
/*     */   
/*     */   public ResourceHandler(ResourceManager resourceSupplier) {
/* 120 */     this(resourceSupplier, (HttpHandler)ResponseCodeHandler.HANDLE_404);
/*     */   }
/*     */   
/*     */   public ResourceHandler(ResourceManager resourceManager, HttpHandler next) {
/* 124 */     this.resourceSupplier = new DefaultResourceSupplier(resourceManager);
/* 125 */     this.resourceManager = resourceManager;
/* 126 */     this.next = next;
/*     */   }
/*     */   
/*     */   public ResourceHandler(ResourceSupplier resourceSupplier) {
/* 130 */     this(resourceSupplier, (HttpHandler)ResponseCodeHandler.HANDLE_404);
/*     */   }
/*     */   
/*     */   public ResourceHandler(ResourceSupplier resourceManager, HttpHandler next) {
/* 134 */     this.resourceSupplier = resourceManager;
/* 135 */     this.next = next;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ResourceHandler() {
/* 144 */     this.next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 149 */     if (exchange.getRequestMethod().equals(Methods.GET) || exchange
/* 150 */       .getRequestMethod().equals(Methods.POST)) {
/* 151 */       serveResource(exchange, true);
/* 152 */     } else if (exchange.getRequestMethod().equals(Methods.HEAD)) {
/* 153 */       serveResource(exchange, false);
/*     */     } else {
/* 155 */       if (KNOWN_METHODS.contains(exchange.getRequestMethod())) {
/* 156 */         exchange.setStatusCode(405);
/* 157 */         exchange.getResponseHeaders().add(Headers.ALLOW, 
/* 158 */             String.join(", ", new CharSequence[] { "GET", "HEAD", "POST" }));
/*     */       } else {
/* 160 */         exchange.setStatusCode(501);
/*     */       } 
/* 162 */       exchange.endExchange();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void serveResource(HttpServerExchange exchange, final boolean sendContent) throws Exception {
/* 168 */     if (DirectoryUtils.sendRequestedBlobs(exchange)) {
/*     */       return;
/*     */     }
/*     */     
/* 172 */     if (!this.allowed.resolve(exchange)) {
/* 173 */       exchange.setStatusCode(403);
/* 174 */       exchange.endExchange();
/*     */       
/*     */       return;
/*     */     } 
/* 178 */     ResponseCache cache = (ResponseCache)exchange.getAttachment(ResponseCache.ATTACHMENT_KEY);
/* 179 */     boolean cachable = this.cachable.resolve(exchange);
/*     */ 
/*     */     
/* 182 */     if (cachable && this.cacheTime != null) {
/* 183 */       exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, "public, max-age=" + this.cacheTime);
/* 184 */       long date = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(this.cacheTime.intValue());
/* 185 */       String dateHeader = DateUtils.toDateString(new Date(date));
/* 186 */       exchange.getResponseHeaders().put(Headers.EXPIRES, dateHeader);
/*     */     } 
/*     */     
/* 189 */     if (cache != null && cachable && 
/* 190 */       cache.tryServeResponse()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 197 */     HttpHandler dispatchTask = new HttpHandler()
/*     */       {
/*     */         public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 200 */           Resource resource = null;
/*     */           try {
/* 202 */             if (File.separatorChar == '/' || !exchange.getRelativePath().contains(File.separator))
/*     */             {
/*     */               
/* 205 */               resource = ResourceHandler.this.resourceSupplier.getResource(exchange, ResourceHandler.this.canonicalize(exchange.getRelativePath()));
/*     */             }
/* 207 */           } catch (IOException e) {
/* 208 */             ResourceHandler.this.clearCacheHeaders(exchange);
/* 209 */             UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 210 */             exchange.setStatusCode(500);
/* 211 */             exchange.endExchange();
/*     */             return;
/*     */           } 
/* 214 */           if (resource == null) {
/* 215 */             ResourceHandler.this.clearCacheHeaders(exchange);
/*     */             
/* 217 */             ResourceHandler.this.next.handleRequest(exchange);
/*     */             
/*     */             return;
/*     */           } 
/* 221 */           if (resource.isDirectory()) {
/*     */             Resource indexResource;
/*     */             try {
/* 224 */               indexResource = ResourceHandler.this.getIndexFiles(exchange, ResourceHandler.this.resourceSupplier, resource.getPath(), ResourceHandler.this.welcomeFiles);
/* 225 */             } catch (IOException e) {
/* 226 */               UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 227 */               exchange.setStatusCode(500);
/* 228 */               exchange.endExchange();
/*     */               return;
/*     */             } 
/* 231 */             if (indexResource == null) {
/* 232 */               if (ResourceHandler.this.directoryListingEnabled) {
/* 233 */                 DirectoryUtils.renderDirectoryListing(exchange, resource);
/*     */                 return;
/*     */               } 
/* 236 */               exchange.setStatusCode(403);
/* 237 */               exchange.endExchange();
/*     */               return;
/*     */             } 
/* 240 */             if (!exchange.getRequestPath().endsWith("/")) {
/* 241 */               exchange.setStatusCode(302);
/* 242 */               exchange.getResponseHeaders().put(Headers.LOCATION, RedirectBuilder.redirect(exchange, exchange.getRelativePath() + "/", true));
/* 243 */               exchange.endExchange();
/*     */               return;
/*     */             } 
/* 246 */             resource = indexResource;
/* 247 */           } else if (exchange.getRelativePath().endsWith("/")) {
/*     */             
/* 249 */             exchange.setStatusCode(404);
/* 250 */             exchange.endExchange();
/*     */             
/*     */             return;
/*     */           } 
/* 254 */           ETag etag = resource.getETag();
/* 255 */           Date lastModified = resource.getLastModified();
/* 256 */           if (!ETagUtils.handleIfMatch(exchange, etag, false) || 
/* 257 */             !DateUtils.handleIfUnmodifiedSince(exchange, lastModified)) {
/* 258 */             exchange.setStatusCode(412);
/* 259 */             exchange.endExchange();
/*     */             return;
/*     */           } 
/* 262 */           if (!ETagUtils.handleIfNoneMatch(exchange, etag, true) || 
/* 263 */             !DateUtils.handleIfModifiedSince(exchange, lastModified)) {
/* 264 */             exchange.setStatusCode(304);
/* 265 */             exchange.endExchange();
/*     */             return;
/*     */           } 
/* 268 */           ContentEncodedResourceManager contentEncodedResourceManager = ResourceHandler.this.contentEncodedResourceManager;
/* 269 */           Long contentLength = resource.getContentLength();
/*     */           
/* 271 */           if (contentLength != null && !exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/* 272 */             exchange.setResponseContentLength(contentLength.longValue());
/*     */           }
/* 274 */           ByteRange.RangeResponseResult rangeResponse = null;
/* 275 */           long start = -1L, end = -1L;
/* 276 */           if (resource instanceof RangeAwareResource && ((RangeAwareResource)resource).isRangeSupported() && contentLength != null && contentEncodedResourceManager == null) {
/*     */             
/* 278 */             exchange.getResponseHeaders().put(Headers.ACCEPT_RANGES, "bytes");
/*     */             
/* 280 */             ByteRange range = ByteRange.parse(exchange.getRequestHeaders().getFirst(Headers.RANGE));
/* 281 */             if (range != null && range.getRanges() == 1 && resource.getContentLength() != null) {
/* 282 */               rangeResponse = range.getResponseResult(resource.getContentLength().longValue(), exchange.getRequestHeaders().getFirst(Headers.IF_RANGE), resource.getLastModified(), (resource.getETag() == null) ? null : resource.getETag().getTag());
/* 283 */               if (rangeResponse != null) {
/* 284 */                 start = rangeResponse.getStart();
/* 285 */                 end = rangeResponse.getEnd();
/* 286 */                 exchange.setStatusCode(rangeResponse.getStatusCode());
/* 287 */                 exchange.getResponseHeaders().put(Headers.CONTENT_RANGE, rangeResponse.getContentRange());
/* 288 */                 long length = rangeResponse.getContentLength();
/* 289 */                 exchange.setResponseContentLength(length);
/* 290 */                 if (rangeResponse.getStatusCode() == 416) {
/*     */                   return;
/*     */                 }
/*     */               } 
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/* 298 */           if (!exchange.getResponseHeaders().contains(Headers.CONTENT_TYPE)) {
/* 299 */             String contentType = resource.getContentType(ResourceHandler.this.mimeMappings);
/* 300 */             if (contentType != null) {
/* 301 */               exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
/*     */             } else {
/* 303 */               exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream");
/*     */             } 
/*     */           } 
/* 306 */           if (lastModified != null) {
/* 307 */             exchange.getResponseHeaders().put(Headers.LAST_MODIFIED, resource.getLastModifiedString());
/*     */           }
/* 309 */           if (etag != null) {
/* 310 */             exchange.getResponseHeaders().put(Headers.ETAG, etag.toString());
/*     */           }
/*     */           
/* 313 */           if (contentEncodedResourceManager != null) {
/*     */             try {
/* 315 */               ContentEncodedResource encoded = contentEncodedResourceManager.getResource(resource, exchange);
/* 316 */               if (encoded != null) {
/* 317 */                 exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, encoded.getContentEncoding());
/* 318 */                 exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, encoded.getResource().getContentLength().longValue());
/* 319 */                 encoded.getResource().serve(exchange.getResponseSender(), exchange, IoCallback.END_EXCHANGE);
/*     */                 
/*     */                 return;
/*     */               } 
/* 323 */             } catch (IOException e) {
/*     */               
/* 325 */               UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 326 */               exchange.setStatusCode(500);
/* 327 */               exchange.endExchange();
/*     */               
/*     */               return;
/*     */             } 
/*     */           }
/* 332 */           if (!sendContent) {
/* 333 */             exchange.endExchange();
/* 334 */           } else if (rangeResponse != null) {
/* 335 */             ((RangeAwareResource)resource).serveRange(exchange.getResponseSender(), exchange, start, end, IoCallback.END_EXCHANGE);
/*     */           } else {
/* 337 */             resource.serve(exchange.getResponseSender(), exchange, IoCallback.END_EXCHANGE);
/*     */           } 
/*     */         }
/*     */       };
/* 341 */     if (exchange.isInIoThread()) {
/* 342 */       exchange.dispatch(dispatchTask);
/*     */     } else {
/* 344 */       dispatchTask.handleRequest(exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void clearCacheHeaders(HttpServerExchange exchange) {
/* 349 */     exchange.getResponseHeaders().remove(Headers.CACHE_CONTROL);
/* 350 */     exchange.getResponseHeaders().remove(Headers.EXPIRES);
/*     */   }
/*     */   
/*     */   private Resource getIndexFiles(HttpServerExchange exchange, ResourceSupplier resourceManager, String base, List<String> possible) throws IOException {
/*     */     String realBase;
/* 355 */     if (base.endsWith("/")) {
/* 356 */       realBase = base;
/*     */     } else {
/* 358 */       realBase = base + "/";
/*     */     } 
/* 360 */     for (String possibility : possible) {
/* 361 */       Resource index = resourceManager.getResource(exchange, canonicalize(realBase + possibility));
/* 362 */       if (index != null) {
/* 363 */         return index;
/*     */       }
/*     */     } 
/* 366 */     return null;
/*     */   }
/*     */   
/*     */   private String canonicalize(String s) {
/* 370 */     if (this.canonicalizePaths) {
/* 371 */       return CanonicalPathUtils.canonicalize(s);
/*     */     }
/* 373 */     return s;
/*     */   }
/*     */   
/*     */   public boolean isDirectoryListingEnabled() {
/* 377 */     return this.directoryListingEnabled;
/*     */   }
/*     */   
/*     */   public ResourceHandler setDirectoryListingEnabled(boolean directoryListingEnabled) {
/* 381 */     this.directoryListingEnabled = directoryListingEnabled;
/* 382 */     return this;
/*     */   }
/*     */   
/*     */   public ResourceHandler addWelcomeFiles(String... files) {
/* 386 */     this.welcomeFiles.addAll(Arrays.asList(files));
/* 387 */     return this;
/*     */   }
/*     */   
/*     */   public ResourceHandler setWelcomeFiles(String... files) {
/* 391 */     this.welcomeFiles.clear();
/* 392 */     this.welcomeFiles.addAll(Arrays.asList(files));
/* 393 */     return this;
/*     */   }
/*     */   
/*     */   public MimeMappings getMimeMappings() {
/* 397 */     return this.mimeMappings;
/*     */   }
/*     */   
/*     */   public ResourceHandler setMimeMappings(MimeMappings mimeMappings) {
/* 401 */     this.mimeMappings = mimeMappings;
/* 402 */     return this;
/*     */   }
/*     */   
/*     */   public Predicate getCachable() {
/* 406 */     return this.cachable;
/*     */   }
/*     */   
/*     */   public ResourceHandler setCachable(Predicate cachable) {
/* 410 */     this.cachable = cachable;
/* 411 */     return this;
/*     */   }
/*     */   
/*     */   public Predicate getAllowed() {
/* 415 */     return this.allowed;
/*     */   }
/*     */   
/*     */   public ResourceHandler setAllowed(Predicate allowed) {
/* 419 */     this.allowed = allowed;
/* 420 */     return this;
/*     */   }
/*     */   
/*     */   public ResourceSupplier getResourceSupplier() {
/* 424 */     return this.resourceSupplier;
/*     */   }
/*     */   
/*     */   public ResourceHandler setResourceSupplier(ResourceSupplier resourceSupplier) {
/* 428 */     this.resourceSupplier = resourceSupplier;
/* 429 */     this.resourceManager = null;
/* 430 */     return this;
/*     */   }
/*     */   
/*     */   public ResourceManager getResourceManager() {
/* 434 */     return this.resourceManager;
/*     */   }
/*     */   
/*     */   public ResourceHandler setResourceManager(ResourceManager resourceManager) {
/* 438 */     this.resourceManager = resourceManager;
/* 439 */     this.resourceSupplier = new DefaultResourceSupplier(resourceManager);
/* 440 */     return this;
/*     */   }
/*     */   
/*     */   public Integer getCacheTime() {
/* 444 */     return this.cacheTime;
/*     */   }
/*     */   
/*     */   public ResourceHandler setCacheTime(Integer cacheTime) {
/* 448 */     this.cacheTime = cacheTime;
/* 449 */     return this;
/*     */   }
/*     */   
/*     */   public ContentEncodedResourceManager getContentEncodedResourceManager() {
/* 453 */     return this.contentEncodedResourceManager;
/*     */   }
/*     */   
/*     */   public ResourceHandler setContentEncodedResourceManager(ContentEncodedResourceManager contentEncodedResourceManager) {
/* 457 */     this.contentEncodedResourceManager = contentEncodedResourceManager;
/* 458 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isCanonicalizePaths() {
/* 462 */     return this.canonicalizePaths;
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
/*     */   public void setCanonicalizePaths(boolean canonicalizePaths) {
/* 474 */     this.canonicalizePaths = canonicalizePaths;
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 481 */       return "resource";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 486 */       Map<String, Class<?>> params = new HashMap<>();
/* 487 */       params.put("location", String.class);
/* 488 */       params.put("allow-listing", boolean.class);
/* 489 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 494 */       return Collections.singleton("location");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 499 */       return "location";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 504 */       return new ResourceHandler.Wrapper((String)config.get("location"), ((Boolean)config.get("allow-listing")).booleanValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final String location;
/*     */     private final boolean allowDirectoryListing;
/*     */     
/*     */     private Wrapper(String location, boolean allowDirectoryListing) {
/* 515 */       this.location = location;
/* 516 */       this.allowDirectoryListing = allowDirectoryListing;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 521 */       ResourceManager rm = new PathResourceManager(Paths.get(this.location, new String[0]), 1024L);
/* 522 */       ResourceHandler resourceHandler = new ResourceHandler(rm);
/* 523 */       resourceHandler.setDirectoryListingEnabled(this.allowDirectoryListing);
/* 524 */       return resourceHandler;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\ResourceHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */