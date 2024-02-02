/*     */ package io.undertow.server.handlers.resource;
/*     */ 
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.MimeMappings;
/*     */ import io.undertow.util.QValueParser;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Date;
/*     */ import java.util.List;
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
/*     */ public class PreCompressedResourceSupplier
/*     */   implements ResourceSupplier
/*     */ {
/*     */   private final ResourceManager resourceManager;
/*  49 */   private final Map<String, String> encodingMap = (Map<String, String>)new CopyOnWriteMap();
/*     */   
/*     */   public PreCompressedResourceSupplier(ResourceManager resourceManager) {
/*  52 */     this.resourceManager = resourceManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public Resource getResource(HttpServerExchange exchange, String path) throws IOException {
/*  57 */     if (exchange.getRequestHeaders().contains(Headers.RANGE))
/*     */     {
/*  59 */       return this.resourceManager.getResource(path);
/*     */     }
/*  61 */     Resource resource = getEncodedResource(exchange, path);
/*  62 */     if (resource == null) {
/*  63 */       return this.resourceManager.getResource(path);
/*     */     }
/*  65 */     return resource;
/*     */   }
/*     */ 
/*     */   
/*     */   private Resource getEncodedResource(HttpServerExchange exchange, String path) throws IOException {
/*  70 */     HeaderValues headerValues = exchange.getRequestHeaders().get(Headers.ACCEPT_ENCODING);
/*  71 */     if (headerValues == null || headerValues.isEmpty()) {
/*  72 */       return null;
/*     */     }
/*  74 */     List<List<QValueParser.QValueResult>> found = QValueParser.parse((List)headerValues);
/*  75 */     for (List<QValueParser.QValueResult> result : found) {
/*  76 */       for (QValueParser.QValueResult value : result) {
/*  77 */         final String extension = this.encodingMap.get(value.getValue());
/*  78 */         if (extension != null) {
/*  79 */           String newPath = path + extension;
/*  80 */           final Resource resource = this.resourceManager.getResource(newPath);
/*  81 */           if (resource != null && !resource.isDirectory()) {
/*  82 */             return new Resource()
/*     */               {
/*     */                 public String getPath() {
/*  85 */                   return resource.getPath();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Date getLastModified() {
/*  90 */                   return resource.getLastModified();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public String getLastModifiedString() {
/*  95 */                   return resource.getLastModifiedString();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public ETag getETag() {
/* 100 */                   return resource.getETag();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public String getName() {
/* 105 */                   return resource.getName();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public boolean isDirectory() {
/* 110 */                   return false;
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public List<Resource> list() {
/* 115 */                   return resource.list();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public String getContentType(MimeMappings mimeMappings) {
/* 120 */                   String fileName = resource.getName();
/* 121 */                   String originalFileName = fileName.substring(0, fileName.length() - extension.length());
/* 122 */                   int index = originalFileName.lastIndexOf('.');
/* 123 */                   if (index != -1 && index != originalFileName.length() - 1) {
/* 124 */                     return mimeMappings.getMimeType(originalFileName.substring(index + 1));
/*     */                   }
/* 126 */                   return null;
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public void serve(Sender sender, HttpServerExchange exchange, IoCallback completionCallback) {
/* 131 */                   exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, value.getValue());
/* 132 */                   resource.serve(sender, exchange, completionCallback);
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Long getContentLength() {
/* 137 */                   return resource.getContentLength();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public String getCacheKey() {
/* 142 */                   return resource.getCacheKey();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public File getFile() {
/* 147 */                   return resource.getFile();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Path getFilePath() {
/* 152 */                   return resource.getFilePath();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public File getResourceManagerRoot() {
/* 157 */                   return resource.getResourceManagerRoot();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Path getResourceManagerRootPath() {
/* 162 */                   return resource.getResourceManagerRootPath();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public URL getUrl() {
/* 167 */                   return resource.getUrl();
/*     */                 }
/*     */               };
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 174 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public PreCompressedResourceSupplier addEncoding(String encoding, String extension) {
/* 179 */     this.encodingMap.put(encoding, extension);
/* 180 */     return this;
/*     */   }
/*     */   
/*     */   public PreCompressedResourceSupplier removeEncoding(String encoding) {
/* 184 */     this.encodingMap.remove(encoding);
/* 185 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\PreCompressedResourceSupplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */