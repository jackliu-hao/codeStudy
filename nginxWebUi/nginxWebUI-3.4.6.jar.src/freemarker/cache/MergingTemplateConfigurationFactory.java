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
/*    */ public class MergingTemplateConfigurationFactory
/*    */   extends TemplateConfigurationFactory
/*    */ {
/*    */   private final TemplateConfigurationFactory[] templateConfigurationFactories;
/*    */   
/*    */   public MergingTemplateConfigurationFactory(TemplateConfigurationFactory... templateConfigurationFactories) {
/* 38 */     this.templateConfigurationFactories = templateConfigurationFactories;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
/* 44 */     TemplateConfiguration mergedTC = null;
/* 45 */     TemplateConfiguration resultTC = null;
/* 46 */     for (TemplateConfigurationFactory tcf : this.templateConfigurationFactories) {
/* 47 */       TemplateConfiguration tc = tcf.get(sourceName, templateSource);
/* 48 */       if (tc != null) {
/* 49 */         if (resultTC == null) {
/* 50 */           resultTC = tc;
/*    */         } else {
/* 52 */           if (mergedTC == null) {
/* 53 */             Configuration cfg = getConfiguration();
/* 54 */             if (cfg == null) {
/* 55 */               throw new IllegalStateException("The TemplateConfigurationFactory wasn't associated to a Configuration yet.");
/*    */             }
/*    */ 
/*    */             
/* 59 */             mergedTC = new TemplateConfiguration();
/* 60 */             mergedTC.setParentConfiguration(cfg);
/* 61 */             mergedTC.merge(resultTC);
/* 62 */             resultTC = mergedTC;
/*    */           } 
/* 64 */           mergedTC.merge(tc);
/*    */         } 
/*    */       }
/*    */     } 
/* 68 */     return resultTC;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void setConfigurationOfChildren(Configuration cfg) {
/* 73 */     for (TemplateConfigurationFactory templateConfigurationFactory : this.templateConfigurationFactories)
/* 74 */       templateConfigurationFactory.setConfiguration(cfg); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\MergingTemplateConfigurationFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */