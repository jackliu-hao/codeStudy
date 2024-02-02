/*    */ package freemarker.core;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
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
/*    */ public class _UnmodifiableCompositeSet<E>
/*    */   extends _UnmodifiableSet<E>
/*    */ {
/*    */   private final Set<E> set1;
/*    */   private final Set<E> set2;
/*    */   
/*    */   public _UnmodifiableCompositeSet(Set<E> set1, Set<E> set2) {
/* 31 */     this.set1 = set1;
/* 32 */     this.set2 = set2;
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<E> iterator() {
/* 37 */     return new CompositeIterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contains(Object o) {
/* 42 */     return (this.set1.contains(o) || this.set2.contains(o));
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 47 */     return this.set1.size() + this.set2.size();
/*    */   }
/*    */   
/*    */   private class CompositeIterator
/*    */     implements Iterator<E>
/*    */   {
/*    */     private Iterator<E> it1;
/*    */     private Iterator<E> it2;
/*    */     
/*    */     public boolean hasNext() {
/* 57 */       if (!this.it1Deplected) {
/* 58 */         if (this.it1 == null) {
/* 59 */           this.it1 = _UnmodifiableCompositeSet.this.set1.iterator();
/*    */         }
/* 61 */         if (this.it1.hasNext()) {
/* 62 */           return true;
/*    */         }
/*    */         
/* 65 */         this.it2 = _UnmodifiableCompositeSet.this.set2.iterator();
/* 66 */         this.it1 = null;
/* 67 */         this.it1Deplected = true;
/*    */       } 
/*    */       
/* 70 */       return this.it2.hasNext();
/*    */     }
/*    */     private boolean it1Deplected;
/*    */     private CompositeIterator() {}
/*    */     public E next() {
/* 75 */       if (!this.it1Deplected) {
/* 76 */         if (this.it1 == null) {
/* 77 */           this.it1 = _UnmodifiableCompositeSet.this.set1.iterator();
/*    */         }
/* 79 */         if (this.it1.hasNext()) {
/* 80 */           return this.it1.next();
/*    */         }
/*    */         
/* 83 */         this.it2 = _UnmodifiableCompositeSet.this.set2.iterator();
/* 84 */         this.it1 = null;
/* 85 */         this.it1Deplected = true;
/*    */       } 
/*    */       
/* 88 */       return this.it2.next();
/*    */     }
/*    */ 
/*    */     
/*    */     public void remove() {
/* 93 */       throw new UnsupportedOperationException();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_UnmodifiableCompositeSet.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */