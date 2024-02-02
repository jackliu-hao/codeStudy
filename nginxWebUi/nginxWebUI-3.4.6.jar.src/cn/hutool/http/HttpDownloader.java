/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.io.FastByteArrayOutputStream;
/*     */ import cn.hutool.core.io.StreamProgress;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import java.io.File;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class HttpDownloader
/*     */ {
/*     */   public static String downloadString(String url, Charset customCharset, StreamProgress streamPress) {
/*  28 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream();
/*  29 */     download(url, (OutputStream)out, true, streamPress);
/*  30 */     return (null == customCharset) ? out.toString() : out.toString(customCharset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] downloadBytes(String url) {
/*  40 */     return requestDownload(url, -1).bodyBytes();
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
/*     */   public static long downloadFile(String url, File targetFileOrDir, int timeout, StreamProgress streamProgress) {
/*  53 */     return requestDownload(url, timeout).writeBody(targetFileOrDir, streamProgress);
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
/*     */   public long downloadFile(String url, File targetFileOrDir, String tempFileSuffix, int timeout, StreamProgress streamProgress) {
/*  70 */     return requestDownload(url, timeout).writeBody(targetFileOrDir, tempFileSuffix, streamProgress);
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
/*     */   public static File downloadForFile(String url, File targetFileOrDir, int timeout, StreamProgress streamProgress) {
/*  83 */     return requestDownload(url, timeout).writeBodyForFile(targetFileOrDir, streamProgress);
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
/*     */   public static long download(String url, OutputStream out, boolean isCloseOut, StreamProgress streamProgress) {
/*  96 */     Assert.notNull(out, "[out] is null !", new Object[0]);
/*     */     
/*  98 */     return requestDownload(url, -1).writeBody(out, isCloseOut, streamProgress);
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
/*     */   private static HttpResponse requestDownload(String url, int timeout) {
/* 110 */     Assert.notBlank(url, "[url] is blank !", new Object[0]);
/*     */ 
/*     */ 
/*     */     
/* 114 */     HttpResponse response = HttpUtil.createGet(url, true).timeout(timeout).executeAsync();
/*     */     
/* 116 */     if (response.isOk()) {
/* 117 */       return response;
/*     */     }
/*     */     
/* 120 */     throw new HttpException("Server response error with status code: [{}]", new Object[] { Integer.valueOf(response.getStatus()) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpDownloader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */