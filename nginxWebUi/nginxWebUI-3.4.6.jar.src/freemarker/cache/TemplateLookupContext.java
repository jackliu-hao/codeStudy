/*     */ package freemarker.cache;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
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
/*     */ public abstract class TemplateLookupContext
/*     */ {
/*     */   private final String templateName;
/*     */   private final Locale templateLocale;
/*     */   private final Object customLookupCondition;
/*     */   
/*     */   public abstract TemplateLookupResult lookupWithAcquisitionStrategy(String paramString) throws IOException;
/*     */   
/*     */   public abstract TemplateLookupResult lookupWithLocalizedThenAcquisitionStrategy(String paramString, Locale paramLocale) throws IOException;
/*     */   
/*     */   TemplateLookupContext(String templateName, Locale templateLocale, Object customLookupCondition) {
/*  67 */     this.templateName = templateName;
/*  68 */     this.templateLocale = templateLocale;
/*  69 */     this.customLookupCondition = customLookupCondition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTemplateName() {
/*  76 */     return this.templateName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Locale getTemplateLocale() {
/*  84 */     return this.templateLocale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getCustomLookupCondition() {
/*  96 */     return this.customLookupCondition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateLookupResult createNegativeLookupResult() {
/* 105 */     return TemplateLookupResult.createNegativeResult();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateLookupContext.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */