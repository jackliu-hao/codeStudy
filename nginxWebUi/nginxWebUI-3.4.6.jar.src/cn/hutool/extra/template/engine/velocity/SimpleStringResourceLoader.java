/*    */ package cn.hutool.extra.template.engine.velocity;
/*    */ 
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import java.io.InputStream;
/*    */ import java.io.Reader;
/*    */ import java.io.StringReader;
/*    */ import org.apache.velocity.exception.ResourceNotFoundException;
/*    */ import org.apache.velocity.runtime.resource.Resource;
/*    */ import org.apache.velocity.runtime.resource.loader.ResourceLoader;
/*    */ import org.apache.velocity.util.ExtProperties;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleStringResourceLoader
/*    */   extends ResourceLoader
/*    */ {
/*    */   public void init(ExtProperties configuration) {}
/*    */   
/*    */   public InputStream getResourceStream(String source) throws ResourceNotFoundException {
/* 36 */     return IoUtil.toStream(source, CharsetUtil.CHARSET_UTF_8);
/*    */   }
/*    */ 
/*    */   
/*    */   public Reader getResourceReader(String source, String encoding) throws ResourceNotFoundException {
/* 41 */     return new StringReader(source);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSourceModified(Resource resource) {
/* 46 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getLastModified(Resource resource) {
/* 51 */     return 0L;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\velocity\SimpleStringResourceLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */