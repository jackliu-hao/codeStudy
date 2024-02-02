/*     */ package org.apache.http.client.utils;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Stack;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.conn.routing.RouteInfo;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.TextUtils;
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
/*     */ public class URIUtils
/*     */ {
/*     */   public enum UriFlag
/*     */   {
/*  57 */     DROP_FRAGMENT,
/*  58 */     NORMALIZE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   public static final EnumSet<UriFlag> NO_FLAGS = EnumSet.noneOf(UriFlag.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   public static final EnumSet<UriFlag> DROP_FRAGMENT = EnumSet.of(UriFlag.DROP_FRAGMENT);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final EnumSet<UriFlag> NORMALIZE = EnumSet.of(UriFlag.NORMALIZE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   public static final EnumSet<UriFlag> DROP_FRAGMENT_AND_NORMALIZE = EnumSet.of(UriFlag.DROP_FRAGMENT, UriFlag.NORMALIZE);
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
/*     */   @Deprecated
/*     */   public static URI createURI(String scheme, String host, int port, String path, String query, String fragment) throws URISyntaxException {
/* 125 */     StringBuilder buffer = new StringBuilder();
/* 126 */     if (host != null) {
/* 127 */       if (scheme != null) {
/* 128 */         buffer.append(scheme);
/* 129 */         buffer.append("://");
/*     */       } 
/* 131 */       buffer.append(host);
/* 132 */       if (port > 0) {
/* 133 */         buffer.append(':');
/* 134 */         buffer.append(port);
/*     */       } 
/*     */     } 
/* 137 */     if (path == null || !path.startsWith("/")) {
/* 138 */       buffer.append('/');
/*     */     }
/* 140 */     if (path != null) {
/* 141 */       buffer.append(path);
/*     */     }
/* 143 */     if (query != null) {
/* 144 */       buffer.append('?');
/* 145 */       buffer.append(query);
/*     */     } 
/* 147 */     if (fragment != null) {
/* 148 */       buffer.append('#');
/* 149 */       buffer.append(fragment);
/*     */     } 
/* 151 */     return new URI(buffer.toString());
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
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static URI rewriteURI(URI uri, HttpHost target, boolean dropFragment) throws URISyntaxException {
/* 177 */     return rewriteURI(uri, target, dropFragment ? DROP_FRAGMENT : NO_FLAGS);
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
/*     */   
/*     */   public static URI rewriteURI(URI uri, HttpHost target, EnumSet<UriFlag> flags) throws URISyntaxException {
/* 201 */     Args.notNull(uri, "URI");
/* 202 */     Args.notNull(flags, "URI flags");
/* 203 */     if (uri.isOpaque()) {
/* 204 */       return uri;
/*     */     }
/* 206 */     URIBuilder uribuilder = new URIBuilder(uri);
/* 207 */     if (target != null) {
/* 208 */       uribuilder.setScheme(target.getSchemeName());
/* 209 */       uribuilder.setHost(target.getHostName());
/* 210 */       uribuilder.setPort(target.getPort());
/*     */     } else {
/* 212 */       uribuilder.setScheme(null);
/* 213 */       uribuilder.setHost(null);
/* 214 */       uribuilder.setPort(-1);
/*     */     } 
/* 216 */     if (flags.contains(UriFlag.DROP_FRAGMENT)) {
/* 217 */       uribuilder.setFragment(null);
/*     */     }
/* 219 */     if (flags.contains(UriFlag.NORMALIZE)) {
/* 220 */       List<String> originalPathSegments = uribuilder.getPathSegments();
/* 221 */       List<String> pathSegments = new ArrayList<String>(originalPathSegments);
/* 222 */       for (Iterator<String> it = pathSegments.iterator(); it.hasNext(); ) {
/* 223 */         String pathSegment = it.next();
/* 224 */         if (pathSegment.isEmpty() && it.hasNext()) {
/* 225 */           it.remove();
/*     */         }
/*     */       } 
/* 228 */       if (pathSegments.size() != originalPathSegments.size()) {
/* 229 */         uribuilder.setPathSegments(pathSegments);
/*     */       }
/*     */     } 
/* 232 */     if (uribuilder.isPathEmpty()) {
/* 233 */       uribuilder.setPathSegments(new String[] { "" });
/*     */     }
/* 235 */     return uribuilder.build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URI rewriteURI(URI uri, HttpHost target) throws URISyntaxException {
/* 246 */     return rewriteURI(uri, target, NORMALIZE);
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
/*     */   public static URI rewriteURI(URI uri) throws URISyntaxException {
/* 261 */     Args.notNull(uri, "URI");
/* 262 */     if (uri.isOpaque()) {
/* 263 */       return uri;
/*     */     }
/* 265 */     URIBuilder uribuilder = new URIBuilder(uri);
/* 266 */     if (uribuilder.getUserInfo() != null) {
/* 267 */       uribuilder.setUserInfo(null);
/*     */     }
/* 269 */     if (uribuilder.getPathSegments().isEmpty()) {
/* 270 */       uribuilder.setPathSegments(new String[] { "" });
/*     */     }
/* 272 */     if (TextUtils.isEmpty(uribuilder.getPath())) {
/* 273 */       uribuilder.setPath("/");
/*     */     }
/* 275 */     if (uribuilder.getHost() != null) {
/* 276 */       uribuilder.setHost(uribuilder.getHost().toLowerCase(Locale.ROOT));
/*     */     }
/* 278 */     uribuilder.setFragment(null);
/* 279 */     return uribuilder.build();
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
/*     */   public static URI rewriteURIForRoute(URI uri, RouteInfo route) throws URISyntaxException {
/* 294 */     return rewriteURIForRoute(uri, route, true);
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
/*     */   public static URI rewriteURIForRoute(URI uri, RouteInfo route, boolean normalizeUri) throws URISyntaxException {
/* 309 */     if (uri == null) {
/* 310 */       return null;
/*     */     }
/* 312 */     if (route.getProxyHost() != null && !route.isTunnelled())
/*     */     {
/* 314 */       return uri.isAbsolute() ? rewriteURI(uri) : rewriteURI(uri, route.getTargetHost(), normalizeUri ? DROP_FRAGMENT_AND_NORMALIZE : DROP_FRAGMENT);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 319 */     return uri.isAbsolute() ? rewriteURI(uri, (HttpHost)null, normalizeUri ? DROP_FRAGMENT_AND_NORMALIZE : DROP_FRAGMENT) : rewriteURI(uri);
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
/*     */   public static URI resolve(URI baseURI, String reference) {
/* 331 */     return resolve(baseURI, URI.create(reference));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URI resolve(URI baseURI, URI reference) {
/*     */     URI resolved;
/* 343 */     Args.notNull(baseURI, "Base URI");
/* 344 */     Args.notNull(reference, "Reference URI");
/* 345 */     String s = reference.toASCIIString();
/* 346 */     if (s.startsWith("?")) {
/* 347 */       String baseUri = baseURI.toASCIIString();
/* 348 */       int i = baseUri.indexOf('?');
/* 349 */       baseUri = (i > -1) ? baseUri.substring(0, i) : baseUri;
/* 350 */       return URI.create(baseUri + s);
/*     */     } 
/* 352 */     boolean emptyReference = s.isEmpty();
/*     */     
/* 354 */     if (emptyReference) {
/* 355 */       resolved = baseURI.resolve(URI.create("#"));
/* 356 */       String resolvedString = resolved.toASCIIString();
/* 357 */       resolved = URI.create(resolvedString.substring(0, resolvedString.indexOf('#')));
/*     */     } else {
/* 359 */       resolved = baseURI.resolve(reference);
/*     */     } 
/*     */     try {
/* 362 */       return normalizeSyntax(resolved);
/* 363 */     } catch (URISyntaxException ex) {
/* 364 */       throw new IllegalArgumentException(ex);
/*     */     } 
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
/*     */   public static URI normalizeSyntax(URI uri) throws URISyntaxException {
/* 378 */     if (uri.isOpaque() || uri.getAuthority() == null)
/*     */     {
/* 380 */       return uri;
/*     */     }
/* 382 */     URIBuilder builder = new URIBuilder(uri);
/* 383 */     List<String> inputSegments = builder.getPathSegments();
/* 384 */     Stack<String> outputSegments = new Stack<String>();
/* 385 */     for (String inputSegment : inputSegments) {
/* 386 */       if (".".equals(inputSegment))
/*     */         continue; 
/* 388 */       if ("..".equals(inputSegment)) {
/* 389 */         if (!outputSegments.isEmpty())
/* 390 */           outputSegments.pop(); 
/*     */         continue;
/*     */       } 
/* 393 */       outputSegments.push(inputSegment);
/*     */     } 
/*     */     
/* 396 */     if (outputSegments.size() == 0) {
/* 397 */       outputSegments.add("");
/*     */     }
/* 399 */     builder.setPathSegments(outputSegments);
/* 400 */     if (builder.getScheme() != null) {
/* 401 */       builder.setScheme(builder.getScheme().toLowerCase(Locale.ROOT));
/*     */     }
/* 403 */     if (builder.getHost() != null) {
/* 404 */       builder.setHost(builder.getHost().toLowerCase(Locale.ROOT));
/*     */     }
/* 406 */     return builder.build();
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
/*     */   public static HttpHost extractHost(URI uri) {
/* 419 */     if (uri == null) {
/* 420 */       return null;
/*     */     }
/* 422 */     HttpHost target = null;
/* 423 */     if (uri.isAbsolute()) {
/* 424 */       int port = uri.getPort();
/* 425 */       String host = uri.getHost();
/* 426 */       if (host == null) {
/*     */         
/* 428 */         host = uri.getAuthority();
/* 429 */         if (host != null) {
/*     */           
/* 431 */           int at = host.indexOf('@');
/* 432 */           if (at >= 0) {
/* 433 */             if (host.length() > at + 1) {
/* 434 */               host = host.substring(at + 1);
/*     */             } else {
/* 436 */               host = null;
/*     */             } 
/*     */           }
/*     */           
/* 440 */           if (host != null) {
/* 441 */             int colon = host.indexOf(':');
/* 442 */             if (colon >= 0) {
/* 443 */               int pos = colon + 1;
/* 444 */               int len = 0;
/* 445 */               for (int i = pos; i < host.length() && 
/* 446 */                 Character.isDigit(host.charAt(i)); i++) {
/* 447 */                 len++;
/*     */               }
/*     */ 
/*     */ 
/*     */               
/* 452 */               if (len > 0) {
/*     */                 try {
/* 454 */                   port = Integer.parseInt(host.substring(pos, pos + len));
/* 455 */                 } catch (NumberFormatException ex) {}
/*     */               }
/*     */               
/* 458 */               host = host.substring(0, colon);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 463 */       String scheme = uri.getScheme();
/* 464 */       if (!TextUtils.isBlank(host)) {
/*     */         try {
/* 466 */           target = new HttpHost(host, port, scheme);
/* 467 */         } catch (IllegalArgumentException ignore) {}
/*     */       }
/*     */     } 
/*     */     
/* 471 */     return target;
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
/*     */   public static URI resolve(URI originalURI, HttpHost target, List<URI> redirects) throws URISyntaxException {
/*     */     URIBuilder uribuilder;
/* 494 */     Args.notNull(originalURI, "Request URI");
/*     */     
/* 496 */     if (redirects == null || redirects.isEmpty()) {
/* 497 */       uribuilder = new URIBuilder(originalURI);
/*     */     } else {
/* 499 */       uribuilder = new URIBuilder(redirects.get(redirects.size() - 1));
/* 500 */       String frag = uribuilder.getFragment();
/*     */       
/* 502 */       for (int i = redirects.size() - 1; frag == null && i >= 0; i--) {
/* 503 */         frag = ((URI)redirects.get(i)).getFragment();
/*     */       }
/* 505 */       uribuilder.setFragment(frag);
/*     */     } 
/*     */     
/* 508 */     if (uribuilder.getFragment() == null) {
/* 509 */       uribuilder.setFragment(originalURI.getFragment());
/*     */     }
/*     */     
/* 512 */     if (target != null && !uribuilder.isAbsolute()) {
/* 513 */       uribuilder.setScheme(target.getSchemeName());
/* 514 */       uribuilder.setHost(target.getHostName());
/* 515 */       uribuilder.setPort(target.getPort());
/*     */     } 
/* 517 */     return uribuilder.build();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\URIUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */