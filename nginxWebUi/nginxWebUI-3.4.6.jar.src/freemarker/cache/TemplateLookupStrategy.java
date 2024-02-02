/*     */ package freemarker.cache;
/*     */ 
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
/*     */ public abstract class TemplateLookupStrategy
/*     */ {
/*  80 */   public static final TemplateLookupStrategy DEFAULT_2_3_0 = new Default020300();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract TemplateLookupResult lookup(TemplateLookupContext paramTemplateLookupContext) throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Default020300
/*     */     extends TemplateLookupStrategy
/*     */   {
/*     */     private Default020300() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateLookupResult lookup(TemplateLookupContext ctx) throws IOException {
/* 105 */       return ctx.lookupWithLocalizedThenAcquisitionStrategy(ctx.getTemplateName(), ctx.getTemplateLocale());
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 110 */       return "TemplateLookupStrategy.DEFAULT_2_3_0";
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateLookupStrategy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */