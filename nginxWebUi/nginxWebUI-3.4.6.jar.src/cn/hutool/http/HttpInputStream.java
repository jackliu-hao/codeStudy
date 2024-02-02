/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.InflaterInputStream;
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
/*     */ public class HttpInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private InputStream in;
/*     */   
/*     */   public HttpInputStream(HttpResponse response) {
/*  30 */     init(response);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  35 */     return this.in.read();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  41 */     return this.in.read(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*  46 */     return this.in.skip(n);
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  51 */     return this.in.available();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  56 */     this.in.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void mark(int readlimit) {
/*  61 */     this.in.mark(readlimit);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void reset() throws IOException {
/*  66 */     this.in.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/*  71 */     return this.in.markSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(HttpResponse response) {
/*     */     try {
/*  81 */       this.in = (response.status < 400) ? response.httpConnection.getInputStream() : response.httpConnection.getErrorStream();
/*  82 */     } catch (IOException e) {
/*  83 */       if (false == e instanceof java.io.FileNotFoundException) {
/*  84 */         throw new HttpException(e);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  90 */     if (null == this.in) {
/*  91 */       this.in = new ByteArrayInputStream(StrUtil.format("Error request, response status: {}", new Object[] { Integer.valueOf(response.status) }).getBytes());
/*     */       
/*     */       return;
/*     */     } 
/*  95 */     if (response.isGzip() && false == response.in instanceof GZIPInputStream) {
/*     */       
/*     */       try {
/*  98 */         this.in = new GZIPInputStream(this.in);
/*  99 */       } catch (IOException iOException) {}
/*     */ 
/*     */     
/*     */     }
/* 103 */     else if (response.isDeflate() && false == this.in instanceof InflaterInputStream) {
/*     */       
/* 105 */       this.in = new InflaterInputStream(this.in, new Inflater(true));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */