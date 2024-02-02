/*    */ package org.apache.commons.compress.archivers.sevenz;
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
/*    */ class StreamMap
/*    */ {
/*    */   int[] folderFirstPackStreamIndex;
/*    */   long[] packStreamOffsets;
/*    */   int[] folderFirstFileIndex;
/*    */   int[] fileFolderIndex;
/*    */   
/*    */   public String toString() {
/* 33 */     return "StreamMap with indices of " + this.folderFirstPackStreamIndex.length + " folders, offsets of " + this.packStreamOffsets.length + " packed streams, first files of " + this.folderFirstFileIndex.length + " folders and folder indices for " + this.fileFolderIndex.length + " files";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\StreamMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */