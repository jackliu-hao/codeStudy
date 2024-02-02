/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import org.apache.commons.compress.parallel.InputStreamSupplier;
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
/*    */ public class ZipArchiveEntryRequest
/*    */ {
/*    */   private final ZipArchiveEntry zipArchiveEntry;
/*    */   private final InputStreamSupplier payloadSupplier;
/*    */   private final int method;
/*    */   
/*    */   private ZipArchiveEntryRequest(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier payloadSupplier) {
/* 42 */     this.zipArchiveEntry = zipArchiveEntry;
/* 43 */     this.payloadSupplier = payloadSupplier;
/* 44 */     this.method = zipArchiveEntry.getMethod();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ZipArchiveEntryRequest createZipArchiveEntryRequest(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier payloadSupplier) {
/* 54 */     return new ZipArchiveEntryRequest(zipArchiveEntry, payloadSupplier);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputStream getPayloadStream() {
/* 62 */     return this.payloadSupplier.get();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getMethod() {
/* 70 */     return this.method;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   ZipArchiveEntry getZipArchiveEntry() {
/* 79 */     return this.zipArchiveEntry;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipArchiveEntryRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */