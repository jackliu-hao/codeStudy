package io.undertow.servlet.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.cache.LRUCache;
import io.undertow.server.handlers.resource.CachingResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeEvent;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.FilterMappingInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.core.ManagedFilter;
import io.undertow.servlet.core.ManagedFilters;
import io.undertow.servlet.core.ManagedServlet;
import io.undertow.servlet.core.ManagedServlets;
import io.undertow.servlet.handlers.security.ServletSecurityRoleHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.http.MappingMatch;

public class ServletPathMatches {
   public static final String DEFAULT_SERVLET_NAME = "default";
   private final Deployment deployment;
   private volatile String[] welcomePages;
   private final ResourceManager resourceManager;
   private volatile ServletPathMatchesData data;
   private final LRUCache<String, ServletPathMatch> pathMatchCacheFixed;
   private final LRUCache<String, ServletPathMatch> pathMatchCacheResources;

   public ServletPathMatches(Deployment deployment) {
      this.deployment = deployment;
      this.welcomePages = (String[])deployment.getDeploymentInfo().getWelcomePages().toArray(new String[deployment.getDeploymentInfo().getWelcomePages().size()]);
      this.resourceManager = deployment.getDeploymentInfo().getResourceManager();
      this.pathMatchCacheFixed = new LRUCache(1000, -1, true);
      this.pathMatchCacheResources = new LRUCache(1000, this.resourceManager instanceof CachingResourceManager ? ((CachingResourceManager)this.resourceManager).getMaxAge() : -1, true);
      if (this.resourceManager.isResourceChangeListenerSupported()) {
         try {
            this.resourceManager.registerResourceChangeListener(new ResourceChangeListener() {
               public void handleChanges(Collection<ResourceChangeEvent> changes) {
                  Iterator var2 = changes.iterator();

                  while(true) {
                     ResourceChangeEvent change;
                     do {
                        if (!var2.hasNext()) {
                           return;
                        }

                        change = (ResourceChangeEvent)var2.next();
                     } while(change.getType() == ResourceChangeEvent.Type.MODIFIED);

                     String path = "/" + change.getResource();
                     ServletPathMatches.this.pathMatchCacheResources.remove(path);
                     ServletPathMatches.this.pathMatchCacheResources.remove(path + "/");
                     String[] var5 = ServletPathMatches.this.welcomePages;
                     int var6 = var5.length;

                     for(int var7 = 0; var7 < var6; ++var7) {
                        String welcomePage = var5[var7];
                        if (path.endsWith("/" + welcomePage)) {
                           String pathToUpdate = path.substring(0, path.length() - welcomePage.length());
                           ServletPathMatches.this.pathMatchCacheResources.remove(pathToUpdate);
                        }
                     }
                  }
               }
            });
         } catch (Exception var3) {
            UndertowLogger.ROOT_LOGGER.couldNotRegisterChangeListener(var3);
         }
      }

   }

   public void initData() {
      this.getData();
   }

   public ServletChain getServletHandlerByName(String name) {
      return this.getData().getServletHandlerByName(name);
   }

   public ServletPathMatch getServletHandlerByPath(String path) {
      ServletPathMatch existing = (ServletPathMatch)this.pathMatchCacheFixed.get(path);
      if (existing == null) {
         existing = (ServletPathMatch)this.pathMatchCacheResources.get(path);
      }

      if (existing != null) {
         return existing;
      } else {
         ServletPathMatch match = this.getData().getServletHandlerByPath(path);
         if (!match.isRequiredWelcomeFileMatch()) {
            this.pathMatchCacheFixed.add(path, match);
            return match;
         } else {
            try {
               String remaining = match.getRemaining() == null ? match.getMatched() : match.getRemaining();
               Resource resource = this.resourceManager.getResource(remaining);
               if (resource != null && resource.isDirectory()) {
                  boolean pathEndsWithSlash = remaining.endsWith("/");
                  String pathWithTrailingSlash = pathEndsWithSlash ? remaining : remaining + "/";
                  ServletPathMatch welcomePage = this.findWelcomeFile(pathWithTrailingSlash, !pathEndsWithSlash);
                  if (welcomePage != null) {
                     this.pathMatchCacheResources.add(path, welcomePage);
                     return welcomePage;
                  } else {
                     welcomePage = this.findWelcomeServlet(pathWithTrailingSlash, !pathEndsWithSlash);
                     if (welcomePage != null) {
                        this.pathMatchCacheResources.add(path, welcomePage);
                        return welcomePage;
                     } else if (pathEndsWithSlash) {
                        this.pathMatchCacheResources.add(path, match);
                        return match;
                     } else {
                        ServletPathMatch redirect = new ServletPathMatch(match.getServletChain(), match.getMatched(), match.getRemaining(), ServletPathMatch.Type.REDIRECT, "/");
                        this.pathMatchCacheResources.add(path, redirect);
                        return redirect;
                     }
                  }
               } else {
                  this.pathMatchCacheResources.add(path, match);
                  return match;
               }
            } catch (IOException var10) {
               throw new RuntimeException(var10);
            }
         }
      }
   }

   public void invalidate() {
      this.data = null;
      this.pathMatchCacheResources.clear();
      this.pathMatchCacheFixed.clear();
   }

   private ServletPathMatchesData getData() {
      ServletPathMatchesData data = this.data;
      if (data != null) {
         return data;
      } else {
         synchronized(this) {
            return this.data != null ? this.data : (this.data = this.setupServletChains());
         }
      }
   }

   private ServletPathMatch findWelcomeFile(String path, boolean requiresRedirect) {
      if (File.separatorChar != '/' && path.contains(File.separator)) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();
         String[] var4 = this.welcomePages;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String i = var4[var6];

            try {
               sb.append(path);
               sb.append(i);
               String mergedPath = sb.toString();
               sb.setLength(0);
               Resource resource = this.resourceManager.getResource(mergedPath);
               if (resource != null) {
                  ServletPathMatch handler = this.data.getServletHandlerByPath(mergedPath);
                  return new ServletPathMatch(handler.getServletChain(), mergedPath, (String)null, requiresRedirect ? ServletPathMatch.Type.REDIRECT : ServletPathMatch.Type.REWRITE, mergedPath);
               }
            } catch (IOException var11) {
            }
         }

         return null;
      }
   }

   private ServletPathMatch findWelcomeServlet(String path, boolean requiresRedirect) {
      StringBuilder sb = new StringBuilder();
      String[] var4 = this.welcomePages;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String i = var4[var6];
         sb.append(path);
         sb.append(i);
         String mergedPath = sb.toString();
         sb.setLength(0);
         ServletPathMatch handler = this.data.getServletHandlerByPath(mergedPath);
         if (handler != null && !handler.isRequiredWelcomeFileMatch()) {
            return new ServletPathMatch(handler.getServletChain(), handler.getMatched(), handler.getRemaining(), requiresRedirect ? ServletPathMatch.Type.REDIRECT : ServletPathMatch.Type.REWRITE, mergedPath);
         }
      }

      return null;
   }

   public void setWelcomePages(List<String> welcomePages) {
      this.welcomePages = (String[])welcomePages.toArray(new String[welcomePages.size()]);
   }

   private ServletPathMatchesData setupServletChains() {
      ServletHandler defaultServlet = null;
      ManagedServlets servlets = this.deployment.getServlets();
      ManagedFilters filters = this.deployment.getFilters();
      Map<String, ServletHandler> extensionServlets = new HashMap();
      Map<String, ServletHandler> pathServlets = new HashMap();
      Set<String> pathMatches = new HashSet();
      Set<String> extensionMatches = new HashSet();
      DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
      Iterator var9 = deploymentInfo.getFilterMappings().iterator();

      while(var9.hasNext()) {
         FilterMappingInfo mapping = (FilterMappingInfo)var9.next();
         if (mapping.getMappingType() == FilterMappingInfo.MappingType.URL) {
            String path = mapping.getMapping();
            if (path.equals("*")) {
               path = "/*";
            }

            if (!path.startsWith("*.")) {
               pathMatches.add(path);
            } else {
               extensionMatches.add(path.substring(2));
            }
         }
      }

      var9 = servlets.getServletHandlers().entrySet().iterator();

      while(var9.hasNext()) {
         Map.Entry<String, ServletHandler> entry = (Map.Entry)var9.next();
         ServletHandler handler = (ServletHandler)entry.getValue();
         Iterator var12 = handler.getManagedServlet().getServletInfo().getMappings().iterator();

         while(var12.hasNext()) {
            String path = (String)var12.next();
            if (path.equals("/")) {
               pathMatches.add("/*");
               if (defaultServlet != null) {
                  throw UndertowServletMessages.MESSAGES.twoServletsWithSameMapping(path);
               }

               defaultServlet = handler;
            } else if (!path.startsWith("*.")) {
               if (path.isEmpty()) {
                  path = "/";
               }

               pathMatches.add(path);
               if (pathServlets.containsKey(path)) {
                  throw UndertowServletMessages.MESSAGES.twoServletsWithSameMapping(path);
               }

               pathServlets.put(path, handler);
            } else {
               String ext = path.substring(2);
               extensionMatches.add(ext);
               if (extensionServlets.containsKey(ext)) {
                  throw UndertowServletMessages.MESSAGES.twoServletsWithSameMapping(path);
               }

               extensionServlets.put(ext, handler);
            }
         }
      }

      ServletHandler managedDefaultServlet = servlets.getServletHandler("default");
      if (managedDefaultServlet == null) {
         managedDefaultServlet = servlets.addServlet(new ServletInfo("default", DefaultServlet.class));
      }

      if (defaultServlet == null) {
         pathMatches.add("/*");
         defaultServlet = managedDefaultServlet;
      }

      ServletPathMatchesData.Builder builder = ServletPathMatchesData.builder();
      Iterator var29 = pathMatches.iterator();

      while(true) {
         while(var29.hasNext()) {
            String path = (String)var29.next();
            MatchData targetServletMatch = resolveServletForPath(path, pathServlets, extensionServlets, defaultServlet);
            Map<DispatcherType, List<ManagedFilter>> noExtension = new EnumMap(DispatcherType.class);
            Map<String, Map<DispatcherType, List<ManagedFilter>>> extension = new HashMap();
            Iterator var16 = extensionMatches.iterator();

            String lastSegment;
            while(var16.hasNext()) {
               lastSegment = (String)var16.next();
               extension.put(lastSegment, new EnumMap(DispatcherType.class));
            }

            var16 = deploymentInfo.getFilterMappings().iterator();

            while(true) {
               label175:
               while(var16.hasNext()) {
                  FilterMappingInfo filterMapping = (FilterMappingInfo)var16.next();
                  ManagedFilter filter = filters.getManagedFilter(filterMapping.getFilterName());
                  Iterator var19;
                  if (filterMapping.getMappingType() == FilterMappingInfo.MappingType.SERVLET) {
                     if (targetServletMatch.handler != null && (filterMapping.getMapping().equals(targetServletMatch.handler.getManagedServlet().getServletInfo().getName()) || filterMapping.getMapping().equals("*"))) {
                        addToListMap(noExtension, filterMapping.getDispatcher(), filter);
                     }

                     var19 = extension.entrySet().iterator();

                     while(true) {
                        ServletHandler pathServlet;
                        Map.Entry entry;
                        do {
                           if (!var19.hasNext()) {
                              continue label175;
                           }

                           entry = (Map.Entry)var19.next();
                           pathServlet = targetServletMatch.handler;
                           boolean defaultServletMatch = targetServletMatch.defaultServlet;
                           if (defaultServletMatch && extensionServlets.containsKey(entry.getKey())) {
                              pathServlet = (ServletHandler)extensionServlets.get(entry.getKey());
                           }
                        } while(!filterMapping.getMapping().equals(pathServlet.getManagedServlet().getServletInfo().getName()) && !filterMapping.getMapping().equals("*"));

                        addToListMap((Map)extension.get(entry.getKey()), filterMapping.getDispatcher(), filter);
                     }
                  } else if (!filterMapping.getMapping().isEmpty() && filterMapping.getMapping().startsWith("*.")) {
                     addToListMap((Map)extension.get(filterMapping.getMapping().substring(2)), filterMapping.getDispatcher(), filter);
                  } else if (isFilterApplicable(path, filterMapping.getMapping())) {
                     addToListMap(noExtension, filterMapping.getDispatcher(), filter);
                     var19 = extension.values().iterator();

                     while(var19.hasNext()) {
                        Map<DispatcherType, List<ManagedFilter>> l = (Map)var19.next();
                        addToListMap(l, filterMapping.getDispatcher(), filter);
                     }
                  }
               }

               if (path.endsWith("/*")) {
                  String prefix = path.substring(0, path.length() - 2);
                  builder.addPrefixMatch(prefix, this.createHandler(deploymentInfo, targetServletMatch.handler, noExtension, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath), targetServletMatch.defaultServlet || targetServletMatch.handler.getManagedServlet().getServletInfo().isRequireWelcomeFileMapping());

                  MappingMatch mappingMatch;
                  Map.Entry entry;
                  ServletHandler pathServlet;
                  String pathMatch;
                  boolean defaultServletMatch;
                  String servletMatchPattern;
                  Object handler;
                  for(Iterator var42 = extension.entrySet().iterator(); var42.hasNext(); builder.addExtensionMatch(prefix, (String)entry.getKey(), servletChain((HttpHandler)handler, pathServlet.getManagedServlet(), (Map)entry.getValue(), pathMatch, deploymentInfo, defaultServletMatch, mappingMatch, servletMatchPattern))) {
                     entry = (Map.Entry)var42.next();
                     pathServlet = targetServletMatch.handler;
                     pathMatch = targetServletMatch.matchedPath;
                     if (targetServletMatch.defaultServlet) {
                        ServletHandler extensionServletHandler = (ServletHandler)extensionServlets.get(entry.getKey());
                        if (extensionServletHandler != null) {
                           defaultServletMatch = false;
                           pathServlet = extensionServletHandler;
                           servletMatchPattern = "*." + (String)entry.getKey();
                           mappingMatch = MappingMatch.EXTENSION;
                        } else {
                           defaultServletMatch = true;
                           servletMatchPattern = "/";
                           mappingMatch = MappingMatch.DEFAULT;
                        }
                     } else {
                        defaultServletMatch = false;
                        servletMatchPattern = path;
                        mappingMatch = MappingMatch.PATH;
                     }

                     handler = pathServlet;
                     if (!((Map)entry.getValue()).isEmpty()) {
                        handler = new FilterHandler((Map)entry.getValue(), deploymentInfo.isAllowNonStandardWrappers(), pathServlet);
                     }
                  }
                  break;
               }

               if (path.isEmpty()) {
                  builder.addExactMatch("/", this.createHandler(deploymentInfo, targetServletMatch.handler, noExtension, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath));
               } else {
                  int lastSegmentIndex = path.lastIndexOf(47);
                  if (lastSegmentIndex > 0) {
                     lastSegment = path.substring(lastSegmentIndex);
                  } else {
                     lastSegment = path;
                  }

                  if (lastSegment.contains(".")) {
                     String ext = lastSegment.substring(lastSegment.lastIndexOf(46) + 1);
                     if (extension.containsKey(ext)) {
                        Map<DispatcherType, List<ManagedFilter>> extMap = (Map)extension.get(ext);
                        builder.addExactMatch(path, this.createHandler(deploymentInfo, targetServletMatch.handler, extMap, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath));
                     } else {
                        builder.addExactMatch(path, this.createHandler(deploymentInfo, targetServletMatch.handler, noExtension, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath));
                     }
                  } else {
                     builder.addExactMatch(path, this.createHandler(deploymentInfo, targetServletMatch.handler, noExtension, targetServletMatch.matchedPath, targetServletMatch.defaultServlet, targetServletMatch.mappingMatch, targetServletMatch.userPath));
                  }
               }
               break;
            }
         }

         var29 = servlets.getServletHandlers().entrySet().iterator();

         while(var29.hasNext()) {
            Map.Entry<String, ServletHandler> entry = (Map.Entry)var29.next();
            Map<DispatcherType, List<ManagedFilter>> filtersByDispatcher = new EnumMap(DispatcherType.class);
            Iterator var35 = deploymentInfo.getFilterMappings().iterator();

            while(var35.hasNext()) {
               FilterMappingInfo filterMapping = (FilterMappingInfo)var35.next();
               ManagedFilter filter = filters.getManagedFilter(filterMapping.getFilterName());
               if (filterMapping.getMappingType() == FilterMappingInfo.MappingType.SERVLET && filterMapping.getMapping().equals(entry.getKey())) {
                  addToListMap(filtersByDispatcher, filterMapping.getDispatcher(), filter);
               }
            }

            if (filtersByDispatcher.isEmpty()) {
               builder.addNameMatch((String)entry.getKey(), servletChain((HttpHandler)entry.getValue(), ((ServletHandler)entry.getValue()).getManagedServlet(), filtersByDispatcher, (String)null, deploymentInfo, false, MappingMatch.EXACT, ""));
            } else {
               builder.addNameMatch((String)entry.getKey(), servletChain(new FilterHandler(filtersByDispatcher, deploymentInfo.isAllowNonStandardWrappers(), (HttpHandler)entry.getValue()), ((ServletHandler)entry.getValue()).getManagedServlet(), filtersByDispatcher, (String)null, deploymentInfo, false, MappingMatch.EXACT, ""));
            }
         }

         return builder.build();
      }
   }

   private ServletChain createHandler(DeploymentInfo deploymentInfo, ServletHandler targetServlet, Map<DispatcherType, List<ManagedFilter>> noExtension, String servletPath, boolean defaultServlet, MappingMatch mappingMatch, String pattern) {
      ServletChain initialHandler;
      if (noExtension.isEmpty()) {
         initialHandler = servletChain(targetServlet, targetServlet.getManagedServlet(), noExtension, servletPath, deploymentInfo, defaultServlet, mappingMatch, pattern);
      } else {
         FilterHandler handler = new FilterHandler(noExtension, deploymentInfo.isAllowNonStandardWrappers(), targetServlet);
         initialHandler = servletChain(handler, targetServlet.getManagedServlet(), noExtension, servletPath, deploymentInfo, defaultServlet, mappingMatch, pattern);
      }

      return initialHandler;
   }

   private static MatchData resolveServletForPath(String path, Map<String, ServletHandler> pathServlets, Map<String, ServletHandler> extensionServlets, ServletHandler defaultServlet) {
      String match;
      if (pathServlets.containsKey(path)) {
         if (path.endsWith("/*")) {
            match = path.substring(0, path.length() - 2);
            return new MatchData((ServletHandler)pathServlets.get(path), match, path, MappingMatch.PATH, false);
         } else {
            return path.equals("/") ? new MatchData((ServletHandler)pathServlets.get(path), path, "", MappingMatch.CONTEXT_ROOT, false) : new MatchData((ServletHandler)pathServlets.get(path), path, path, MappingMatch.EXACT, false);
         }
      } else {
         match = null;
         ServletHandler servlet = null;
         String userPath = "";
         Iterator var7 = pathServlets.entrySet().iterator();

         while(true) {
            Map.Entry entry;
            String key;
            String base;
            do {
               do {
                  do {
                     if (!var7.hasNext()) {
                        if (servlet != null) {
                           return new MatchData(servlet, match, userPath, MappingMatch.PATH, false);
                        }

                        int index = path.lastIndexOf(46);
                        if (index != -1) {
                           String ext = path.substring(index + 1);
                           servlet = (ServletHandler)extensionServlets.get(ext);
                           if (servlet != null) {
                              return new MatchData(servlet, (String)null, "*." + ext, MappingMatch.EXTENSION, false);
                           }
                        }

                        return new MatchData(defaultServlet, (String)null, "/", MappingMatch.DEFAULT, true);
                     }

                     entry = (Map.Entry)var7.next();
                     key = (String)entry.getKey();
                  } while(!key.endsWith("/*"));

                  base = key.substring(0, key.length() - 1);
               } while(match != null && base.length() <= match.length());
            } while(!path.startsWith(base) && !path.equals(base.substring(0, base.length() - 1)));

            match = base.substring(0, base.length() - 1);
            servlet = (ServletHandler)entry.getValue();
            userPath = key;
         }
      }
   }

   private static boolean isFilterApplicable(String path, String filterPath) {
      String modifiedPath;
      if (filterPath.equals("*")) {
         modifiedPath = "/*";
      } else {
         modifiedPath = filterPath;
      }

      if (path.isEmpty()) {
         return modifiedPath.equals("/*") || modifiedPath.equals("/");
      } else if (!modifiedPath.endsWith("/*")) {
         return modifiedPath.equals(path);
      } else {
         String baseFilterPath = modifiedPath.substring(0, modifiedPath.length() - 1);
         String exactFilterPath = modifiedPath.substring(0, modifiedPath.length() - 2);
         return path.startsWith(baseFilterPath) || path.equals(exactFilterPath);
      }
   }

   private static <K, V> void addToListMap(Map<K, List<V>> map, K key, V value) {
      List<V> list = (List)map.get(key);
      if (list == null) {
         map.put(key, list = new ArrayList());
      }

      ((List)list).add(value);
   }

   private static ServletChain servletChain(HttpHandler next, ManagedServlet managedServlet, Map<DispatcherType, List<ManagedFilter>> filters, String servletPath, DeploymentInfo deploymentInfo, boolean defaultServlet, MappingMatch mappingMatch, String pattern) {
      HttpHandler servletHandler = next;
      if (!deploymentInfo.isSecurityDisabled()) {
         servletHandler = new ServletSecurityRoleHandler(next, deploymentInfo.getAuthorizationManager());
      }

      HttpHandler servletHandler = wrapHandlers((HttpHandler)servletHandler, managedServlet.getServletInfo().getHandlerChainWrappers());
      return new ServletChain(servletHandler, managedServlet, servletPath, defaultServlet, mappingMatch, pattern, filters);
   }

   private static HttpHandler wrapHandlers(HttpHandler wrapee, List<HandlerWrapper> wrappers) {
      HttpHandler current = wrapee;

      HandlerWrapper wrapper;
      for(Iterator var3 = wrappers.iterator(); var3.hasNext(); current = wrapper.wrap(current)) {
         wrapper = (HandlerWrapper)var3.next();
      }

      return current;
   }

   private static class MatchData {
      final ServletHandler handler;
      final String matchedPath;
      final String userPath;
      final MappingMatch mappingMatch;
      final boolean defaultServlet;

      private MatchData(ServletHandler handler, String matchedPath, String userPath, MappingMatch mappingMatch, boolean defaultServlet) {
         this.handler = handler;
         this.matchedPath = matchedPath;
         this.userPath = userPath;
         this.mappingMatch = mappingMatch;
         this.defaultServlet = defaultServlet;
      }

      // $FF: synthetic method
      MatchData(ServletHandler x0, String x1, String x2, MappingMatch x3, boolean x4, Object x5) {
         this(x0, x1, x2, x3, x4);
      }
   }
}
