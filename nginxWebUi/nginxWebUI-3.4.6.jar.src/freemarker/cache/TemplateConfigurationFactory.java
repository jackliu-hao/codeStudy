/*    */ package freemarker.cache;
/*    */ 
/*    */ import freemarker.core.TemplateConfiguration;
/*    */ import freemarker.template.Configuration;
/*    */ import java.io.IOException;
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
/*    */ public abstract class TemplateConfigurationFactory
/*    */ {
/*    */   private Configuration cfg;
/*    */   
/*    */   public abstract TemplateConfiguration get(String paramString, Object paramObject) throws IOException, TemplateConfigurationFactoryException;
/*    */   
/*    */   public final void setConfiguration(Configuration cfg) {
/* 63 */     if (this.cfg != null) {
/* 64 */       if (cfg != this.cfg) {
/* 65 */         throw new IllegalStateException("The TemplateConfigurationFactory is already bound to another Configuration");
/*    */       }
/*    */       
/*    */       return;
/*    */     } 
/* 70 */     this.cfg = cfg;
/* 71 */     setConfigurationOfChildren(cfg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Configuration getConfiguration() {
/* 80 */     return this.cfg;
/*    */   }
/*    */   
/*    */   protected abstract void setConfigurationOfChildren(Configuration paramConfiguration);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateConfigurationFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */