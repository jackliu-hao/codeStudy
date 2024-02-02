/*    */ package org.apache.commons.compress.compressors.snappy;
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
/*    */ public enum FramedSnappyDialect
/*    */ {
/* 31 */   STANDARD(true, true),
/*    */ 
/*    */ 
/*    */   
/* 35 */   IWORK_ARCHIVE(false, false);
/*    */   
/*    */   private final boolean streamIdentifier;
/*    */   private final boolean checksumWithCompressedChunks;
/*    */   
/*    */   FramedSnappyDialect(boolean hasStreamIdentifier, boolean usesChecksumWithCompressedChunks) {
/* 41 */     this.streamIdentifier = hasStreamIdentifier;
/* 42 */     this.checksumWithCompressedChunks = usesChecksumWithCompressedChunks;
/*    */   }
/*    */   
/*    */   boolean hasStreamIdentifier() {
/* 46 */     return this.streamIdentifier;
/*    */   }
/*    */   
/*    */   boolean usesChecksumWithCompressedChunks() {
/* 50 */     return this.checksumWithCompressedChunks;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\snappy\FramedSnappyDialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */