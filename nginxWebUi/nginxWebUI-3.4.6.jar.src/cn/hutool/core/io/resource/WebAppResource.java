/*    */ package cn.hutool.core.io.resource;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import java.io.File;
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
/*    */ public class WebAppResource
/*    */   extends FileResource
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public WebAppResource(String path) {
/* 22 */     super(new File(FileUtil.getWebRoot(), path));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\WebAppResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */