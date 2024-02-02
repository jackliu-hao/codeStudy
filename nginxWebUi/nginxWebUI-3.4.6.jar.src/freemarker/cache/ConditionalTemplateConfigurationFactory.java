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
/*    */ public class ConditionalTemplateConfigurationFactory
/*    */   extends TemplateConfigurationFactory
/*    */ {
/*    */   private final TemplateSourceMatcher matcher;
/*    */   private final TemplateConfiguration templateConfiguration;
/*    */   private final TemplateConfigurationFactory templateConfigurationFactory;
/*    */   
/*    */   public ConditionalTemplateConfigurationFactory(TemplateSourceMatcher matcher, TemplateConfigurationFactory templateConfigurationFactory) {
/* 40 */     this.matcher = matcher;
/* 41 */     this.templateConfiguration = null;
/* 42 */     this.templateConfigurationFactory = templateConfigurationFactory;
/*    */   }
/*    */ 
/*    */   
/*    */   public ConditionalTemplateConfigurationFactory(TemplateSourceMatcher matcher, TemplateConfiguration templateConfiguration) {
/* 47 */     this.matcher = matcher;
/* 48 */     this.templateConfiguration = templateConfiguration;
/* 49 */     this.templateConfigurationFactory = null;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
/* 55 */     if (this.matcher.matches(sourceName, templateSource)) {
/* 56 */       if (this.templateConfigurationFactory != null) {
/* 57 */         return this.templateConfigurationFactory.get(sourceName, templateSource);
/*    */       }
/* 59 */       return this.templateConfiguration;
/*    */     } 
/*    */     
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setConfigurationOfChildren(Configuration cfg) {
/* 68 */     if (this.templateConfiguration != null) {
/* 69 */       this.templateConfiguration.setParentConfiguration(cfg);
/*    */     }
/* 71 */     if (this.templateConfigurationFactory != null)
/* 72 */       this.templateConfigurationFactory.setConfiguration(cfg); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\ConditionalTemplateConfigurationFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */