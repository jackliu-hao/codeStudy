/*    */ package org.apache.commons.compress.archivers.dump;
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
/*    */ public class InvalidFormatException
/*    */   extends DumpArchiveException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected long offset;
/*    */   
/*    */   public InvalidFormatException() {
/* 31 */     super("there was an error decoding a tape segment");
/*    */   }
/*    */   
/*    */   public InvalidFormatException(long offset) {
/* 35 */     super("there was an error decoding a tape segment header at offset " + offset + ".");
/*    */     
/* 37 */     this.offset = offset;
/*    */   }
/*    */   
/*    */   public long getOffset() {
/* 41 */     return this.offset;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\InvalidFormatException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */