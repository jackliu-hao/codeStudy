/*    */ package freemarker.template;
/*    */ 
/*    */ import freemarker.template.utility.StringUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MalformedTemplateNameException
/*    */   extends IOException
/*    */ {
/*    */   private final String templateName;
/*    */   private final String malformednessDescription;
/*    */   
/*    */   public MalformedTemplateNameException(String templateName, String malformednessDescription) {
/* 43 */     super("Malformed template name, " + StringUtil.jQuote(templateName) + ": " + malformednessDescription);
/* 44 */     this.templateName = templateName;
/* 45 */     this.malformednessDescription = malformednessDescription;
/*    */   }
/*    */   
/*    */   public String getTemplateName() {
/* 49 */     return this.templateName;
/*    */   }
/*    */   
/*    */   public String getMalformednessDescription() {
/* 53 */     return this.malformednessDescription;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\MalformedTemplateNameException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */