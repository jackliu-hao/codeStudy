/*    */ package org.apache.commons.compress.archivers.sevenz;
/*    */ 
/*    */ import java.util.BitSet;
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
/*    */ class Archive
/*    */ {
/*    */   long packPos;
/* 26 */   long[] packSizes = new long[0];
/*    */   
/*    */   BitSet packCrcsDefined;
/*    */   
/*    */   long[] packCrcs;
/*    */   
/* 32 */   Folder[] folders = Folder.EMPTY_FOLDER_ARRAY;
/*    */   
/*    */   SubStreamsInfo subStreamsInfo;
/*    */   
/* 36 */   SevenZArchiveEntry[] files = SevenZArchiveEntry.EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY;
/*    */   
/*    */   StreamMap streamMap;
/*    */ 
/*    */   
/*    */   public String toString() {
/* 42 */     return "Archive with packed streams starting at offset " + this.packPos + ", " + 
/* 43 */       lengthOf(this.packSizes) + " pack sizes, " + lengthOf(this.packCrcs) + " CRCs, " + 
/* 44 */       lengthOf((Object[])this.folders) + " folders, " + lengthOf((Object[])this.files) + " files and " + this.streamMap;
/*    */   }
/*    */ 
/*    */   
/*    */   private static String lengthOf(long[] a) {
/* 49 */     return (a == null) ? "(null)" : String.valueOf(a.length);
/*    */   }
/*    */   
/*    */   private static String lengthOf(Object[] a) {
/* 53 */     return (a == null) ? "(null)" : String.valueOf(a.length);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\Archive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */