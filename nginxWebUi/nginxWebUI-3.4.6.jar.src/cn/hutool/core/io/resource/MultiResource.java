/*     */ package cn.hutool.core.io.resource;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collection;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class MultiResource
/*     */   implements Resource, Iterable<Resource>, Iterator<Resource>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final List<Resource> resources;
/*     */   private int cursor;
/*     */   
/*     */   public MultiResource(Resource... resources) {
/*  35 */     this(CollUtil.newArrayList((Object[])resources));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultiResource(Collection<Resource> resources) {
/*  44 */     if (resources instanceof List) {
/*  45 */       this.resources = (List<Resource>)resources;
/*     */     } else {
/*  47 */       this.resources = CollUtil.newArrayList(resources);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  53 */     return ((Resource)this.resources.get(this.cursor)).getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getUrl() {
/*  58 */     return ((Resource)this.resources.get(this.cursor)).getUrl();
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getStream() {
/*  63 */     return ((Resource)this.resources.get(this.cursor)).getStream();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isModified() {
/*  68 */     return ((Resource)this.resources.get(this.cursor)).isModified();
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedReader getReader(Charset charset) {
/*  73 */     return ((Resource)this.resources.get(this.cursor)).getReader(charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public String readStr(Charset charset) throws IORuntimeException {
/*  78 */     return ((Resource)this.resources.get(this.cursor)).readStr(charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public String readUtf8Str() throws IORuntimeException {
/*  83 */     return ((Resource)this.resources.get(this.cursor)).readUtf8Str();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] readBytes() throws IORuntimeException {
/*  88 */     return ((Resource)this.resources.get(this.cursor)).readBytes();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Resource> iterator() {
/*  93 */     return this.resources.iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  98 */     return (this.cursor < this.resources.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Resource next() {
/* 103 */     if (this.cursor >= this.resources.size()) {
/* 104 */       throw new ConcurrentModificationException();
/*     */     }
/* 106 */     this.cursor++;
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove() {
/* 112 */     this.resources.remove(this.cursor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void reset() {
/* 119 */     this.cursor = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultiResource add(Resource resource) {
/* 128 */     this.resources.add(resource);
/* 129 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\MultiResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */