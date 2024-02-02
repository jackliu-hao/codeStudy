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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DumpArchiveConstants
/*    */ {
/*    */   public static final int TP_SIZE = 1024;
/*    */   public static final int NTREC = 10;
/*    */   public static final int HIGH_DENSITY_NTREC = 32;
/*    */   public static final int OFS_MAGIC = 60011;
/*    */   public static final int NFS_MAGIC = 60012;
/*    */   public static final int FS_UFS2_MAGIC = 424935705;
/*    */   public static final int CHECKSUM = 84446;
/*    */   public static final int LBLSIZE = 16;
/*    */   public static final int NAMELEN = 64;
/*    */   
/*    */   public enum SEGMENT_TYPE
/*    */   {
/* 43 */     TAPE(1),
/* 44 */     INODE(2),
/* 45 */     BITS(3),
/* 46 */     ADDR(4),
/* 47 */     END(5),
/* 48 */     CLRI(6);
/*    */     
/*    */     final int code;
/*    */     
/*    */     SEGMENT_TYPE(int code) {
/* 53 */       this.code = code;
/*    */     }
/*    */     
/*    */     public static SEGMENT_TYPE find(int code) {
/* 57 */       for (SEGMENT_TYPE t : values()) {
/* 58 */         if (t.code == code) {
/* 59 */           return t;
/*    */         }
/*    */       } 
/*    */       
/* 63 */       return null;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public enum COMPRESSION_TYPE
/*    */   {
/* 71 */     ZLIB(0),
/* 72 */     BZLIB(1),
/* 73 */     LZO(2);
/*    */     
/*    */     final int code;
/*    */     
/*    */     COMPRESSION_TYPE(int code) {
/* 78 */       this.code = code;
/*    */     }
/*    */     
/*    */     public static COMPRESSION_TYPE find(int code) {
/* 82 */       for (COMPRESSION_TYPE t : values()) {
/* 83 */         if (t.code == code) {
/* 84 */           return t;
/*    */         }
/*    */       } 
/*    */       
/* 88 */       return null;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\DumpArchiveConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */