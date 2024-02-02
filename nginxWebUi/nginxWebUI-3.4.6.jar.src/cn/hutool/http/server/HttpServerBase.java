/*    */ package cn.hutool.http.server;
/*    */ 
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import com.sun.net.httpserver.HttpContext;
/*    */ import com.sun.net.httpserver.HttpExchange;
/*    */ import java.io.Closeable;
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpServerBase
/*    */   implements Closeable
/*    */ {
/* 18 */   static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*    */ 
/*    */ 
/*    */   
/*    */   final HttpExchange httpExchange;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpServerBase(HttpExchange httpExchange) {
/* 28 */     this.httpExchange = httpExchange;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpExchange getHttpExchange() {
/* 37 */     return this.httpExchange;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpContext getHttpContext() {
/* 47 */     return getHttpExchange().getHttpContext();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {
/* 55 */     this.httpExchange.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\server\HttpServerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */