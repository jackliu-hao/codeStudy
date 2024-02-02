/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.Configuration;
/*    */ import freemarker.template.Version;
/*    */ import freemarker.template.utility.DateUtil;
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
/*    */ @Deprecated
/*    */ public class CommandLine
/*    */ {
/*    */   public static void main(String[] args) {
/* 36 */     Version ver = Configuration.getVersion();
/*    */     
/* 38 */     System.out.println();
/* 39 */     System.out.print("Apache FreeMarker version ");
/* 40 */     System.out.print(ver);
/*    */ 
/*    */     
/* 43 */     if (!ver.toString().endsWith("Z") && ver
/* 44 */       .getBuildDate() != null) {
/* 45 */       System.out.print(" (built on ");
/* 46 */       System.out.print(DateUtil.dateToISO8601String(ver
/* 47 */             .getBuildDate(), true, true, true, 6, DateUtil.UTC, (DateUtil.DateToISO8601CalendarFactory)new DateUtil.TrivialDateToISO8601CalendarFactory()));
/*    */ 
/*    */ 
/*    */       
/* 51 */       System.out.print(")");
/*    */     } 
/* 53 */     System.out.println();
/*    */     
/* 55 */     if (ver.isGAECompliant() != null) {
/* 56 */       System.out.print("Google App Engine complian variant: ");
/* 57 */       System.out.println(ver.isGAECompliant().booleanValue() ? "Yes" : "No");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\CommandLine.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */