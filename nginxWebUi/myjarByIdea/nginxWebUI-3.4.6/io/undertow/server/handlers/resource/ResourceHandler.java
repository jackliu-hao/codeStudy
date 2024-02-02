package io.undertow.server.handlers.resource;

import io.undertow.UndertowLogger;
import io.undertow.io.IoCallback;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.server.handlers.cache.ResponseCache;
import io.undertow.server.handlers.encoding.ContentEncodedResource;
import io.undertow.server.handlers.encoding.ContentEncodedResourceManager;
import io.undertow.util.ByteRange;
import io.undertow.util.CanonicalPathUtils;
import io.undertow.util.DateUtils;
import io.undertow.util.ETag;
import io.undertow.util.ETagUtils;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.MimeMappings;
import io.undertow.util.RedirectBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class ResourceHandler implements HttpHandler {
   private static final Set<HttpString> KNOWN_METHODS = new HashSet();
   private final List<String> welcomeFiles;
   private volatile boolean directoryListingEnabled;
   private volatile boolean canonicalizePaths;
   private volatile MimeMappings mimeMappings;
   private volatile Predicate cachable;
   private volatile Predicate allowed;
   private volatile ResourceSupplier resourceSupplier;
   private volatile ResourceManager resourceManager;
   private volatile Integer cacheTime;
   private volatile ContentEncodedResourceManager contentEncodedResourceManager;
   private final HttpHandler next;

   public ResourceHandler(ResourceManager resourceSupplier) {
      this((ResourceManager)resourceSupplier, ResponseCodeHandler.HANDLE_404);
   }

   public ResourceHandler(ResourceManager resourceManager, HttpHandler next) {
      this.welcomeFiles = new CopyOnWriteArrayList(new String[]{"index.html", "index.htm", "default.html", "default.htm"});
      this.directoryListingEnabled = false;
      this.canonicalizePaths = true;
      this.mimeMappings = MimeMappings.DEFAULT;
      this.cachable = Predicates.truePredicate();
      this.allowed = Predicates.truePredicate();
      this.resourceSupplier = new DefaultResourceSupplier(resourceManager);
      this.resourceManager = resourceManager;
      this.next = next;
   }

   public ResourceHandler(ResourceSupplier resourceSupplier) {
      this((ResourceSupplier)resourceSupplier, ResponseCodeHandler.HANDLE_404);
   }

   public ResourceHandler(ResourceSupplier resourceManager, HttpHandler next) {
      this.welcomeFiles = new CopyOnWriteArrayList(new String[]{"index.html", "index.htm", "default.html", "default.htm"});
      this.directoryListingEnabled = false;
      this.canonicalizePaths = true;
      this.mimeMappings = MimeMappings.DEFAULT;
      this.cachable = Predicates.truePredicate();
      this.allowed = Predicates.truePredicate();
      this.resourceSupplier = resourceManager;
      this.next = next;
   }

   /** @deprecated */
   @Deprecated
   public ResourceHandler() {
      this.welcomeFiles = new CopyOnWriteArrayList(new String[]{"index.html", "index.htm", "default.html", "default.htm"});
      this.directoryListingEnabled = false;
      this.canonicalizePaths = true;
      this.mimeMappings = MimeMappings.DEFAULT;
      this.cachable = Predicates.truePredicate();
      this.allowed = Predicates.truePredicate();
      this.next = ResponseCodeHandler.HANDLE_404;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (!exchange.getRequestMethod().equals(Methods.GET) && !exchange.getRequestMethod().equals(Methods.POST)) {
         if (exchange.getRequestMethod().equals(Methods.HEAD)) {
            this.serveResource(exchange, false);
         } else {
            if (KNOWN_METHODS.contains(exchange.getRequestMethod())) {
               exchange.setStatusCode(405);
               exchange.getResponseHeaders().add(Headers.ALLOW, String.join(", ", "GET", "HEAD", "POST"));
            } else {
               exchange.setStatusCode(501);
            }

            exchange.endExchange();
         }
      } else {
         this.serveResource(exchange, true);
      }

   }

   private void serveResource(HttpServerExchange exchange, final boolean sendContent) throws Exception {
      if (!DirectoryUtils.sendRequestedBlobs(exchange)) {
         if (!this.allowed.resolve(exchange)) {
            exchange.setStatusCode(403);
            exchange.endExchange();
         } else {
            ResponseCache cache = (ResponseCache)exchange.getAttachment(ResponseCache.ATTACHMENT_KEY);
            boolean cachable = this.cachable.resolve(exchange);
            if (cachable && this.cacheTime != null) {
               exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, "public, max-age=" + this.cacheTime);
               long date = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis((long)this.cacheTime);
               String dateHeader = DateUtils.toDateString(new Date(date));
               exchange.getResponseHeaders().put(Headers.EXPIRES, dateHeader);
            }

            if (cache == null || !cachable || !cache.tryServeResponse()) {
               HttpHandler dispatchTask = new HttpHandler() {
                  public void handleRequest(HttpServerExchange exchange) throws Exception {
                     Resource resource = null;

                     try {
                        if (File.separatorChar == '/' || !exchange.getRelativePath().contains(File.separator)) {
                           resource = ResourceHandler.this.resourceSupplier.getResource(exchange, ResourceHandler.this.canonicalize(exchange.getRelativePath()));
                        }
                     } catch (IOException var17) {
                        ResourceHandler.this.clearCacheHeaders(exchange);
                        UndertowLogger.REQUEST_IO_LOGGER.ioException(var17);
                        exchange.setStatusCode(500);
                        exchange.endExchange();
                        return;
                     }

                     if (resource == null) {
                        ResourceHandler.this.clearCacheHeaders(exchange);
                        ResourceHandler.this.next.handleRequest(exchange);
                     } else {
                        if (resource.isDirectory()) {
                           Resource indexResource;
                           try {
                              indexResource = ResourceHandler.this.getIndexFiles(exchange, ResourceHandler.this.resourceSupplier, resource.getPath(), ResourceHandler.this.welcomeFiles);
                           } catch (IOException var16) {
                              UndertowLogger.REQUEST_IO_LOGGER.ioException(var16);
                              exchange.setStatusCode(500);
                              exchange.endExchange();
                              return;
                           }

                           if (indexResource == null) {
                              if (ResourceHandler.this.directoryListingEnabled) {
                                 DirectoryUtils.renderDirectoryListing(exchange, resource);
                                 return;
                              }

                              exchange.setStatusCode(403);
                              exchange.endExchange();
                              return;
                           }

                           if (!exchange.getRequestPath().endsWith("/")) {
                              exchange.setStatusCode(302);
                              exchange.getResponseHeaders().put(Headers.LOCATION, RedirectBuilder.redirect(exchange, exchange.getRelativePath() + "/", true));
                              exchange.endExchange();
                              return;
                           }

                           resource = indexResource;
                        } else if (exchange.getRelativePath().endsWith("/")) {
                           exchange.setStatusCode(404);
                           exchange.endExchange();
                           return;
                        }

                        ETag etag = resource.getETag();
                        Date lastModified = resource.getLastModified();
                        if (ETagUtils.handleIfMatch(exchange, etag, false) && DateUtils.handleIfUnmodifiedSince(exchange, lastModified)) {
                           if (ETagUtils.handleIfNoneMatch(exchange, etag, true) && DateUtils.handleIfModifiedSince(exchange, lastModified)) {
                              ContentEncodedResourceManager contentEncodedResourceManager = ResourceHandler.this.contentEncodedResourceManager;
                              Long contentLength = resource.getContentLength();
                              if (contentLength != null && !exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
                                 exchange.setResponseContentLength(contentLength);
                              }

                              ByteRange.RangeResponseResult rangeResponse = null;
                              long start = -1L;
                              long end = -1L;
                              if (resource instanceof RangeAwareResource && ((RangeAwareResource)resource).isRangeSupported() && contentLength != null && contentEncodedResourceManager == null) {
                                 exchange.getResponseHeaders().put(Headers.ACCEPT_RANGES, "bytes");
                                 ByteRange range = ByteRange.parse(exchange.getRequestHeaders().getFirst(Headers.RANGE));
                                 if (range != null && range.getRanges() == 1 && resource.getContentLength() != null) {
                                    rangeResponse = range.getResponseResult(resource.getContentLength(), exchange.getRequestHeaders().getFirst(Headers.IF_RANGE), resource.getLastModified(), resource.getETag() == null ? null : resource.getETag().getTag());
                                    if (rangeResponse != null) {
                                       start = rangeResponse.getStart();
                                       end = rangeResponse.getEnd();
                                       exchange.setStatusCode(rangeResponse.getStatusCode());
                                       exchange.getResponseHeaders().put(Headers.CONTENT_RANGE, rangeResponse.getContentRange());
                                       long length = rangeResponse.getContentLength();
                                       exchange.setResponseContentLength(length);
                                       if (rangeResponse.getStatusCode() == 416) {
                                          return;
                                       }
                                    }
                                 }
                              }

                              if (!exchange.getResponseHeaders().contains(Headers.CONTENT_TYPE)) {
                                 String contentType = resource.getContentType(ResourceHandler.this.mimeMappings);
                                 if (contentType != null) {
                                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
                                 } else {
                                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream");
                                 }
                              }

                              if (lastModified != null) {
                                 exchange.getResponseHeaders().put(Headers.LAST_MODIFIED, resource.getLastModifiedString());
                              }

                              if (etag != null) {
                                 exchange.getResponseHeaders().put(Headers.ETAG, etag.toString());
                              }

                              if (contentEncodedResourceManager != null) {
                                 try {
                                    ContentEncodedResource encoded = contentEncodedResourceManager.getResource(resource, exchange);
                                    if (encoded != null) {
                                       exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, encoded.getContentEncoding());
                                       exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, encoded.getResource().getContentLength());
                                       encoded.getResource().serve(exchange.getResponseSender(), exchange, IoCallback.END_EXCHANGE);
                                       return;
                                    }
                                 } catch (IOException var15) {
                                    UndertowLogger.REQUEST_IO_LOGGER.ioException(var15);
                                    exchange.setStatusCode(500);
                                    exchange.endExchange();
                                    return;
                                 }
                              }

                              if (!sendContent) {
                                 exchange.endExchange();
                              } else if (rangeResponse != null) {
                                 ((RangeAwareResource)resource).serveRange(exchange.getResponseSender(), exchange, start, end, IoCallback.END_EXCHANGE);
                              } else {
                                 resource.serve(exchange.getResponseSender(), exchange, IoCallback.END_EXCHANGE);
                              }

                           } else {
                              exchange.setStatusCode(304);
                              exchange.endExchange();
                           }
                        } else {
                           exchange.setStatusCode(412);
                           exchange.endExchange();
                        }
                     }
                  }
               };
               if (exchange.isInIoThread()) {
                  exchange.dispatch(dispatchTask);
               } else {
                  dispatchTask.handleRequest(exchange);
               }

            }
         }
      }
   }

   private void clearCacheHeaders(HttpServerExchange exchange) {
      exchange.getResponseHeaders().remove(Headers.CACHE_CONTROL);
      exchange.getResponseHeaders().remove(Headers.EXPIRES);
   }

   private Resource getIndexFiles(HttpServerExchange exchange, ResourceSupplier resourceManager, String base, List<String> possible) throws IOException {
      String realBase;
      if (base.endsWith("/")) {
         realBase = base;
      } else {
         realBase = base + "/";
      }

      Iterator var6 = possible.iterator();

      Resource index;
      do {
         if (!var6.hasNext()) {
            return null;
         }

         String possibility = (String)var6.next();
         index = resourceManager.getResource(exchange, this.canonicalize(realBase + possibility));
      } while(index == null);

      return index;
   }

   private String canonicalize(String s) {
      return this.canonicalizePaths ? CanonicalPathUtils.canonicalize(s) : s;
   }

   public boolean isDirectoryListingEnabled() {
      return this.directoryListingEnabled;
   }

   public ResourceHandler setDirectoryListingEnabled(boolean directoryListingEnabled) {
      this.directoryListingEnabled = directoryListingEnabled;
      return this;
   }

   public ResourceHandler addWelcomeFiles(String... files) {
      this.welcomeFiles.addAll(Arrays.asList(files));
      return this;
   }

   public ResourceHandler setWelcomeFiles(String... files) {
      this.welcomeFiles.clear();
      this.welcomeFiles.addAll(Arrays.asList(files));
      return this;
   }

   public MimeMappings getMimeMappings() {
      return this.mimeMappings;
   }

   public ResourceHandler setMimeMappings(MimeMappings mimeMappings) {
      this.mimeMappings = mimeMappings;
      return this;
   }

   public Predicate getCachable() {
      return this.cachable;
   }

   public ResourceHandler setCachable(Predicate cachable) {
      this.cachable = cachable;
      return this;
   }

   public Predicate getAllowed() {
      return this.allowed;
   }

   public ResourceHandler setAllowed(Predicate allowed) {
      this.allowed = allowed;
      return this;
   }

   public ResourceSupplier getResourceSupplier() {
      return this.resourceSupplier;
   }

   public ResourceHandler setResourceSupplier(ResourceSupplier resourceSupplier) {
      this.resourceSupplier = resourceSupplier;
      this.resourceManager = null;
      return this;
   }

   public ResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourceHandler setResourceManager(ResourceManager resourceManager) {
      this.resourceManager = resourceManager;
      this.resourceSupplier = new DefaultResourceSupplier(resourceManager);
      return this;
   }

   public Integer getCacheTime() {
      return this.cacheTime;
   }

   public ResourceHandler setCacheTime(Integer cacheTime) {
      this.cacheTime = cacheTime;
      return this;
   }

   public ContentEncodedResourceManager getContentEncodedResourceManager() {
      return this.contentEncodedResourceManager;
   }

   public ResourceHandler setContentEncodedResourceManager(ContentEncodedResourceManager contentEncodedResourceManager) {
      this.contentEncodedResourceManager = contentEncodedResourceManager;
      return this;
   }

   public boolean isCanonicalizePaths() {
      return this.canonicalizePaths;
   }

   public void setCanonicalizePaths(boolean canonicalizePaths) {
      this.canonicalizePaths = canonicalizePaths;
   }

   static {
      KNOWN_METHODS.add(Methods.OPTIONS);
      KNOWN_METHODS.add(Methods.GET);
      KNOWN_METHODS.add(Methods.HEAD);
      KNOWN_METHODS.add(Methods.POST);
      KNOWN_METHODS.add(Methods.PUT);
      KNOWN_METHODS.add(Methods.DELETE);
      KNOWN_METHODS.add(Methods.TRACE);
      KNOWN_METHODS.add(Methods.CONNECT);
   }

   private static class Wrapper implements HandlerWrapper {
      private final String location;
      private final boolean allowDirectoryListing;

      private Wrapper(String location, boolean allowDirectoryListing) {
         this.location = location;
         this.allowDirectoryListing = allowDirectoryListing;
      }

      public HttpHandler wrap(HttpHandler handler) {
         ResourceManager rm = new PathResourceManager(Paths.get(this.location), 1024L);
         ResourceHandler resourceHandler = new ResourceHandler(rm);
         resourceHandler.setDirectoryListingEnabled(this.allowDirectoryListing);
         return resourceHandler;
      }

      // $FF: synthetic method
      Wrapper(String x0, boolean x1, Object x2) {
         this(x0, x1);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "resource";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("location", String.class);
         params.put("allow-listing", Boolean.TYPE);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("location");
      }

      public String defaultParameter() {
         return "location";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((String)config.get("location"), (Boolean)config.get("allow-listing"));
      }
   }
}
