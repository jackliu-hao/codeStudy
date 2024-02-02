/*    */ package freemarker.template.utility;
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
/*    */ public class UnrecognizedTimeZoneException
/*    */   extends Exception
/*    */ {
/*    */   private final String timeZoneName;
/*    */   
/*    */   public UnrecognizedTimeZoneException(String timeZoneName) {
/* 30 */     super("Unrecognized time zone: " + StringUtil.jQuote(timeZoneName));
/* 31 */     this.timeZoneName = timeZoneName;
/*    */   }
/*    */   
/*    */   public String getTimeZoneName() {
/* 35 */     return this.timeZoneName;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\UnrecognizedTimeZoneException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */