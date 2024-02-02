/*    */ package org.antlr.v4.runtime.misc;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class Pair<A, B>
/*    */   implements Serializable
/*    */ {
/*    */   public final A a;
/*    */   public final B b;
/*    */   
/*    */   public Pair(A a, B b) {
/* 40 */     this.a = a;
/* 41 */     this.b = b;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 46 */     if (obj == this) {
/* 47 */       return true;
/*    */     }
/* 49 */     if (!(obj instanceof Pair)) {
/* 50 */       return false;
/*    */     }
/*    */     
/* 53 */     Pair<?, ?> other = (Pair<?, ?>)obj;
/* 54 */     return (ObjectEqualityComparator.INSTANCE.equals(this.a, other.a) && ObjectEqualityComparator.INSTANCE.equals(this.b, other.b));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 60 */     int hash = MurmurHash.initialize();
/* 61 */     hash = MurmurHash.update(hash, this.a);
/* 62 */     hash = MurmurHash.update(hash, this.b);
/* 63 */     return MurmurHash.finish(hash, 2);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return String.format("(%s, %s)", new Object[] { this.a, this.b });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\Pair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */