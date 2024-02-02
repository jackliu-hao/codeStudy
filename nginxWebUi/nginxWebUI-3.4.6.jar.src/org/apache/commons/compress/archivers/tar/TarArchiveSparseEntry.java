/*    */ package org.apache.commons.compress.archivers.tar;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TarArchiveSparseEntry
/*    */   implements TarConstants
/*    */ {
/*    */   private final boolean isExtended;
/*    */   private final List<TarArchiveStructSparse> sparseHeaders;
/*    */   
/*    */   public TarArchiveSparseEntry(byte[] headerBuf) throws IOException {
/* 63 */     int offset = 0;
/* 64 */     this.sparseHeaders = new ArrayList<>(TarUtils.readSparseStructs(headerBuf, 0, 21));
/* 65 */     offset += 504;
/* 66 */     this.isExtended = TarUtils.parseBoolean(headerBuf, offset);
/*    */   }
/*    */   
/*    */   public boolean isExtended() {
/* 70 */     return this.isExtended;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<TarArchiveStructSparse> getSparseHeaders() {
/* 79 */     return this.sparseHeaders;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\tar\TarArchiveSparseEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */