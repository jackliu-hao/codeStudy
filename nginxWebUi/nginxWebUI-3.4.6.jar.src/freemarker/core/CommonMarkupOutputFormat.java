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
/*     */ 
/*     */ 
/*     */ public abstract class CommonMarkupOutputFormat<MO extends CommonTemplateMarkupOutputModel>
/*     */   extends MarkupOutputFormat<MO>
/*     */ {
/*     */   public final MO fromPlainTextByEscaping(String textToEsc) throws TemplateModelException {
/*  41 */     return newTemplateMarkupOutputModel(textToEsc, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public final MO fromMarkup(String markupText) throws TemplateModelException {
/*  46 */     return newTemplateMarkupOutputModel(null, markupText);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void output(MO mo, Writer out) throws IOException, TemplateModelException {
/*  51 */     String mc = mo.getMarkupContent();
/*  52 */     if (mc != null) {
/*  53 */       out.write(mc);
/*     */     } else {
/*  55 */       output(mo.getPlainTextContent(), out);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getSourcePlainText(MO mo) throws TemplateModelException {
/*  64 */     return mo.getPlainTextContent();
/*     */   }
/*     */ 
/*     */   
/*     */   public final String getMarkupString(MO mo) throws TemplateModelException {
/*  69 */     String mc = mo.getMarkupContent();
/*  70 */     if (mc != null) {
/*  71 */       return mc;
/*     */     }
/*     */     
/*  74 */     mc = escapePlainText(mo.getPlainTextContent());
/*  75 */     mo.setMarkupContent(mc);
/*  76 */     return mc;
/*     */   }
/*     */ 
/*     */   
/*     */   public final MO concat(MO mo1, MO mo2) throws TemplateModelException {
/*  81 */     String pc1 = mo1.getPlainTextContent();
/*  82 */     String mc1 = mo1.getMarkupContent();
/*  83 */     String pc2 = mo2.getPlainTextContent();
/*  84 */     String mc2 = mo2.getMarkupContent();
/*     */     
/*  86 */     String pc3 = (pc1 != null && pc2 != null) ? (pc1 + pc2) : null;
/*  87 */     String mc3 = (mc1 != null && mc2 != null) ? (mc1 + mc2) : null;
/*  88 */     if (pc3 != null || mc3 != null) {
/*  89 */       return newTemplateMarkupOutputModel(pc3, mc3);
/*     */     }
/*     */     
/*  92 */     if (pc1 != null) {
/*  93 */       return newTemplateMarkupOutputModel((String)null, getMarkupString(mo1) + mc2);
/*     */     }
/*  95 */     return newTemplateMarkupOutputModel((String)null, mc1 + getMarkupString(mo2));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty(MO mo) throws TemplateModelException {
/* 101 */     String s = mo.getPlainTextContent();
/* 102 */     if (s != null) {
/* 103 */       return (s.length() == 0);
/*     */     }
/* 105 */     return (mo.getMarkupContent().length() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOutputFormatMixingAllowed() {
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAutoEscapedByDefault() {
/* 115 */     return true;
/*     */   }
/*     */   
/*     */   public abstract void output(String paramString, Writer paramWriter) throws IOException, TemplateModelException;
/*     */   
/*     */   protected abstract MO newTemplateMarkupOutputModel(String paramString1, String paramString2) throws TemplateModelException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\CommonMarkupOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */