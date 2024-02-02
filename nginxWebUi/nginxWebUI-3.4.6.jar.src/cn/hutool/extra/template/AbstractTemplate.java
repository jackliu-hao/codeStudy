/*    */ package cn.hutool.extra.template;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.StringWriter;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractTemplate
/*    */   implements Template
/*    */ {
/*    */   public void render(Map<?, ?> bindingMap, File file) {
/* 21 */     BufferedOutputStream out = null;
/*    */     try {
/* 23 */       out = FileUtil.getOutputStream(file);
/* 24 */       render(bindingMap, out);
/*    */     } finally {
/* 26 */       IoUtil.close(out);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String render(Map<?, ?> bindingMap) {
/* 32 */     StringWriter writer = new StringWriter();
/* 33 */     render(bindingMap, writer);
/* 34 */     return writer.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\AbstractTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */