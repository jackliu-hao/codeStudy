/*     */ package org.apache.http.client.utils;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.util.EntityUtils;
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
/*     */ public class HttpClientUtils
/*     */ {
/*     */   public static void closeQuietly(HttpResponse response) {
/*  71 */     if (response != null) {
/*  72 */       HttpEntity entity = response.getEntity();
/*  73 */       if (entity != null) {
/*     */         try {
/*  75 */           EntityUtils.consume(entity);
/*  76 */         } catch (IOException ex) {}
/*     */       }
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
/*     */   public static void closeQuietly(CloseableHttpResponse response) {
/* 105 */     if (response != null) {
/*     */       try {
/*     */         try {
/* 108 */           EntityUtils.consume(response.getEntity());
/*     */         } finally {
/* 110 */           response.close();
/*     */         } 
/* 112 */       } catch (IOException ignore) {}
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
/*     */   public static void closeQuietly(HttpClient httpClient) {
/* 139 */     if (httpClient != null && 
/* 140 */       httpClient instanceof Closeable)
/*     */       try {
/* 142 */         ((Closeable)httpClient).close();
/* 143 */       } catch (IOException ignore) {} 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\HttpClientUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */