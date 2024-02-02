/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.core.TemplateConfiguration;
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FirstMatchTemplateConfigurationFactory
/*     */   extends TemplateConfigurationFactory
/*     */ {
/*     */   private final TemplateConfigurationFactory[] templateConfigurationFactories;
/*     */   private boolean allowNoMatch;
/*     */   private String noMatchErrorDetails;
/*     */   
/*     */   public FirstMatchTemplateConfigurationFactory(TemplateConfigurationFactory... templateConfigurationFactories) {
/*  38 */     this.templateConfigurationFactories = templateConfigurationFactories;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
/*  44 */     for (TemplateConfigurationFactory tcf : this.templateConfigurationFactories) {
/*  45 */       TemplateConfiguration tc = tcf.get(sourceName, templateSource);
/*  46 */       if (tc != null) {
/*  47 */         return tc;
/*     */       }
/*     */     } 
/*  50 */     if (!this.allowNoMatch) {
/*  51 */       throw new TemplateConfigurationFactoryException(FirstMatchTemplateConfigurationFactory.class
/*  52 */           .getSimpleName() + " has found no matching choice for source name " + 
/*     */           
/*  54 */           StringUtil.jQuote(sourceName) + ". " + ((this.noMatchErrorDetails != null) ? ("Error details: " + this.noMatchErrorDetails) : "(Set the noMatchErrorDetails property of the factory bean to give a more specific error message. Set allowNoMatch to true if this shouldn't be an error.)"));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  60 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAllowNoMatch() {
/*  67 */     return this.allowNoMatch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowNoMatch(boolean allowNoMatch) {
/*  77 */     this.allowNoMatch = allowNoMatch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getNoMatchErrorDetails() {
/*  87 */     return this.noMatchErrorDetails;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNoMatchErrorDetails(String noMatchErrorDetails) {
/*  92 */     this.noMatchErrorDetails = noMatchErrorDetails;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FirstMatchTemplateConfigurationFactory allowNoMatch(boolean allow) {
/*  99 */     setAllowNoMatch(allow);
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FirstMatchTemplateConfigurationFactory noMatchErrorDetails(String message) {
/* 107 */     setNoMatchErrorDetails(message);
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setConfigurationOfChildren(Configuration cfg) {
/* 113 */     for (TemplateConfigurationFactory templateConfigurationFactory : this.templateConfigurationFactories)
/* 114 */       templateConfigurationFactory.setConfiguration(cfg); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\FirstMatchTemplateConfigurationFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */