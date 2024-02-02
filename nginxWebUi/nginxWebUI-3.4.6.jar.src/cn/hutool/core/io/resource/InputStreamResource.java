/*    */ package cn.hutool.core.io.resource;
/*    */ 
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
/*    */ public class InputStreamResource
/*    */   implements Resource, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final InputStream in;
/*    */   private final String name;
/*    */   
/*    */   public InputStreamResource(InputStream in) {
/* 26 */     this(in, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputStreamResource(InputStream in, String name) {
/* 36 */     this.in = in;
/* 37 */     this.name = name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 42 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public URL getUrl() {
/* 47 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getStream() {
/* 52 */     return this.in;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\InputStreamResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */