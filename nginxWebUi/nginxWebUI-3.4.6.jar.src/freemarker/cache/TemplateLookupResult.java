/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.template.utility.NullArgumentException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TemplateLookupResult
/*     */ {
/*     */   static TemplateLookupResult createNegativeResult() {
/*  36 */     return NegativeTemplateLookupResult.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   static TemplateLookupResult from(String templateSourceName, Object templateSource) {
/*  41 */     return (templateSource != null) ? new PositiveTemplateLookupResult(templateSourceName, templateSource) : 
/*     */       
/*  43 */       createNegativeResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemplateLookupResult() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getTemplateSourceName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isPositive();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract Object getTemplateSource();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class PositiveTemplateLookupResult
/*     */     extends TemplateLookupResult
/*     */   {
/*     */     private final String templateSourceName;
/*     */ 
/*     */ 
/*     */     
/*     */     private final Object templateSource;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private PositiveTemplateLookupResult(String templateSourceName, Object templateSource) {
/*  85 */       NullArgumentException.check("templateName", templateSourceName);
/*  86 */       NullArgumentException.check("templateSource", templateSource);
/*     */       
/*  88 */       if (templateSource instanceof TemplateLookupResult) {
/*  89 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/*  92 */       this.templateSourceName = templateSourceName;
/*  93 */       this.templateSource = templateSource;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getTemplateSourceName() {
/*  98 */       return this.templateSourceName;
/*     */     }
/*     */ 
/*     */     
/*     */     Object getTemplateSource() {
/* 103 */       return this.templateSource;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isPositive() {
/* 108 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class NegativeTemplateLookupResult
/*     */     extends TemplateLookupResult {
/* 114 */     private static final NegativeTemplateLookupResult INSTANCE = new NegativeTemplateLookupResult();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getTemplateSourceName() {
/* 122 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     Object getTemplateSource() {
/* 127 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isPositive() {
/* 132 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateLookupResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */