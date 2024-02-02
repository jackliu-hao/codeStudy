/*    */ package org.antlr.v4.runtime.misc;
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
/*    */ public class Triple<A, B, C>
/*    */ {
/*    */   public final A a;
/*    */   public final B b;
/*    */   public final C c;
/*    */   
/*    */   public Triple(A a, B b, C c) {
/* 39 */     this.a = a;
/* 40 */     this.b = b;
/* 41 */     this.c = c;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 46 */     if (obj == this) {
/* 47 */       return true;
/*    */     }
/* 49 */     if (!(obj instanceof Triple)) {
/* 50 */       return false;
/*    */     }
/*    */     
/* 53 */     Triple<?, ?, ?> other = (Triple<?, ?, ?>)obj;
/* 54 */     return (ObjectEqualityComparator.INSTANCE.equals(this.a, other.a) && ObjectEqualityComparator.INSTANCE.equals(this.b, other.b) && ObjectEqualityComparator.INSTANCE.equals(this.c, other.c));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     int hash = MurmurHash.initialize();
/* 62 */     hash = MurmurHash.update(hash, this.a);
/* 63 */     hash = MurmurHash.update(hash, this.b);
/* 64 */     hash = MurmurHash.update(hash, this.c);
/* 65 */     return MurmurHash.finish(hash, 3);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 70 */     return String.format("(%s, %s, %s)", new Object[] { this.a, this.b, this.c });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\Triple.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */