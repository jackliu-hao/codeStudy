/*    */ package cn.hutool.core.io.resource;
/*    */ 
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import java.nio.charset.Charset;
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
/*    */ public class StringResource
/*    */   extends CharSequenceResource
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public StringResource(String data) {
/* 24 */     super(data, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringResource(String data, String name) {
/* 34 */     super(data, name, CharsetUtil.CHARSET_UTF_8);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringResource(String data, String name, Charset charset) {
/* 45 */     super(data, name, charset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\StringResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */