/*     */ package javax.activation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
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
/*     */ public class URLDataSource
/*     */   implements DataSource
/*     */ {
/*  48 */   private URL url = null;
/*  49 */   private URLConnection url_conn = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URLDataSource(URL url) {
/*  59 */     this.url = url;
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
/*     */   public String getContentType() {
/*  74 */     String type = null;
/*     */     
/*     */     try {
/*  77 */       if (this.url_conn == null)
/*  78 */         this.url_conn = this.url.openConnection(); 
/*  79 */     } catch (IOException e) {}
/*     */     
/*  81 */     if (this.url_conn != null) {
/*  82 */       type = this.url_conn.getContentType();
/*     */     }
/*  84 */     if (type == null) {
/*  85 */       type = "application/octet-stream";
/*     */     }
/*  87 */     return type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  97 */     return this.url.getFile();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() throws IOException {
/* 107 */     return this.url.openStream();
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
/*     */   public OutputStream getOutputStream() throws IOException {
/* 120 */     this.url_conn = this.url.openConnection();
/*     */     
/* 122 */     if (this.url_conn != null) {
/* 123 */       this.url_conn.setDoOutput(true);
/* 124 */       return this.url_conn.getOutputStream();
/*     */     } 
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL getURL() {
/* 135 */     return this.url;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\URLDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */