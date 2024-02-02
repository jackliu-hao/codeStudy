/*    */ package freemarker.core;
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
/*    */ public final class UnknownDateTypeFormattingUnsupportedException
/*    */   extends UnformattableValueException
/*    */ {
/*    */   public UnknownDateTypeFormattingUnsupportedException() {
/* 32 */     super("Can't convert the date-like value to string because it isn't known if it's a date (no time part), time or date-time value.");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UnknownDateTypeFormattingUnsupportedException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */