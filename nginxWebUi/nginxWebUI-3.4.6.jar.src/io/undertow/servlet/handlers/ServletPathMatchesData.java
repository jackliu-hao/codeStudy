/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.SubstringMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ class ServletPathMatchesData
/*     */ {
/*     */   private final Map<String, ServletPathMatch> exactPathMatches;
/*     */   private final SubstringMap<PathMatch> prefixMatches;
/*     */   private final Map<String, ServletChain> nameMatches;
/*     */   
/*     */   ServletPathMatchesData(Map<String, ServletChain> exactPathMatches, SubstringMap<PathMatch> prefixMatches, Map<String, ServletChain> nameMatches) {
/*  42 */     this.prefixMatches = prefixMatches;
/*  43 */     this.nameMatches = nameMatches;
/*  44 */     Map<String, ServletPathMatch> newExactPathMatches = new HashMap<>();
/*  45 */     for (Map.Entry<String, ServletChain> entry : exactPathMatches.entrySet()) {
/*  46 */       newExactPathMatches.put(entry.getKey(), new ServletPathMatch(entry.getValue(), entry.getKey(), ((ServletChain)entry.getValue()).isDefaultServletMapping()));
/*     */     }
/*  48 */     this.exactPathMatches = newExactPathMatches;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletChain getServletHandlerByName(String name) {
/*  53 */     return this.nameMatches.get(name);
/*     */   }
/*     */   
/*     */   public ServletPathMatch getServletHandlerByExactPath(String path) {
/*  57 */     return this.exactPathMatches.get(path);
/*     */   }
/*     */   
/*     */   public ServletPathMatch getServletHandlerByPath(String path) {
/*  61 */     ServletPathMatch exact = this.exactPathMatches.get(path);
/*  62 */     if (exact != null) {
/*  63 */       return exact;
/*     */     }
/*  65 */     SubstringMap.SubstringMatch<PathMatch> match = this.prefixMatches.get(path, path.length());
/*  66 */     if (match != null) {
/*  67 */       return handleMatch(path, (PathMatch)match.getValue(), path.lastIndexOf('.'));
/*     */     }
/*  69 */     int extensionPos = -1;
/*  70 */     for (int i = path.length() - 1; i >= 0; i--) {
/*  71 */       char c = path.charAt(i);
/*  72 */       if (c == '/') {
/*  73 */         match = this.prefixMatches.get(path, i);
/*  74 */         if (match != null) {
/*  75 */           return handleMatch(path, (PathMatch)match.getValue(), extensionPos);
/*     */         }
/*  77 */       } else if (c == '.' && extensionPos == -1) {
/*  78 */         extensionPos = i;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  83 */     throw UndertowMessages.MESSAGES.servletPathMatchFailed();
/*     */   }
/*     */   
/*     */   private ServletPathMatch handleMatch(String path, PathMatch match, int extensionPos) {
/*  87 */     if (extensionPos == -1 || match.extensionMatches.isEmpty()) {
/*  88 */       return new ServletPathMatch(match.defaultHandler, path, match.requireWelcomeFileMatch);
/*     */     }
/*  90 */     String ext = path.substring(extensionPos + 1);
/*  91 */     ServletChain handler = (ServletChain)match.extensionMatches.get(ext);
/*  92 */     if (handler != null) {
/*  93 */       return new ServletPathMatch(handler, path, handler.getManagedServlet().getServletInfo().isRequireWelcomeFileMapping());
/*     */     }
/*  95 */     return new ServletPathMatch(match.defaultHandler, path, match.requireWelcomeFileMatch);
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/*  99 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static final class Builder
/*     */   {
/* 104 */     private final Map<String, ServletChain> exactPathMatches = new HashMap<>();
/*     */     
/* 106 */     private final SubstringMap<ServletPathMatchesData.PathMatch> prefixMatches = new SubstringMap();
/*     */     
/* 108 */     private final Map<String, ServletChain> nameMatches = new HashMap<>();
/*     */     
/*     */     public void addExactMatch(String exactMatch, ServletChain match) {
/* 111 */       this.exactPathMatches.put(exactMatch, match);
/*     */     }
/*     */     public void addPrefixMatch(String prefix, ServletChain match, boolean requireWelcomeFileMatch) {
/*     */       ServletPathMatchesData.PathMatch m;
/* 115 */       SubstringMap.SubstringMatch<ServletPathMatchesData.PathMatch> mt = this.prefixMatches.get(prefix);
/*     */       
/* 117 */       if (mt == null) {
/* 118 */         this.prefixMatches.put(prefix, m = new ServletPathMatchesData.PathMatch(match));
/*     */       } else {
/* 120 */         m = (ServletPathMatchesData.PathMatch)mt.getValue();
/*     */       } 
/* 122 */       m.defaultHandler = match;
/* 123 */       m.requireWelcomeFileMatch = requireWelcomeFileMatch;
/*     */     }
/*     */     public void addExtensionMatch(String prefix, String extension, ServletChain match) {
/*     */       ServletPathMatchesData.PathMatch m;
/* 127 */       SubstringMap.SubstringMatch<ServletPathMatchesData.PathMatch> mt = this.prefixMatches.get(prefix);
/*     */       
/* 129 */       if (mt == null) {
/* 130 */         this.prefixMatches.put(prefix, m = new ServletPathMatchesData.PathMatch(null));
/*     */       } else {
/* 132 */         m = (ServletPathMatchesData.PathMatch)mt.getValue();
/*     */       } 
/* 134 */       m.extensionMatches.put(extension, match);
/*     */     }
/*     */     
/*     */     public void addNameMatch(String name, ServletChain match) {
/* 138 */       this.nameMatches.put(name, match);
/*     */     }
/*     */     
/*     */     public ServletPathMatchesData build() {
/* 142 */       return new ServletPathMatchesData(this.exactPathMatches, this.prefixMatches, this.nameMatches);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class PathMatch
/*     */   {
/* 150 */     private final Map<String, ServletChain> extensionMatches = new HashMap<>();
/*     */     private volatile ServletChain defaultHandler;
/*     */     private volatile boolean requireWelcomeFileMatch;
/*     */     
/*     */     PathMatch(ServletChain defaultHandler) {
/* 155 */       this.defaultHandler = defaultHandler;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\ServletPathMatchesData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */