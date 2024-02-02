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
/*    */ 
/*    */ public final class UnknownDateTypeParsingUnsupportedException
/*    */   extends UnformattableValueException
/*    */ {
/*    */   public UnknownDateTypeParsingUnsupportedException() {
/* 33 */     super("Can't parse the string to date-like value because it isn't known if it's desired result should be a date (no time part), a time, or a date-time value.");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UnknownDateTypeParsingUnsupportedException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */