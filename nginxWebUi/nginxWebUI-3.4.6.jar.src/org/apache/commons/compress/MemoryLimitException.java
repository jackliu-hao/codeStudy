/*    */ package org.apache.commons.compress;
/*    */ 
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
/*    */ public class MemoryLimitException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final long memoryNeededInKb;
/*    */   private final int memoryLimitInKb;
/*    */   
/*    */   public MemoryLimitException(long memoryNeededInKb, int memoryLimitInKb) {
/* 40 */     super(buildMessage(memoryNeededInKb, memoryLimitInKb));
/* 41 */     this.memoryNeededInKb = memoryNeededInKb;
/* 42 */     this.memoryLimitInKb = memoryLimitInKb;
/*    */   }
/*    */   
/*    */   public MemoryLimitException(long memoryNeededInKb, int memoryLimitInKb, Exception e) {
/* 46 */     super(buildMessage(memoryNeededInKb, memoryLimitInKb), e);
/* 47 */     this.memoryNeededInKb = memoryNeededInKb;
/* 48 */     this.memoryLimitInKb = memoryLimitInKb;
/*    */   }
/*    */   
/*    */   public long getMemoryNeededInKb() {
/* 52 */     return this.memoryNeededInKb;
/*    */   }
/*    */   
/*    */   public int getMemoryLimitInKb() {
/* 56 */     return this.memoryLimitInKb;
/*    */   }
/*    */   
/*    */   private static String buildMessage(long memoryNeededInKb, int memoryLimitInKb) {
/* 60 */     return memoryNeededInKb + " kb of memory would be needed; limit was " + memoryLimitInKb + " kb. If the file is not corrupt, consider increasing the memory limit.";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\MemoryLimitException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */