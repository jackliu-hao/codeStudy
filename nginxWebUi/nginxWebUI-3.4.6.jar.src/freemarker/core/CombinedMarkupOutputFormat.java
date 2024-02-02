/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
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
/*     */ public final class CombinedMarkupOutputFormat
/*     */   extends CommonMarkupOutputFormat<TemplateCombinedMarkupOutputModel>
/*     */ {
/*     */   private final String name;
/*     */   private final MarkupOutputFormat outer;
/*     */   private final MarkupOutputFormat inner;
/*     */   
/*     */   public CombinedMarkupOutputFormat(MarkupOutputFormat outer, MarkupOutputFormat inner) {
/*  43 */     this(null, outer, inner);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CombinedMarkupOutputFormat(String name, MarkupOutputFormat outer, MarkupOutputFormat inner) {
/*  52 */     this.name = (name != null) ? null : (outer.getName() + "{" + inner.getName() + "}");
/*  53 */     this.outer = outer;
/*  54 */     this.inner = inner;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  59 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMimeType() {
/*  64 */     return this.outer.getMimeType();
/*     */   }
/*     */ 
/*     */   
/*     */   public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
/*  69 */     this.outer.output(this.inner.escapePlainText(textToEsc), out);
/*     */   }
/*     */ 
/*     */   
/*     */   public String escapePlainText(String plainTextContent) throws TemplateModelException {
/*  74 */     return this.outer.escapePlainText(this.inner.escapePlainText(plainTextContent));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLegacyBuiltInBypassed(String builtInName) throws TemplateModelException {
/*  79 */     return this.outer.isLegacyBuiltInBypassed(builtInName);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAutoEscapedByDefault() {
/*  84 */     return this.outer.isAutoEscapedByDefault();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOutputFormatMixingAllowed() {
/*  89 */     return this.outer.isOutputFormatMixingAllowed();
/*     */   }
/*     */   
/*     */   public MarkupOutputFormat getOuterOutputFormat() {
/*  93 */     return this.outer;
/*     */   }
/*     */   
/*     */   public MarkupOutputFormat getInnerOutputFormat() {
/*  97 */     return this.inner;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected TemplateCombinedMarkupOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
/* 103 */     return new TemplateCombinedMarkupOutputModel(plainTextContent, markupContent, this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\CombinedMarkupOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */