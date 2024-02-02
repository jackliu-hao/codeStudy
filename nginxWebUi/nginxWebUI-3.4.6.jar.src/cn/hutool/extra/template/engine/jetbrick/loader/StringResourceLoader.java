/*    */ package cn.hutool.extra.template.engine.jetbrick.loader;
/*    */ 
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.nio.charset.Charset;
/*    */ import jetbrick.io.resource.AbstractResource;
/*    */ import jetbrick.io.resource.Resource;
/*    */ import jetbrick.io.resource.ResourceNotFoundException;
/*    */ import jetbrick.template.loader.AbstractResourceLoader;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringResourceLoader
/*    */   extends AbstractResourceLoader
/*    */ {
/*    */   private Charset charset;
/*    */   
/*    */   public Resource load(String name) {
/* 26 */     return (Resource)new StringTemplateResource(name, this.charset);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCharset(Charset charset) {
/* 34 */     this.charset = charset;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static class StringTemplateResource
/*    */     extends AbstractResource
/*    */   {
/*    */     private final String content;
/*    */ 
/*    */ 
/*    */     
/*    */     private final Charset charset;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public StringTemplateResource(String content, Charset charset) {
/* 53 */       this.content = content;
/* 54 */       this.charset = charset;
/*    */     }
/*    */ 
/*    */     
/*    */     public InputStream openStream() throws ResourceNotFoundException {
/* 59 */       return IoUtil.toStream(this.content, this.charset);
/*    */     }
/*    */ 
/*    */     
/*    */     public URL getURL() {
/* 64 */       throw new UnsupportedOperationException();
/*    */     }
/*    */ 
/*    */     
/*    */     public boolean exist() {
/* 69 */       return StrUtil.isEmpty(this.content);
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 74 */       return this.content;
/*    */     }
/*    */ 
/*    */     
/*    */     public long lastModified() {
/* 79 */       return 1L;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\jetbrick\loader\StringResourceLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */