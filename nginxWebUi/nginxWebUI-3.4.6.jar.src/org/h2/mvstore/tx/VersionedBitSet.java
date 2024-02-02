/*    */ package org.h2.mvstore.tx;
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
/*    */ final class VersionedBitSet
/*    */   extends BitSet
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private long version;
/*    */   
/*    */   public long getVersion() {
/* 22 */     return this.version;
/*    */   }
/*    */   
/*    */   public void setVersion(long paramLong) {
/* 26 */     this.version = paramLong;
/*    */   }
/*    */ 
/*    */   
/*    */   public VersionedBitSet clone() {
/* 31 */     return (VersionedBitSet)super.clone();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\VersionedBitSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */