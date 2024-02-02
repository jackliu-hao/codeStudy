/*    */ package freemarker.template;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.Date;
/*    */ import java.util.List;
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
/*    */ public interface TemplateDateModel
/*    */   extends TemplateModel
/*    */ {
/*    */   public static final int UNKNOWN = 0;
/*    */   public static final int TIME = 1;
/*    */   public static final int DATE = 2;
/*    */   public static final int DATETIME = 3;
/* 59 */   public static final List TYPE_NAMES = Collections.unmodifiableList(
/* 60 */       Arrays.asList((Object[])new String[] { "UNKNOWN", "TIME", "DATE", "DATETIME" }));
/*    */   
/*    */   Date getAsDate() throws TemplateModelException;
/*    */   
/*    */   int getDateType();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateDateModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */