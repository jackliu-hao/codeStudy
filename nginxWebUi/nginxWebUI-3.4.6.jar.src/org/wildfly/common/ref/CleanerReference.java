/*    */ package org.wildfly.common.ref;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.Set;
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ public class CleanerReference<T, A>
/*    */   extends PhantomReference<T, A>
/*    */ {
/* 31 */   private static final Set<CleanerReference<?, ?>> set = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CleanerReference(T referent, A attachment, Reaper<T, A> reaper) {
/* 41 */     super(referent, attachment, reaper);
/* 42 */     set.add(this);
/*    */   }
/*    */   
/*    */   void clean() {
/* 46 */     set.remove(this);
/*    */   }
/*    */   
/*    */   public final int hashCode() {
/* 50 */     return super.hashCode();
/*    */   }
/*    */   
/*    */   public final boolean equals(Object obj) {
/* 54 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\CleanerReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */