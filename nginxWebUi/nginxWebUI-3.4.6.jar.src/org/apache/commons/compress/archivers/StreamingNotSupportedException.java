/*    */ package org.apache.commons.compress.archivers;
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
/*    */ public class StreamingNotSupportedException
/*    */   extends ArchiveException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final String format;
/*    */   
/*    */   public StreamingNotSupportedException(String format) {
/* 38 */     super("The " + format + " doesn't support streaming.");
/* 39 */     this.format = format;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getFormat() {
/* 48 */     return this.format;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\StreamingNotSupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */