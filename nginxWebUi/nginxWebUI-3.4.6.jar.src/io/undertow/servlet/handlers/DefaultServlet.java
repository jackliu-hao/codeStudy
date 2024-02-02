/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.resource.DefaultResourceSupplier;
/*     */ import io.undertow.server.handlers.resource.DirectoryUtils;
/*     */ import io.undertow.server.handlers.resource.PreCompressedResourceSupplier;
/*     */ import io.undertow.server.handlers.resource.RangeAwareResource;
/*     */ import io.undertow.server.handlers.resource.Resource;
/*     */ import io.undertow.server.handlers.resource.ResourceSupplier;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.api.DefaultServletConfig;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import io.undertow.util.ByteRange;
/*     */ import io.undertow.util.CanonicalPathUtils;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.ETagUtils;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultServlet
/*     */   extends HttpServlet
/*     */ {
/*     */   public static final String DIRECTORY_LISTING = "directory-listing";
/*     */   public static final String DEFAULT_ALLOWED = "default-allowed";
/*     */   public static final String ALLOWED_EXTENSIONS = "allowed-extensions";
/*     */   public static final String DISALLOWED_EXTENSIONS = "disallowed-extensions";
/*     */   public static final String RESOLVE_AGAINST_CONTEXT_ROOT = "resolve-against-context-root";
/*     */   public static final String ALLOW_POST = "allow-post";
/*  86 */   private static final Set<String> DEFAULT_ALLOWED_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "js", "css", "png", "jpg", "gif", "html", "htm", "txt", "pdf", "jpeg", "xml" })));
/*     */   
/*     */   private Deployment deployment;
/*     */   
/*     */   private ResourceSupplier resourceSupplier;
/*     */   
/*     */   private boolean directoryListingEnabled = false;
/*     */   private boolean defaultAllowed = true;
/*  94 */   private Set<String> allowed = DEFAULT_ALLOWED_EXTENSIONS;
/*  95 */   private Set<String> disallowed = Collections.emptySet();
/*     */   
/*     */   private boolean resolveAgainstContextRoot;
/*     */   private boolean allowPost = false;
/*     */   
/*     */   public void init(ServletConfig config) throws ServletException {
/* 101 */     super.init(config);
/* 102 */     ServletContextImpl sc = (ServletContextImpl)config.getServletContext();
/* 103 */     this.deployment = sc.getDeployment();
/* 104 */     DefaultServletConfig defaultServletConfig = this.deployment.getDeploymentInfo().getDefaultServletConfig();
/* 105 */     if (defaultServletConfig != null) {
/* 106 */       this.defaultAllowed = defaultServletConfig.isDefaultAllowed();
/* 107 */       this.allowed = new HashSet<>();
/* 108 */       if (defaultServletConfig.getAllowed() != null) {
/* 109 */         this.allowed.addAll(defaultServletConfig.getAllowed());
/*     */       }
/* 111 */       this.disallowed = new HashSet<>();
/* 112 */       if (defaultServletConfig.getDisallowed() != null) {
/* 113 */         this.disallowed.addAll(defaultServletConfig.getDisallowed());
/*     */       }
/*     */     } 
/* 116 */     if (config.getInitParameter("default-allowed") != null) {
/* 117 */       this.defaultAllowed = Boolean.parseBoolean(config.getInitParameter("default-allowed"));
/*     */     }
/* 119 */     if (config.getInitParameter("allowed-extensions") != null) {
/* 120 */       String extensions = config.getInitParameter("allowed-extensions");
/* 121 */       this.allowed = new HashSet<>(Arrays.asList(extensions.split(",")));
/*     */     } 
/* 123 */     if (config.getInitParameter("disallowed-extensions") != null) {
/* 124 */       String extensions = config.getInitParameter("disallowed-extensions");
/* 125 */       this.disallowed = new HashSet<>(Arrays.asList(extensions.split(",")));
/*     */     } 
/* 127 */     if (config.getInitParameter("resolve-against-context-root") != null) {
/* 128 */       this.resolveAgainstContextRoot = Boolean.parseBoolean(config.getInitParameter("resolve-against-context-root"));
/*     */     }
/* 130 */     if (config.getInitParameter("allow-post") != null) {
/* 131 */       this.allowPost = Boolean.parseBoolean(config.getInitParameter("allow-post"));
/*     */     }
/* 133 */     if (this.deployment.getDeploymentInfo().getPreCompressedResources().isEmpty()) {
/* 134 */       this.resourceSupplier = (ResourceSupplier)new DefaultResourceSupplier(this.deployment.getDeploymentInfo().getResourceManager());
/*     */     } else {
/* 136 */       PreCompressedResourceSupplier preCompressedResourceSupplier = new PreCompressedResourceSupplier(this.deployment.getDeploymentInfo().getResourceManager());
/* 137 */       for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)this.deployment.getDeploymentInfo().getPreCompressedResources().entrySet()) {
/* 138 */         preCompressedResourceSupplier.addEncoding(entry.getKey(), entry.getValue());
/*     */       }
/* 140 */       this.resourceSupplier = (ResourceSupplier)preCompressedResourceSupplier;
/*     */     } 
/* 142 */     String listings = config.getInitParameter("directory-listing");
/* 143 */     if (Boolean.valueOf(listings).booleanValue()) {
/* 144 */       this.directoryListingEnabled = true;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/*     */     Resource resource;
/* 150 */     String path = getPath(req);
/* 151 */     if (!isAllowed(path, req.getDispatcherType())) {
/* 152 */       resp.sendError(404);
/*     */       return;
/*     */     } 
/* 155 */     if (File.separatorChar != '/')
/*     */     {
/* 157 */       path = CanonicalPathUtils.canonicalize(path.replace(File.separatorChar, '/'));
/*     */     }
/*     */     
/* 160 */     HttpServerExchange exchange = SecurityActions.requireCurrentServletRequestContext().getOriginalRequest().getExchange();
/*     */ 
/*     */     
/* 163 */     if (File.separatorChar == '/' || !path.contains(File.separator)) {
/* 164 */       resource = this.resourceSupplier.getResource(exchange, path);
/*     */     } else {
/* 166 */       resource = null;
/*     */     } 
/*     */     
/* 169 */     if (resource == null) {
/* 170 */       if (req.getDispatcherType() == DispatcherType.INCLUDE) {
/*     */         
/* 172 */         UndertowServletLogger.REQUEST_LOGGER.requestedResourceDoesNotExistForIncludeMethod(path);
/* 173 */         throw new FileNotFoundException(path);
/*     */       } 
/* 175 */       resp.sendError(404);
/*     */       return;
/*     */     } 
/* 178 */     if (resource.isDirectory()) {
/* 179 */       if ("css".equals(req.getQueryString())) {
/* 180 */         resp.setContentType("text/css");
/* 181 */         resp.getWriter().write("body {\n    font-family: \"Lucida Grande\", \"Lucida Sans Unicode\", \"Trebuchet MS\", Helvetica, Arial, Verdana, sans-serif;\n    margin: 5px;\n}\n\nth.loc {\n    background-image: linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -o-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -moz-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -webkit-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -ms-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    \n    background-image: -webkit-gradient(\n        linear,\n        left bottom,\n        left top,\n        color-stop(0.08, rgb(153,151,153)),\n        color-stop(0.54, rgb(199,199,199))\n    );\n    color: black;\n    padding: 2px;\n    font-weight: normal;\n    border: solid 1px;\n    font-size: 150%;\n    text-align: left;\n}\n\nth.label {\n    border: solid  1px;\n    text-align: left;\n    padding: 4px;\n    padding-left: 8px;\n    font-weight: normal;\n    font-size: small;\n    background-color: #e8e8e8;\n}\n\nth.offset {\n    padding-left: 32px;\n}\n\nth.footer {\n    font-size: 75%;\n    text-align: right;\n}\n\na.icon {\n    padding-left: 24px;\n    text-decoration: none;\n    color: black;\n}\n\na.icon:hover {\n    text-decoration: underline;\n}\n\ntable {\n    border: 1px solid;\n    border-spacing: 0px;\n    width: 100%;\n    border-collapse: collapse;\n}\n\ntr.odd {\n    background-color: #f3f6fa;\n}\n\ntr.odd td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.even {\n    background-color: #ffffff;\n}\n\ntr.even td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.eveninvis td {\n    color: #ffffff;\n}\n\ntr.oddinvis td {\n    color: #f3f6fa\n}\n\na.up {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABI0lEQVQ4y2P4//8/Ay7sM4nhPwjjUwMm0ua//Y+M0+e//QrSGDAfgvEZAjdgydHXcAzTXLjWDoxhhqBbhGLA1N0vwBhdM7ohMHVwA8yrzn4zLj/936j8FE7N6IaA1IL0gPQy2DVc+rnp3FeCmtENAekB6WXw7Lz1tWD5x/+wEIdhdI3o8iA9IL0MYZMfvq9a9+V/w+avcIzLAGQ1ID0gvQxJc56/aNn29X/vnm9wjMsAZDWtQD0gvQwFy94+6N37/f/Moz/gGJcByGpAekB6GarXf7427ciP/0vP/YRjdP/CMLIakB6QXobKDd9PN+769b91P2kYpAekl2HJhb8r11/583/9ZRIxUM+8U783MQCBGBDXAHEbibgGrBdfTiMGU2wAAPz+nxp+TnhDAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.dir {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAAA+UlEQVQ4jWP4//8/AyUYTKTNf/sfGafPf/s1be47G5IMWHL0NRxP2f3mbcaCtz/RDUbHKAZM3f2CJAw3wLzq7Dfj8tP/jcpPkYRBekB6GewaLv3cdO7r/y0XSMMgPSC9DJ6dt74WLP/4v3TVZ5IwSA9IL0PY5Ifvq9Z9+d+w+StJGKQHpJchac7zFy3bvv7v3fONJNwK1APSy5C/7O2D3r3f/888+oMkDNID0stQvf7ztWlHfvxfeu4nSRikB6SXoXLD99ONu379b91PGgbpAellWHLh38r1V/78X3+ZRAzUM/fUr00MQCAGxDVA3EYirgHrpUpupAQDAPs+7c1tGDnPAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.file {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAABM0lEQVQ4y5WSTW6DMBCF3xvzc4wuOEIO0kVAuUB7vJ4g3KBdoHSRROomEpusUaoAcaYLfmKoqVRLIxnJ7/M3YwJVBcknACv8b+1U9SvoP1bXa/3WNDVIAQmQBLsNOEsGQYAwDNcARgDqusbl+wIRA2NkBEyqP0s+kCOAQhhjICJdkaDIJDwEvQAhH+G+SHagWTsi4jHoAWYIOxYDZDjnb8Fn4Akvz6AHcAbx3Tp5ETwI3RwckyVtv4Fr4VEe9qq6bDB5tlnYWou2bWGtRRRF6jdwAm5Za1FVFc7nM0QERVG8A9hPDRaGpapomgZlWSJJEuR5ftpsNq8ADr9amC+SuN/vuN1uIIntdnvKsuwZwKf2wxgBxpjpX+dA4jjW4/H4kabpixt2AbvAmDX+XnsAB509ww+A8mAar+XXgQAAAABJRU5ErkJggg==') left center no-repeat;\n}"); return;
/*     */       } 
/* 183 */       if ("js".equals(req.getQueryString())) {
/* 184 */         resp.setContentType("application/javascript");
/* 185 */         resp.getWriter().write("function growit() {\n    var table = document.getElementById(\"thetable\");\n\n    var i = table.rows.length - 1;\n    while (i-- > 0) {\n        if (table.rows[i].id == \"eraseme\") {\n            table.deleteRow(i);\n        } else {\n            break;\n        }\n    }\n    table.style.height=\"\";\n    var i = 0;\n    while (table.offsetHeight < window.innerHeight - 24) {\n        i++;\n        var tbody = table.tBodies[0];\n        var row = tbody.insertRow(tbody.rows.length);\n        row.id=\"eraseme\";\n        var cell = row.insertCell(0);\n        if (table.rows.length % 2 != 0) {\n            row.className=\"even eveninvis\";\n        } else {\n            row.className=\"odd oddinvis\";\n        }\n\n        cell.colSpan=3;\n        cell.appendChild(document.createTextNode(\"i\"));\n    }\n    table.style.height=\"100%\";\n    if (i > 0) {\n        document.documentElement.style.overflowY=\"hidden\";\n    } else {\n        document.documentElement.style.overflowY=\"auto\";\n    }\n}");
/*     */         return;
/*     */       } 
/* 188 */       if (this.directoryListingEnabled) {
/* 189 */         resp.setContentType("text/html");
/* 190 */         StringBuilder output = DirectoryUtils.renderDirectoryListing(req.getRequestURI(), resource);
/* 191 */         resp.getWriter().write(output.toString());
/*     */       } else {
/* 193 */         resp.sendError(403);
/*     */       } 
/*     */     } else {
/* 196 */       if (path.endsWith("/")) {
/*     */         
/* 198 */         resp.sendError(404);
/*     */         return;
/*     */       } 
/* 201 */       serveFileBlocking(req, resp, resource, exchange);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 207 */     if (this.allowPost) {
/* 208 */       doGet(req, resp);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 213 */       switch (req.getDispatcherType()) {
/*     */         case INCLUDE:
/*     */         case FORWARD:
/*     */         case ERROR:
/* 217 */           doGet(req, resp);
/*     */           return;
/*     */       } 
/* 220 */       super.doPost(req, resp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 227 */     switch (req.getDispatcherType()) {
/*     */       case INCLUDE:
/*     */       case FORWARD:
/*     */       case ERROR:
/* 231 */         doGet(req, resp);
/*     */         return;
/*     */     } 
/* 234 */     super.doPut(req, resp);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 240 */     switch (req.getDispatcherType()) {
/*     */       case INCLUDE:
/*     */       case FORWARD:
/*     */       case ERROR:
/* 244 */         doGet(req, resp);
/*     */         return;
/*     */     } 
/* 247 */     super.doDelete(req, resp);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 253 */     switch (req.getDispatcherType()) {
/*     */       case INCLUDE:
/*     */       case FORWARD:
/*     */       case ERROR:
/* 257 */         doGet(req, resp);
/*     */         return;
/*     */     } 
/* 260 */     super.doOptions(req, resp);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 266 */     switch (req.getDispatcherType()) {
/*     */       case INCLUDE:
/*     */       case FORWARD:
/*     */       case ERROR:
/* 270 */         doGet(req, resp);
/*     */         return;
/*     */     } 
/* 273 */     super.doTrace(req, resp);
/*     */   }
/*     */ 
/*     */   
/*     */   private void serveFileBlocking(HttpServletRequest req, HttpServletResponse resp, Resource resource, HttpServerExchange exchange) throws IOException {
/* 278 */     ETag etag = resource.getETag();
/* 279 */     Date lastModified = resource.getLastModified();
/* 280 */     if (req.getDispatcherType() != DispatcherType.INCLUDE) {
/* 281 */       if (!ETagUtils.handleIfMatch(req.getHeader("If-Match"), etag, false) || 
/* 282 */         !DateUtils.handleIfUnmodifiedSince(req.getHeader("If-Unmodified-Since"), lastModified)) {
/* 283 */         resp.setStatus(412);
/*     */         return;
/*     */       } 
/* 286 */       if (!ETagUtils.handleIfNoneMatch(req.getHeader("If-None-Match"), etag, true) || 
/* 287 */         !DateUtils.handleIfModifiedSince(req.getHeader("If-Modified-Since"), lastModified)) {
/* 288 */         if (req.getMethod().equals("GET") || req.getMethod().equals("HEAD")) {
/* 289 */           resp.setStatus(304);
/*     */         } else {
/* 291 */           resp.setStatus(412);
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 298 */     if (resp.getContentType() == null && 
/* 299 */       !resource.isDirectory()) {
/* 300 */       String contentType = this.deployment.getServletContext().getMimeType(resource.getName());
/* 301 */       if (contentType != null) {
/* 302 */         resp.setContentType(contentType);
/*     */       } else {
/* 304 */         resp.setContentType("application/octet-stream");
/*     */       } 
/*     */     } 
/*     */     
/* 308 */     if (lastModified != null) {
/* 309 */       resp.setHeader("Last-Modified", resource.getLastModifiedString());
/*     */     }
/* 311 */     if (etag != null) {
/* 312 */       resp.setHeader("ETag", etag.toString());
/*     */     }
/* 314 */     ByteRange.RangeResponseResult rangeResponse = null;
/* 315 */     long start = -1L, end = -1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 322 */       Long contentLength = resource.getContentLength();
/* 323 */       if (contentLength != null) {
/* 324 */         resp.getOutputStream();
/* 325 */         if (contentLength.longValue() > 2147483647L) {
/* 326 */           resp.setContentLengthLong(contentLength.longValue());
/*     */         } else {
/* 328 */           resp.setContentLength(contentLength.intValue());
/*     */         } 
/* 330 */         if (resource instanceof RangeAwareResource && ((RangeAwareResource)resource).isRangeSupported() && resource.getContentLength() != null) {
/* 331 */           resp.setHeader("Accept-Ranges", "bytes");
/*     */           
/* 333 */           ByteRange range = ByteRange.parse(req.getHeader("Range"));
/* 334 */           if (range != null) {
/* 335 */             rangeResponse = range.getResponseResult(resource.getContentLength().longValue(), req.getHeader("If-Range"), resource.getLastModified(), (resource.getETag() == null) ? null : resource.getETag().getTag());
/* 336 */             if (rangeResponse != null) {
/* 337 */               start = rangeResponse.getStart();
/* 338 */               end = rangeResponse.getEnd();
/* 339 */               resp.setStatus(rangeResponse.getStatusCode());
/* 340 */               resp.setHeader("Content-Range", rangeResponse.getContentRange());
/* 341 */               long length = rangeResponse.getContentLength();
/* 342 */               if (length > 2147483647L) {
/* 343 */                 resp.setContentLengthLong(length);
/*     */               } else {
/* 345 */                 resp.setContentLength((int)length);
/*     */               } 
/* 347 */               if (rangeResponse.getStatusCode() == 416) {
/*     */                 return;
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 354 */     } catch (IllegalStateException illegalStateException) {}
/*     */ 
/*     */     
/* 357 */     boolean include = (req.getDispatcherType() == DispatcherType.INCLUDE);
/* 358 */     if (!req.getMethod().equals("HEAD")) {
/* 359 */       if (rangeResponse == null) {
/* 360 */         resource.serve(exchange.getResponseSender(), exchange, completionCallback(include));
/*     */       } else {
/* 362 */         ((RangeAwareResource)resource).serveRange(exchange.getResponseSender(), exchange, start, end, completionCallback(include));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private IoCallback completionCallback(final boolean include) {
/* 368 */     return new IoCallback()
/*     */       {
/*     */         public void onComplete(HttpServerExchange exchange, Sender sender)
/*     */         {
/* 372 */           if (!include) {
/* 373 */             sender.close();
/*     */           }
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 380 */           sender.close();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String getPath(HttpServletRequest request) {
/*     */     String servletPath, pathInfo;
/* 389 */     if (request.getDispatcherType() == DispatcherType.INCLUDE && request.getAttribute("javax.servlet.include.request_uri") != null) {
/* 390 */       pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
/* 391 */       servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
/*     */     } else {
/* 393 */       pathInfo = request.getPathInfo();
/* 394 */       servletPath = request.getServletPath();
/*     */     } 
/* 396 */     String result = pathInfo;
/* 397 */     if (result == null) {
/* 398 */       result = CanonicalPathUtils.canonicalize(servletPath);
/* 399 */     } else if (this.resolveAgainstContextRoot) {
/* 400 */       result = servletPath + CanonicalPathUtils.canonicalize(pathInfo);
/*     */     } else {
/* 402 */       result = CanonicalPathUtils.canonicalize(result);
/*     */     } 
/* 404 */     if (result == null || result.isEmpty()) {
/* 405 */       result = "/";
/*     */     }
/* 407 */     return result;
/*     */   }
/*     */   
/*     */   private boolean isAllowed(String path, DispatcherType dispatcherType) {
/*     */     String lastSegment;
/* 412 */     if (!path.isEmpty() && 
/* 413 */       dispatcherType == DispatcherType.REQUEST)
/*     */     {
/* 415 */       if (Paths.isForbidden(path)) {
/* 416 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 420 */     if (this.defaultAllowed && this.disallowed.isEmpty()) {
/* 421 */       return true;
/*     */     }
/* 423 */     int pos = path.lastIndexOf('/');
/*     */     
/* 425 */     if (pos == -1) {
/* 426 */       lastSegment = path;
/*     */     } else {
/* 428 */       lastSegment = path.substring(pos + 1);
/*     */     } 
/* 430 */     if (lastSegment.isEmpty()) {
/* 431 */       return true;
/*     */     }
/* 433 */     int ext = lastSegment.lastIndexOf('.');
/* 434 */     if (ext == -1)
/*     */     {
/* 436 */       return true;
/*     */     }
/* 438 */     String extension = lastSegment.substring(ext + 1, lastSegment.length());
/* 439 */     if (this.defaultAllowed) {
/* 440 */       return !this.disallowed.contains(extension);
/*     */     }
/* 442 */     return this.allowed.contains(extension);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\DefaultServlet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */