/*    */ package org.apache.commons.compress.archivers.tar;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ public final class TarArchiveStructSparse
/*    */ {
/*    */   private final long offset;
/*    */   private final long numbytes;
/*    */   
/*    */   public TarArchiveStructSparse(long offset, long numbytes) {
/* 40 */     if (offset < 0L) {
/* 41 */       throw new IllegalArgumentException("offset must not be negative");
/*    */     }
/* 43 */     if (numbytes < 0L) {
/* 44 */       throw new IllegalArgumentException("numbytes must not be negative");
/*    */     }
/* 46 */     this.offset = offset;
/* 47 */     this.numbytes = numbytes;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 52 */     if (this == o) {
/* 53 */       return true;
/*    */     }
/* 55 */     if (o == null || getClass() != o.getClass()) {
/* 56 */       return false;
/*    */     }
/* 58 */     TarArchiveStructSparse that = (TarArchiveStructSparse)o;
/* 59 */     return (this.offset == that.offset && this.numbytes == that.numbytes);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 65 */     return Objects.hash(new Object[] { Long.valueOf(this.offset), Long.valueOf(this.numbytes) });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 70 */     return "TarArchiveStructSparse{offset=" + this.offset + ", numbytes=" + this.numbytes + '}';
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getOffset() {
/* 77 */     return this.offset;
/*    */   }
/*    */   
/*    */   public long getNumbytes() {
/* 81 */     return this.numbytes;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\tar\TarArchiveStructSparse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */