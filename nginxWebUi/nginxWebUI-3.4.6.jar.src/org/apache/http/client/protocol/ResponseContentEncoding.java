/*     */ package org.apache.http.client.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.entity.DecompressingEntity;
/*     */ import org.apache.http.client.entity.DeflateInputStreamFactory;
/*     */ import org.apache.http.client.entity.GZIPInputStreamFactory;
/*     */ import org.apache.http.client.entity.InputStreamFactory;
/*     */ import org.apache.http.config.Lookup;
/*     */ import org.apache.http.config.RegistryBuilder;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class ResponseContentEncoding
/*     */   implements HttpResponseInterceptor
/*     */ {
/*     */   public static final String UNCOMPRESSED = "http.client.response.uncompressed";
/*     */   private final Lookup<InputStreamFactory> decoderRegistry;
/*     */   private final boolean ignoreUnknown;
/*     */   
/*     */   public ResponseContentEncoding(Lookup<InputStreamFactory> decoderRegistry, boolean ignoreUnknown) {
/*  70 */     this.decoderRegistry = (decoderRegistry != null) ? decoderRegistry : (Lookup<InputStreamFactory>)RegistryBuilder.create().register("gzip", GZIPInputStreamFactory.getInstance()).register("x-gzip", GZIPInputStreamFactory.getInstance()).register("deflate", DeflateInputStreamFactory.getInstance()).build();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     this.ignoreUnknown = ignoreUnknown;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResponseContentEncoding(boolean ignoreUnknown) {
/*  83 */     this(null, ignoreUnknown);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResponseContentEncoding(Lookup<InputStreamFactory> decoderRegistry) {
/*  90 */     this(decoderRegistry, true);
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
/*     */   public ResponseContentEncoding() {
/* 102 */     this((Lookup<InputStreamFactory>)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
/* 109 */     HttpEntity entity = response.getEntity();
/*     */     
/* 111 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/* 112 */     RequestConfig requestConfig = clientContext.getRequestConfig();
/*     */ 
/*     */     
/* 115 */     if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0L) {
/* 116 */       Header ceheader = entity.getContentEncoding();
/* 117 */       if (ceheader != null) {
/* 118 */         HeaderElement[] codecs = ceheader.getElements();
/* 119 */         for (HeaderElement codec : codecs) {
/* 120 */           String codecname = codec.getName().toLowerCase(Locale.ROOT);
/* 121 */           InputStreamFactory decoderFactory = (InputStreamFactory)this.decoderRegistry.lookup(codecname);
/* 122 */           if (decoderFactory != null) {
/* 123 */             response.setEntity((HttpEntity)new DecompressingEntity(response.getEntity(), decoderFactory));
/* 124 */             response.removeHeaders("Content-Length");
/* 125 */             response.removeHeaders("Content-Encoding");
/* 126 */             response.removeHeaders("Content-MD5");
/*     */           }
/* 128 */           else if (!"identity".equals(codecname) && !this.ignoreUnknown) {
/* 129 */             throw new HttpException("Unsupported Content-Encoding: " + codec.getName());
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\ResponseContentEncoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */