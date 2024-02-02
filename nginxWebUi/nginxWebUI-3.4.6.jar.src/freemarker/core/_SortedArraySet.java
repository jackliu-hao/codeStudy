/*    */ package freemarker.core;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
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
/*    */ public class _SortedArraySet<E>
/*    */   extends _UnmodifiableSet<E>
/*    */ {
/*    */   private final E[] array;
/*    */   
/*    */   public _SortedArraySet(E[] array) {
/* 32 */     this.array = array;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 37 */     return this.array.length;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contains(Object o) {
/* 42 */     return (Arrays.binarySearch((Object[])this.array, o) >= 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<E> iterator() {
/* 47 */     return new _ArrayIterator((Object[])this.array);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean add(E o) {
/* 52 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean remove(Object o) {
/* 57 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean addAll(Collection<? extends E> c) {
/* 62 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean removeAll(Collection<?> c) {
/* 67 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean retainAll(Collection<?> c) {
/* 72 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public void clear() {
/* 77 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_SortedArraySet.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */