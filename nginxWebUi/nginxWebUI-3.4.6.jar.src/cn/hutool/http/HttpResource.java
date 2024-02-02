/*    */ package cn.hutool.http;
/*    */ 
/*    */ import cn.hutool.core.io.resource.Resource;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.io.InputStream;
/*    */ import java.io.Serializable;
/*    */ import java.net.URL;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpResource
/*    */   implements Resource, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Resource resource;
/*    */   private final String contentType;
/*    */   
/*    */   public HttpResource(Resource resource, String contentType) {
/* 29 */     this.resource = (Resource)Assert.notNull(resource, "Resource must be not null !", new Object[0]);
/* 30 */     this.contentType = contentType;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 35 */     return this.resource.getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public URL getUrl() {
/* 40 */     return this.resource.getUrl();
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getStream() {
/* 45 */     return this.resource.getStream();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getContentType() {
/* 54 */     return this.contentType;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */