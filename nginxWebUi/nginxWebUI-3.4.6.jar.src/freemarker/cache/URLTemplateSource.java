/*     */ package freemarker.cache;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
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
/*     */ class URLTemplateSource
/*     */ {
/*     */   private final URL url;
/*     */   private URLConnection conn;
/*     */   private InputStream inputStream;
/*     */   private Boolean useCaches;
/*     */   
/*     */   URLTemplateSource(URL url, Boolean useCaches) throws IOException {
/*  42 */     this.url = url;
/*  43 */     this.conn = url.openConnection();
/*  44 */     this.useCaches = useCaches;
/*  45 */     if (useCaches != null) {
/*  46 */       this.conn.setUseCaches(useCaches.booleanValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  52 */     if (o instanceof URLTemplateSource) {
/*  53 */       return this.url.equals(((URLTemplateSource)o).url);
/*     */     }
/*  55 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  61 */     return this.url.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  66 */     return this.url.toString();
/*     */   }
/*     */   
/*     */   long lastModified() {
/*  70 */     if (this.conn instanceof JarURLConnection) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  75 */       URL jarURL = ((JarURLConnection)this.conn).getJarFileURL();
/*  76 */       if (jarURL.getProtocol().equals("file"))
/*     */       {
/*  78 */         return (new File(jarURL.getFile())).lastModified();
/*     */       }
/*     */       
/*  81 */       URLConnection jarConn = null;
/*     */       try {
/*  83 */         jarConn = jarURL.openConnection();
/*  84 */         return jarConn.getLastModified();
/*  85 */       } catch (IOException e) {
/*  86 */         return -1L;
/*     */       } finally {
/*     */         try {
/*  89 */           if (jarConn != null) jarConn.getInputStream().close(); 
/*  90 */         } catch (IOException iOException) {}
/*     */       } 
/*     */     } 
/*     */     
/*  94 */     long lastModified = this.conn.getLastModified();
/*  95 */     if (lastModified == -1L && this.url.getProtocol().equals("file"))
/*     */     {
/*     */ 
/*     */       
/*  99 */       return (new File(this.url.getFile())).lastModified();
/*     */     }
/* 101 */     return lastModified;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   InputStream getInputStream() throws IOException {
/* 107 */     if (this.inputStream != null) {
/*     */ 
/*     */       
/*     */       try {
/* 111 */         this.inputStream.close();
/* 112 */       } catch (IOException iOException) {}
/*     */ 
/*     */       
/* 115 */       this.conn = this.url.openConnection();
/*     */     } 
/* 117 */     this.inputStream = this.conn.getInputStream();
/* 118 */     return this.inputStream;
/*     */   }
/*     */   
/*     */   void close() throws IOException {
/*     */     try {
/* 123 */       if (this.inputStream != null) {
/* 124 */         this.inputStream.close();
/*     */       } else {
/* 126 */         this.conn.getInputStream().close();
/*     */       } 
/*     */     } finally {
/* 129 */       this.inputStream = null;
/* 130 */       this.conn = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   Boolean getUseCaches() {
/* 135 */     return this.useCaches;
/*     */   }
/*     */   
/*     */   void setUseCaches(boolean useCaches) {
/* 139 */     if (this.conn != null) {
/* 140 */       this.conn.setUseCaches(useCaches);
/* 141 */       this.useCaches = Boolean.valueOf(useCaches);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\URLTemplateSource.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */