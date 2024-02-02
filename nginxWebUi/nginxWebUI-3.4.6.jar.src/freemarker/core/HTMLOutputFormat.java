/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.utility.StringUtil;
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
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
/*    */ public class HTMLOutputFormat
/*    */   extends CommonMarkupOutputFormat<TemplateHTMLOutputModel>
/*    */ {
/* 41 */   public static final HTMLOutputFormat INSTANCE = new HTMLOutputFormat();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 52 */     return "HTML";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMimeType() {
/* 57 */     return "text/html";
/*    */   }
/*    */ 
/*    */   
/*    */   public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
/* 62 */     StringUtil.XHTMLEnc(textToEsc, out);
/*    */   }
/*    */ 
/*    */   
/*    */   public String escapePlainText(String plainTextContent) {
/* 67 */     return StringUtil.XHTMLEnc(plainTextContent);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isLegacyBuiltInBypassed(String builtInName) {
/* 72 */     return (builtInName.equals("html") || builtInName.equals("xml") || builtInName.equals("xhtml"));
/*    */   }
/*    */ 
/*    */   
/*    */   protected TemplateHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
/* 77 */     return new TemplateHTMLOutputModel(plainTextContent, markupContent);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\HTMLOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */