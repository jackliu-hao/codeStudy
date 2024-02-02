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
/*    */ 
/*    */ 
/*    */ class Dirent
/*    */ {
/*    */   private final int ino;
/*    */   private final int parentIno;
/*    */   private final int type;
/*    */   private final String name;
/*    */   
/*    */   Dirent(int ino, int parentIno, int type, String name) {
/* 39 */     this.ino = ino;
/* 40 */     this.parentIno = parentIno;
/* 41 */     this.type = type;
/* 42 */     this.name = name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   int getIno() {
/* 50 */     return this.ino;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   int getParentIno() {
/* 58 */     return this.parentIno;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   int getType() {
/* 66 */     return this.type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   String getName() {
/* 77 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 85 */     return String.format("[%d]: %s", new Object[] { Integer.valueOf(this.ino), this.name });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\Dirent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */