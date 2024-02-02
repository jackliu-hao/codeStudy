/*    */ package cn.hutool.extra.template.engine.freemarker;
/*    */ 
/*    */ import freemarker.cache.TemplateLoader;
/*    */ import java.io.Reader;
/*    */ import java.io.StringReader;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleStringTemplateLoader
/*    */   implements TemplateLoader
/*    */ {
/*    */   public Object findTemplateSource(String name) {
/* 19 */     return name;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getLastModified(Object templateSource) {
/* 24 */     return System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   
/*    */   public Reader getReader(Object templateSource, String encoding) {
/* 29 */     return new StringReader((String)templateSource);
/*    */   }
/*    */   
/*    */   public void closeTemplateSource(Object templateSource) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\freemarker\SimpleStringTemplateLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */