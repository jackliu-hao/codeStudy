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
/*    */ final class UncheckedParseException
/*    */   extends RuntimeException
/*    */ {
/*    */   private final ParseException parseException;
/*    */   
/*    */   public UncheckedParseException(ParseException parseException) {
/* 31 */     this.parseException = parseException;
/*    */   }
/*    */   
/*    */   public ParseException getParseException() {
/* 35 */     return this.parseException;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UncheckedParseException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */