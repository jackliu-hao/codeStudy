/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.handlers.cache.LRUCache;
/*     */ import io.undertow.server.handlers.resource.CachingResourceManager;
/*     */ import io.undertow.server.handlers.resource.Resource;
/*     */ import io.undertow.server.handlers.resource.ResourceChangeEvent;
/*     */ import io.undertow.server.handlers.resource.ResourceChangeListener;
/*     */ import io.undertow.server.handlers.resource.ResourceManager;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.FilterMappingInfo;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import io.undertow.servlet.core.ManagedFilter;
/*     */ import io.undertow.servlet.core.ManagedFilters;
/*     */ import io.undertow.servlet.core.ManagedServlet;
/*     */ import io.undertow.servlet.core.ManagedServlets;
/*     */ import io.undertow.servlet.handlers.security.ServletSecurityRoleHandler;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.http.MappingMatch;
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
/*     */ public class ServletPathMatches
/*     */ {
/*     */   public static final String DEFAULT_SERVLET_NAME = "default";
/*     */   private final Deployment deployment;
/*     */   private volatile String[] welcomePages;
/*     */   private final ResourceManager resourceManager;
/*     */   private volatile ServletPathMatchesData data;
/*     */   private final LRUCache<String, ServletPathMatch> pathMatchCacheFixed;
/*     */   private final LRUCache<String, ServletPathMatch> pathMatchCacheResources;
/*     */   
/*     */   public ServletPathMatches(Deployment deployment) {
/*  79 */     this.deployment = deployment;
/*  80 */     this.welcomePages = (String[])deployment.getDeploymentInfo().getWelcomePages().toArray((Object[])new String[deployment.getDeploymentInfo().getWelcomePages().size()]);
/*  81 */     this.resourceManager = deployment.getDeploymentInfo().getResourceManager();
/*  82 */     this.pathMatchCacheFixed = new LRUCache(1000, -1, true);
/*  83 */     this
/*  84 */       .pathMatchCacheResources = new LRUCache(1000, (this.resourceManager instanceof CachingResourceManager) ? ((CachingResourceManager)this.resourceManager).getMaxAge() : -1, true);
/*     */     
/*  86 */     if (this.resourceManager.isResourceChangeListenerSupported()) {
/*     */       try {
/*  88 */         this.resourceManager.registerResourceChangeListener(new ResourceChangeListener()
/*     */             {
/*     */               public void handleChanges(Collection<ResourceChangeEvent> changes)
/*     */               {
/*  92 */                 for (ResourceChangeEvent change : changes) {
/*     */                   
/*  94 */                   if (change.getType() != ResourceChangeEvent.Type.MODIFIED) {
/*  95 */                     String path = "/" + change.getResource();
/*     */                     
/*  97 */                     ServletPathMatches.this.pathMatchCacheResources.remove(path);
/*  98 */                     ServletPathMatches.this.pathMatchCacheResources.remove(path + "/");
/*     */                     
/* 100 */                     for (String welcomePage : ServletPathMatches.this.welcomePages) {
/* 101 */                       if (path.endsWith("/" + welcomePage)) {
/* 102 */                         String pathToUpdate = path.substring(0, path.length() - welcomePage.length());
/* 103 */                         ServletPathMatches.this.pathMatchCacheResources.remove(pathToUpdate);
/*     */                       } 
/*     */                     } 
/*     */                   } 
/*     */                 } 
/*     */               }
/*     */             });
/* 110 */       } catch (Exception e) {
/* 111 */         UndertowLogger.ROOT_LOGGER.couldNotRegisterChangeListener(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void initData() {
/* 117 */     getData();
/*     */   }
/*     */   
/*     */   public ServletChain getServletHandlerByName(String name) {
/* 121 */     return getData().getServletHandlerByName(name);
/*     */   }
/*     */   
/*     */   public ServletPathMatch getServletHandlerByPath(String path) {
/* 125 */     ServletPathMatch existing = (ServletPathMatch)this.pathMatchCacheFixed.get(path);
/* 126 */     if (existing == null) {
/* 127 */       existing = (ServletPathMatch)this.pathMatchCacheResources.get(path);
/*     */     }
/* 129 */     if (existing != null) {
/* 130 */       return existing;
/*     */     }
/*     */     
/* 133 */     ServletPathMatch match = getData().getServletHandlerByPath(path);
/* 134 */     if (!match.isRequiredWelcomeFileMatch()) {
/* 135 */       this.pathMatchCacheFixed.add(path, match);
/* 136 */       return match;
/*     */     } 
/*     */     
/*     */     try {
/* 140 */       String remaining = (match.getRemaining() == null) ? match.getMatched() : match.getRemaining();
/* 141 */       Resource resource = this.resourceManager.getResource(remaining);
/* 142 */       if (resource == null || !resource.isDirectory()) {
/* 143 */         this.pathMatchCacheResources.add(path, match);
/* 144 */         return match;
/*     */       } 
/*     */       
/* 147 */       boolean pathEndsWithSlash = remaining.endsWith("/");
/* 148 */       String pathWithTrailingSlash = pathEndsWithSlash ? remaining : (remaining + "/");
/*     */       
/* 150 */       ServletPathMatch welcomePage = findWelcomeFile(pathWithTrailingSlash, !pathEndsWithSlash);
/*     */       
/* 152 */       if (welcomePage != null) {
/* 153 */         this.pathMatchCacheResources.add(path, welcomePage);
/* 154 */         return welcomePage;
/*     */       } 
/* 156 */       welcomePage = findWelcomeServlet(pathWithTrailingSlash, !pathEndsWithSlash);
/* 157 */       if (welcomePage != null) {
/* 158 */         this.pathMatchCacheResources.add(path, welcomePage);
/* 159 */         return welcomePage;
/* 160 */       }  if (pathEndsWithSlash) {
/* 161 */         this.pathMatchCacheResources.add(path, match);
/* 162 */         return match;
/*     */       } 
/* 164 */       ServletPathMatch redirect = new ServletPathMatch(match.getServletChain(), match.getMatched(), match.getRemaining(), ServletPathMatch.Type.REDIRECT, "/");
/* 165 */       this.pathMatchCacheResources.add(path, redirect);
/* 166 */       return redirect;
/*     */ 
/*     */     
/*     */     }
/* 170 */     catch (IOException e) {
/* 171 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void invalidate() {
/* 177 */     this.data = null;
/* 178 */     this.pathMatchCacheResources.clear();
/* 179 */     this.pathMatchCacheFixed.clear();
/*     */   }
/*     */   
/*     */   private ServletPathMatchesData getData() {
/* 183 */     ServletPathMatchesData data = this.data;
/* 184 */     if (data != null) {
/* 185 */       return data;
/*     */     }
/* 187 */     synchronized (this) {
/* 188 */       if (this.data != null) {
/* 189 */         return this.data;
/*     */       }
/* 191 */       return this.data = setupServletChains();
/*     */     } 
/*     */   }
/*     */   
/*     */   private ServletPathMatch findWelcomeFile(String path, boolean requiresRedirect) {
/* 196 */     if (File.separatorChar != '/' && path.contains(File.separator)) {
/* 197 */       return null;
/*     */     }
/* 199 */     StringBuilder sb = new StringBuilder();
/* 200 */     for (String i : this.welcomePages) {
/*     */       try {
/* 202 */         sb.append(path);
/* 203 */         sb.append(i);
/* 204 */         String mergedPath = sb.toString();
/* 205 */         sb.setLength(0);
/* 206 */         Resource resource = this.resourceManager.getResource(mergedPath);
/* 207 */         if (resource != null) {
/* 208 */           ServletPathMatch handler = this.data.getServletHandlerByPath(mergedPath);
/* 209 */           return new ServletPathMatch(handler.getServletChain(), mergedPath, null, requiresRedirect ? ServletPathMatch.Type.REDIRECT : ServletPathMatch.Type.REWRITE, mergedPath);
/*     */         } 
/* 211 */       } catch (IOException iOException) {}
/*     */     } 
/*     */     
/* 214 */     return null;
/*     */   }
/*     */   
/*     */   private ServletPathMatch findWelcomeServlet(String path, boolean requiresRedirect) {
/* 218 */     StringBuilder sb = new StringBuilder();
/* 219 */     for (String i : this.welcomePages) {
/* 220 */       sb.append(path);
/* 221 */       sb.append(i);
/* 222 */       String mergedPath = sb.toString();
/* 223 */       sb.setLength(0);
/* 224 */       ServletPathMatch handler = this.data.getServletHandlerByPath(mergedPath);
/* 225 */       if (handler != null && !handler.isRequiredWelcomeFileMatch()) {
/* 226 */         return new ServletPathMatch(handler.getServletChain(), handler.getMatched(), handler.getRemaining(), requiresRedirect ? ServletPathMatch.Type.REDIRECT : ServletPathMatch.Type.REWRITE, mergedPath);
/*     */       }
/*     */     } 
/* 229 */     return null;
/*     */   }
/*     */   
/*     */   public void setWelcomePages(List<String> welcomePages) {
/* 233 */     this.welcomePages = welcomePages.<String>toArray(new String[welcomePages.size()]);
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
/*     */   private ServletPathMatchesData setupServletChains() {
/* 247 */     ServletHandler defaultServlet = null;
/* 248 */     ManagedServlets servlets = this.deployment.getServlets();
/* 249 */     ManagedFilters filters = this.deployment.getFilters();
/*     */     
/* 251 */     Map<String, ServletHandler> extensionServlets = new HashMap<>();
/* 252 */     Map<String, ServletHandler> pathServlets = new HashMap<>();
/*     */     
/* 254 */     Set<String> pathMatches = new HashSet<>();
/* 255 */     Set<String> extensionMatches = new HashSet<>();
/*     */     
/* 257 */     DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
/*     */ 
/*     */     
/* 260 */     for (FilterMappingInfo mapping : deploymentInfo.getFilterMappings()) {
/* 261 */       if (mapping.getMappingType() == FilterMappingInfo.MappingType.URL) {
/* 262 */         String path = mapping.getMapping();
/* 263 */         if (path.equals("*"))
/*     */         {
/* 265 */           path = "/*";
/*     */         }
/* 267 */         if (!path.startsWith("*.")) {
/* 268 */           pathMatches.add(path); continue;
/*     */         } 
/* 270 */         extensionMatches.add(path.substring(2));
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 276 */     for (Map.Entry<String, ServletHandler> entry : (Iterable<Map.Entry<String, ServletHandler>>)servlets.getServletHandlers().entrySet()) {
/* 277 */       ServletHandler handler = entry.getValue();
/*     */       
/* 279 */       for (String path : handler.getManagedServlet().getServletInfo().getMappings()) {
/* 280 */         if (path.equals("/")) {
/*     */           
/* 282 */           pathMatches.add("/*");
/* 283 */           if (defaultServlet != null) {
/* 284 */             throw UndertowServletMessages.MESSAGES.twoServletsWithSameMapping(path);
/*     */           }
/* 286 */           defaultServlet = handler; continue;
/* 287 */         }  if (!path.startsWith("*.")) {
/*     */           
/* 289 */           if (path.isEmpty()) {
/* 290 */             path = "/";
/*     */           }
/* 292 */           pathMatches.add(path);
/* 293 */           if (pathServlets.containsKey(path)) {
/* 294 */             throw UndertowServletMessages.MESSAGES.twoServletsWithSameMapping(path);
/*     */           }
/* 296 */           pathServlets.put(path, handler);
/*     */           continue;
/*     */         } 
/* 299 */         String ext = path.substring(2);
/* 300 */         extensionMatches.add(ext);
/* 301 */         if (extensionServlets.containsKey(ext)) {
/* 302 */           throw UndertowServletMessages.MESSAGES.twoServletsWithSameMapping(path);
/*     */         }
/* 304 */         extensionServlets.put(ext, handler);
/*     */       } 
/*     */     } 
/*     */     
/* 308 */     ServletHandler managedDefaultServlet = servlets.getServletHandler("default");
/* 309 */     if (managedDefaultServlet == null)
/*     */     {
/* 311 */       managedDefaultServlet = servlets.addServlet(new ServletInfo("default", DefaultServlet.class));
/*     */     }
/*     */     
/* 314 */     if (defaultServlet == null) {
/*     */       
/* 316 */       pathMatches.add("/*");
/* 317 */       defaultServlet = managedDefaultServlet;
/*     */     } 
/*     */     
/* 320 */     ServletPathMatchesData.Builder builder = ServletPathMatchesData.builder();
/*     */ 
/*     */ 
/*     */     
/* 324 */     for (String path : pathMatches) {
/*     */       String lastSegment;
/* 326 */       MatchData targetServletMatch = resolveServletForPath(path, pathServlets, extensionServlets, defaultServlet);
/*     */       
/* 328 */       Map<DispatcherType, List<ManagedFilter>> noExtension = new EnumMap<>(DispatcherType.class);
/* 329 */       Map<String, Map<DispatcherType, List<ManagedFilter>>> extension = new HashMap<>();
/*     */ 
/*     */       
/* 332 */       for (String ext : extensionMatches) {
/* 333 */         extension.put(ext, new EnumMap<>(DispatcherType.class));
/*     */       }
/*     */ 
/*     */       
/* 337 */       for (FilterMappingInfo filterMapping : deploymentInfo.getFilterMappings()) {
/* 338 */         ManagedFilter filter = filters.getManagedFilter(filterMapping.getFilterName());
/* 339 */         if (filterMapping.getMappingType() == FilterMappingInfo.MappingType.SERVLET) {
/* 340 */           if (targetServletMatch.handler != null && (
/* 341 */             filterMapping.getMapping().equals(targetServletMatch.handler.getManagedServlet().getServletInfo().getName()) || filterMapping.getMapping().equals("*"))) {
/* 342 */             addToListMap(noExtension, filterMapping.getDispatcher(), filter);
/*     */           }
/*     */           
/* 345 */           for (Map.Entry<String, Map<DispatcherType, List<ManagedFilter>>> entry : extension.entrySet()) {
/* 346 */             ServletHandler pathServlet = targetServletMatch.handler;
/* 347 */             boolean defaultServletMatch = targetServletMatch.defaultServlet;
/* 348 */             if (defaultServletMatch && extensionServlets.containsKey(entry.getKey())) {
/* 349 */               pathServlet = extensionServlets.get(entry.getKey());
/*     */             }
/*     */             
/* 352 */             if (filterMapping.getMapping().equals(pathServlet.getManagedServlet().getServletInfo().getName()) || filterMapping.getMapping().equals("*"))
/* 353 */               addToListMap(extension.get(entry.getKey()), filterMapping.getDispatcher(), filter); 
/*     */           } 
/*     */           continue;
/*     */         } 
/* 357 */         if (filterMapping.getMapping().isEmpty() || !filterMapping.getMapping().startsWith("*.")) {
/* 358 */           if (isFilterApplicable(path, filterMapping.getMapping())) {
/* 359 */             addToListMap(noExtension, filterMapping.getDispatcher(), filter);
/* 360 */             for (Map<DispatcherType, List<ManagedFilter>> l : extension.values())
/* 361 */               addToListMap(l, filterMapping.getDispatcher(), filter); 
/*     */           } 
/*     */           continue;
/*     */         } 
/* 365 */         addToListMap(extension.get(filterMapping.getMapping().substring(2)), filterMapping.getDispatcher(), filter);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 370 */       if (path.endsWith("/*")) {
/* 371 */         String prefix = path.substring(0, path.length() - 2);
/*     */         
/* 373 */         builder.addPrefixMatch(prefix, createHandler(deploymentInfo, targetServletMatch.handler, noExtension, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath), (targetServletMatch.defaultServlet || targetServletMatch.handler.getManagedServlet().getServletInfo().isRequireWelcomeFileMapping()));
/*     */ 
/*     */         
/* 376 */         for (Map.Entry<String, Map<DispatcherType, List<ManagedFilter>>> entry : extension.entrySet()) {
/* 377 */           boolean defaultServletMatch; String servletMatchPattern; MappingMatch mappingMatch; ServletHandler pathServlet = targetServletMatch.handler;
/* 378 */           String pathMatch = targetServletMatch.matchedPath;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 383 */           if (targetServletMatch.defaultServlet) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 389 */             ServletHandler extensionServletHandler = extensionServlets.get(entry.getKey());
/* 390 */             if (extensionServletHandler != null) {
/* 391 */               defaultServletMatch = false;
/* 392 */               pathServlet = extensionServletHandler;
/* 393 */               servletMatchPattern = "*." + (String)entry.getKey();
/* 394 */               mappingMatch = MappingMatch.EXTENSION;
/*     */             } else {
/* 396 */               defaultServletMatch = true;
/* 397 */               servletMatchPattern = "/";
/* 398 */               mappingMatch = MappingMatch.DEFAULT;
/*     */             } 
/*     */           } else {
/* 401 */             defaultServletMatch = false;
/* 402 */             servletMatchPattern = path;
/* 403 */             mappingMatch = MappingMatch.PATH;
/*     */           } 
/* 405 */           HttpHandler handler = pathServlet;
/* 406 */           if (!((Map)entry.getValue()).isEmpty()) {
/* 407 */             handler = new FilterHandler(entry.getValue(), deploymentInfo.isAllowNonStandardWrappers(), handler);
/*     */           }
/* 409 */           builder.addExtensionMatch(prefix, entry.getKey(), servletChain(handler, pathServlet.getManagedServlet(), entry.getValue(), pathMatch, deploymentInfo, defaultServletMatch, mappingMatch, servletMatchPattern));
/*     */         }  continue;
/* 411 */       }  if (path.isEmpty()) {
/*     */         
/* 413 */         builder.addExactMatch("/", createHandler(deploymentInfo, targetServletMatch.handler, noExtension, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath));
/*     */         continue;
/*     */       } 
/* 416 */       int lastSegmentIndex = path.lastIndexOf('/');
/*     */       
/* 418 */       if (lastSegmentIndex > 0) {
/* 419 */         lastSegment = path.substring(lastSegmentIndex);
/*     */       } else {
/* 421 */         lastSegment = path;
/*     */       } 
/* 423 */       if (lastSegment.contains(".")) {
/* 424 */         String ext = lastSegment.substring(lastSegment.lastIndexOf('.') + 1);
/* 425 */         if (extension.containsKey(ext)) {
/* 426 */           Map<DispatcherType, List<ManagedFilter>> extMap = extension.get(ext);
/* 427 */           builder.addExactMatch(path, createHandler(deploymentInfo, targetServletMatch.handler, extMap, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath)); continue;
/*     */         } 
/* 429 */         builder.addExactMatch(path, createHandler(deploymentInfo, targetServletMatch.handler, noExtension, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath));
/*     */         continue;
/*     */       } 
/* 432 */       builder.addExactMatch(path, createHandler(deploymentInfo, targetServletMatch.handler, noExtension, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 440 */     for (Map.Entry<String, ServletHandler> entry : (Iterable<Map.Entry<String, ServletHandler>>)servlets.getServletHandlers().entrySet()) {
/* 441 */       Map<DispatcherType, List<ManagedFilter>> filtersByDispatcher = new EnumMap<>(DispatcherType.class);
/* 442 */       for (FilterMappingInfo filterMapping : deploymentInfo.getFilterMappings()) {
/* 443 */         ManagedFilter filter = filters.getManagedFilter(filterMapping.getFilterName());
/* 444 */         if (filterMapping.getMappingType() == FilterMappingInfo.MappingType.SERVLET && 
/* 445 */           filterMapping.getMapping().equals(entry.getKey())) {
/* 446 */           addToListMap(filtersByDispatcher, filterMapping.getDispatcher(), filter);
/*     */         }
/*     */       } 
/*     */       
/* 450 */       if (filtersByDispatcher.isEmpty()) {
/* 451 */         builder.addNameMatch(entry.getKey(), servletChain(entry.getValue(), ((ServletHandler)entry.getValue()).getManagedServlet(), filtersByDispatcher, null, deploymentInfo, false, MappingMatch.EXACT, "")); continue;
/*     */       } 
/* 453 */       builder.addNameMatch(entry.getKey(), servletChain(new FilterHandler(filtersByDispatcher, deploymentInfo.isAllowNonStandardWrappers(), entry.getValue()), ((ServletHandler)entry.getValue()).getManagedServlet(), filtersByDispatcher, null, deploymentInfo, false, MappingMatch.EXACT, ""));
/*     */     } 
/*     */ 
/*     */     
/* 457 */     return builder.build();
/*     */   }
/*     */   
/*     */   private ServletChain createHandler(DeploymentInfo deploymentInfo, ServletHandler targetServlet, Map<DispatcherType, List<ManagedFilter>> noExtension, String servletPath, boolean defaultServlet, MappingMatch mappingMatch, String pattern) {
/*     */     ServletChain initialHandler;
/* 462 */     if (noExtension.isEmpty()) {
/* 463 */       initialHandler = servletChain(targetServlet, targetServlet.getManagedServlet(), noExtension, servletPath, deploymentInfo, defaultServlet, mappingMatch, pattern);
/*     */     } else {
/* 465 */       FilterHandler handler = new FilterHandler(noExtension, deploymentInfo.isAllowNonStandardWrappers(), targetServlet);
/* 466 */       initialHandler = servletChain(handler, targetServlet.getManagedServlet(), noExtension, servletPath, deploymentInfo, defaultServlet, mappingMatch, pattern);
/*     */     } 
/* 468 */     return initialHandler;
/*     */   }
/*     */   
/*     */   private static MatchData resolveServletForPath(String path, Map<String, ServletHandler> pathServlets, Map<String, ServletHandler> extensionServlets, ServletHandler defaultServlet) {
/* 472 */     if (pathServlets.containsKey(path)) {
/* 473 */       if (path.endsWith("/*")) {
/* 474 */         String base = path.substring(0, path.length() - 2);
/* 475 */         return new MatchData(pathServlets.get(path), base, path, MappingMatch.PATH, false);
/*     */       } 
/* 477 */       if (path.equals("/")) {
/* 478 */         return new MatchData(pathServlets.get(path), path, "", MappingMatch.CONTEXT_ROOT, false);
/*     */       }
/* 480 */       return new MatchData(pathServlets.get(path), path, path, MappingMatch.EXACT, false);
/*     */     } 
/*     */     
/* 483 */     String match = null;
/* 484 */     ServletHandler servlet = null;
/* 485 */     String userPath = "";
/* 486 */     for (Map.Entry<String, ServletHandler> entry : pathServlets.entrySet()) {
/* 487 */       String key = entry.getKey();
/* 488 */       if (key.endsWith("/*")) {
/* 489 */         String base = key.substring(0, key.length() - 1);
/* 490 */         if ((match == null || base.length() > match.length()) && (
/* 491 */           path.startsWith(base) || path.equals(base.substring(0, base.length() - 1)))) {
/* 492 */           match = base.substring(0, base.length() - 1);
/* 493 */           servlet = entry.getValue();
/* 494 */           userPath = key;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 499 */     if (servlet != null) {
/* 500 */       return new MatchData(servlet, match, userPath, MappingMatch.PATH, false);
/*     */     }
/* 502 */     int index = path.lastIndexOf('.');
/* 503 */     if (index != -1) {
/* 504 */       String ext = path.substring(index + 1);
/* 505 */       servlet = extensionServlets.get(ext);
/* 506 */       if (servlet != null) {
/* 507 */         return new MatchData(servlet, null, "*." + ext, MappingMatch.EXTENSION, false);
/*     */       }
/*     */     } 
/*     */     
/* 511 */     return new MatchData(defaultServlet, null, "/", MappingMatch.DEFAULT, true);
/*     */   }
/*     */   
/*     */   private static boolean isFilterApplicable(String path, String filterPath) {
/*     */     String modifiedPath;
/* 516 */     if (filterPath.equals("*")) {
/* 517 */       modifiedPath = "/*";
/*     */     } else {
/* 519 */       modifiedPath = filterPath;
/*     */     } 
/* 521 */     if (path.isEmpty()) {
/* 522 */       return (modifiedPath.equals("/*") || modifiedPath.equals("/"));
/*     */     }
/* 524 */     if (modifiedPath.endsWith("/*")) {
/* 525 */       String baseFilterPath = modifiedPath.substring(0, modifiedPath.length() - 1);
/* 526 */       String exactFilterPath = modifiedPath.substring(0, modifiedPath.length() - 2);
/* 527 */       return (path.startsWith(baseFilterPath) || path.equals(exactFilterPath));
/*     */     } 
/* 529 */     return modifiedPath.equals(path);
/*     */   }
/*     */ 
/*     */   
/*     */   private static <K, V> void addToListMap(Map<K, List<V>> map, K key, V value) {
/* 534 */     List<V> list = map.get(key);
/* 535 */     if (list == null) {
/* 536 */       map.put(key, list = new ArrayList<>());
/*     */     }
/* 538 */     list.add(value);
/*     */   }
/*     */   private static ServletChain servletChain(HttpHandler next, ManagedServlet managedServlet, Map<DispatcherType, List<ManagedFilter>> filters, String servletPath, DeploymentInfo deploymentInfo, boolean defaultServlet, MappingMatch mappingMatch, String pattern) {
/*     */     ServletSecurityRoleHandler servletSecurityRoleHandler;
/* 542 */     HttpHandler servletHandler = next;
/* 543 */     if (!deploymentInfo.isSecurityDisabled()) {
/* 544 */       servletSecurityRoleHandler = new ServletSecurityRoleHandler(servletHandler, deploymentInfo.getAuthorizationManager());
/*     */     }
/* 546 */     HttpHandler httpHandler1 = wrapHandlers((HttpHandler)servletSecurityRoleHandler, managedServlet.getServletInfo().getHandlerChainWrappers());
/* 547 */     return new ServletChain(httpHandler1, managedServlet, servletPath, defaultServlet, mappingMatch, pattern, filters);
/*     */   }
/*     */   
/*     */   private static HttpHandler wrapHandlers(HttpHandler wrapee, List<HandlerWrapper> wrappers) {
/* 551 */     HttpHandler current = wrapee;
/* 552 */     for (HandlerWrapper wrapper : wrappers) {
/* 553 */       current = wrapper.wrap(current);
/*     */     }
/* 555 */     return current;
/*     */   }
/*     */   
/*     */   private static class MatchData {
/*     */     final ServletHandler handler;
/*     */     final String matchedPath;
/*     */     final String userPath;
/*     */     final MappingMatch mappingMatch;
/*     */     final boolean defaultServlet;
/*     */     
/*     */     private MatchData(ServletHandler handler, String matchedPath, String userPath, MappingMatch mappingMatch, boolean defaultServlet) {
/* 566 */       this.handler = handler;
/* 567 */       this.matchedPath = matchedPath;
/* 568 */       this.userPath = userPath;
/* 569 */       this.mappingMatch = mappingMatch;
/* 570 */       this.defaultServlet = defaultServlet;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\ServletPathMatches.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */