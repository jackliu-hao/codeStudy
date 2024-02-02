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
/*    */ public class RTFOutputFormat
/*    */   extends CommonMarkupOutputFormat<TemplateRTFOutputModel>
/*    */ {
/* 41 */   public static final RTFOutputFormat INSTANCE = new RTFOutputFormat();
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
/* 52 */     return "RTF";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMimeType() {
/* 57 */     return "application/rtf";
/*    */   }
/*    */ 
/*    */   
/*    */   public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
/* 62 */     StringUtil.RTFEnc(textToEsc, out);
/*    */   }
/*    */ 
/*    */   
/*    */   public String escapePlainText(String plainTextContent) {
/* 67 */     return StringUtil.RTFEnc(plainTextContent);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isLegacyBuiltInBypassed(String builtInName) {
/* 72 */     return builtInName.equals("rtf");
/*    */   }
/*    */ 
/*    */   
/*    */   protected TemplateRTFOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
/* 77 */     return new TemplateRTFOutputModel(plainTextContent, markupContent);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\RTFOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */